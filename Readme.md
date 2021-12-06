# 파일럿 프로젝트 피드백

## 1) JwtAccessDeniedHandler, JwtAuthenticationEntryPoint 라는 네이밍이 적절한가?
- 만약 새로운 Security 필터가 추가되었을때 추가된 필터에서 AccessDeniedHandler 로 JWT와 관련되지 않은 이유로 보내게 될 수 도 있지 않은가? 또한 WebSecurityConfigurerAdapter 를 여러 개 상속 받아서 다른 이유로 AccessDeniedHandler를 처리해야 한다면 이 네이밍이 적절하다고 볼 수 있는가?
- AccessDeniedHandler: 권한과 관련된 403(Forbidden) 에 대한 처리를 위한 핸들러
- AuthenticationEntryPoint: 스프링 시큐리티 컨텍스트 내의 인증과정에서 실패하거나 인증헤더(Authorization)를 보내지 않게 되는 경우 401(Unauthorized) 를 처리하는 EntryPoint
- <b>Spring Security에 대해서 더 추가적인 학습 필요</b>

## 2) 메소드 리턴 값으로 null을 리턴하는 것보단 빈 공백을 리턴한다던지 항상 NPE에 안전한 방식으로 구현하는게 좋다.

- 기존 코드는 아래와 같이 요청 헤더에 AUTHORIZATION_HEADER 값이 없거나 BEARER_PREFIX로 시작하지 않게 된다면 null을 리턴하게 된다.  

```
private String resolveToken(HttpServletRequest request) {
    String token = request.getHeader(TokenProvider.AUTHORIZATION_HEADER);
    if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
        return token.substring(7);
    }
    return null;
}
```

- 하지만 이렇게 null을 리턴하는 것보단 NPE에 안전하게 빈 공백을 리턴하거나 Optional을 활용하도록 습관을 가지는게 좋다.

~~~
private String resolveToken(HttpServletRequest request) {
    String token = request.getHeader(TokenProvider.AUTHORIZATION_HEADER);
    if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
        return token.substring(7);
    }
    return "";
}
~~~

## 3) RefreshTokenService에 @Transactional 어노테이션을 안붙여도 정상적으로 트랜잭션 처리되지만 명시해주는게 좋다.
- [Spring Redis Template Transaction](https://gompangs.tistory.com/141#Trsnaction)

## 4) 연관관계상 중간 엔티티(TodoWorkspsace, Participant)의 생성 메서드를 서비스 레이어에서 사용해서 중간 엔티티를 생성하는건 적절치 않다. 문제가 발생할 오지가 크고 서비스 레이어의 흐름상에도 적절치 않다.

- 기존 코드는 TodoService의 기본 Todo 를 저장하는 메소드는 아래와 같다.

~~~
@Transactional
public Long saveBasicTodo(BasicTodoSaveRequestDto rq) {
    Member member = memberRepository.findById(rq.getMemberId())
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    SecurityUtil.checkAuthority(member.getAccountId());

    Todo parentTodo = getParentTodo(rq.getParentId());

    Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
            .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

    TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);

    Todo todo = TodoFactory.createTodo(member, todoWorkspace, parentTodo, rq);
    todoRepository.save(todo);

    return todo.getId();
}
~~~

- TodoWorkspace의 생성 메서드를 서비스 레이어에서 호출하여 객체를 생성하는 모습을 볼 수 있다.
- 하지만 이렇게 연관관계상 중간 엔티티의 생성 메서드를 서비스 레이어에서 사용하게 되면 문제가 발생할 여지가 크다.
  - 무분별하게 중간엔티티를 생성함으로써 데이터 정합성에 문제가 생길 우려가 크다.
- 또한 로직의 흐름상에도 맞지 않는다.
  - 예를 들어, 로직의 흐름상으로도 Todo를 생성 할 때 Todo에 Workspace를 할당하는게 적절하지 TodoWorkspace를 생성하여 Todo를 이에 할당하는 것은 적절치 않다.
- 이러한 문제들로 인해 아래와 같이 코드가 수정될 수 있다.

- TodoService 클래스

~~~
@Transactional
public Long saveBasicTodo(BasicTodoSaveRequestDto rq) {
    Member member = memberRepository.findById(rq.getMemberId())
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    SecurityUtil.checkAuthority(member.getAccountId());

    Todo parentTodo = getParentTodo(rq.getParentId());

    Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
            .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

    Todo todo = TodoFactory.createTodo(member, workspace, parentTodo, rq);
    todoRepository.save(todo);

    return todo.getId();
}
~~~

- TodoFactory 클래스

~~~
public class TodoFactory {

    public static Todo createTodo(Member member, Workspace workspace, Todo parent, TodoSaveRequestDto request) {
        Todo todo = null;
        if (request instanceof BasicTodoSaveRequestDto) {
            BasicTodoSaveRequestDto basicTodoRequest = (BasicTodoSaveRequestDto) request;
            todo = BasicTodo.createBasicTodo(member, workspace, basicTodoRequest.getContent(), parent, basicTodoRequest.getExpectedTime());
        }

        return todo;
    }
}
~~~

- BasicTodo 클래스

~~~
public class BasicTodo extends Todo {
    @Column(nullable = false)
    private int expectedTime;

    @Builder
    public BasicTodo(Member member, Workspace workspace, String content, Todo parent, TodoStatus status, int expectedTime) {
        super(member, workspace, content, parent, status);
        this.expectedTime = expectedTime;
    }

    //== 생성 메서드 ==//
    public static BasicTodo createBasicTodo(Member member, Workspace workspace, String content, Todo parent, int expectedTime) {
        return BasicTodo.builder()
                .member(member)
                .workspace(workspace)
                .content(content)
                .parent(parent)
                .status(TodoStatus.UNCOMPLETED)
                .expectedTime(expectedTime)
                .build();
    }
}
~~~

- Todo 클래스

~~~
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public abstract class Todo extends BaseEntity {

    ...

    public Todo(Member member, Workspace workspace, String content, Todo parent, TodoStatus status) {
        this.member = member;
        this.content = content;
        this.parent = parent;
        this.status = status;

        addTodoWorkspace(workspace);
    }

    //== 연관관계 메서드 ==//
    public void addTodoWorkspace(Workspace workspace) {
        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);
        todoWorkspaceGroup.addTodoWorkspace(todoWorkspace);
        todoWorkspace.setTodo(this);
    }
    ...
}
~~~

- 중간 엔티티 TodoWorkspace의 생성 역할을 Todo 내부에서 가짐으로써 좀 더 안전하게 중간 엔티티를 생성할 수 있다.
- <b>중간 엔티티의 생성 메서드가 존재하는게 문제가 아니라 서비스레이어에서 중간 엔티티를 생성하는게 문제이다.</b>

## 5) 실제 요청한 member에 대한 유효성 검사와 권한 체크는 엄연히 다르다. 권한 체크는 이미 필터단에서 처리되었을 것이고 SecurityUtil클래스의 checkAuthority 메서드는 유효성 검사로 봐야되기에 네이밍을 변경해주는게 좋다.

BasicTodo를 등록하는 saveBasicTodo 메서드를 보자.

~~~
@Transactional
public Long saveBasicTodo(BasicTodoSaveRequestDto rq) {
    Member member = memberRepository.findById(rq.getMemberId())
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    SecurityUtil.checkAuthority(member.getAccountId());

    Todo parentTodo = getParentTodo(rq.getParentId());

    Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
            .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

    Todo todo = TodoFactory.createTodo(member, workspace, parentTodo, rq);
    todoRepository.save(todo);

    return todo.getId();
}
~~~

`SecurityUtil.checkAuthority(member.getAccountId());` 메서드로 실제 access 토큰에 있는 계정 id와 request로 받은 member의 계정 id가 일치하는지 비교하고 있다. 하지만 이는 권한 체크로 볼 수 없다.

권한 체크는 이미 필터단에서 처리가 되었을 것이고 단순하게 유효한 사용자의 요청인지 검증하는 것이므로 네이밍을 `SecurityUtil.checkValidRequest()` 처럼 변경해주게 더 적절하다.

## 6) Refresh 토큰 저장소를 꼭 Redis가 아닌 RDB로 해도 문제 없다.
- Refresh 토큰을 저장소로 RDB를 사용하게 된다면 주기적인 배치 작업을 통해 Refresh 토큰 값을 삭제해주는 작업이 필요하다고 생각하여 Redis를 사용하여 데이터 저장시마다 유효 시간을 정하였다.
- 하지만 배치 작업을 통해 데이터를 삭제하는 과정은 꼭 필요한 작업만은 아니였다.
- 다음과 같은 과정으로 RDB를 사용할 수도 있다.
  - 1)RDB에서 RefreshToken 값을 저장하고 reissue가 들어올때 RDB로부터 조회한다.
  - 2)조회한 Refresh 토큰이 클라이언트로부터 받은 토큰 값과 일치하고 유효하다면 토큰을 재발행해서 RDB의 refresh token 값을 갱신해준다.
- 만약 클라이언트가 유효시간이 만료된 토큰을 보냈을 경우 토큰을 파싱해서 유효시간을 검증함으로써 시간이 만료됨을 서버에서 알아차리고 처리 할 수 있다.

## 7) Repository 인터페이스의 실제 구현체는 SimpleJpaRepository 클래스인데 기본적으로 @Transactional(readOnly = true)가 설정되있으니 참고하자.

## 8) 페치 조인을 사용할 때 나중에 테이블의 로우가 100만건일때 괜찮을까? 에 대해 한 번쯤 꼭 생각해보자.

- 프로젝트 요구사항으로 작업 공간에 속한 사람만이 다른 참가자를 삭제할 수 있다고 스스로 제한하였다.
- 그래서 다음과 같이 Todo를 조회할 때 Member, TodoWorkspaceGroup, 하위 Todo들을 페치조인으로 전부 가져와 Refresh 토큰으로부터 얻은 계정이 해당 작업 공간에 속하는지 검증하는 로직을 실행하였다.

~~~
@Transactional
public void delete(Long todoId) {
    if (!todoRepository.existsById(todoId)) {
        throw new CustomException(TODO_NOT_FOUND);
    }

    Todo todo = todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(todoId);

    SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

    if(todo.childsSize() > 0) {
        todo.clearChilds();
    }

    todoRepository.deleteById(todoId);
}
~~~

- 이는 5 개의 테이블을 조인하며 쿼리를 실행하게 되는데 만약 조인으로 뻥튀기된 테이블의 로우가 100만건이라면..?
- 이처럼 항상 확장적인 생각을 할 수 있어야 한다.

## 9) 유효한 멤버인지 검증하는 로직을 컨트롤러에 빼도 좋다.

~~~
@Transactional
public Long saveBasicTodo(BasicTodoSaveRequestDto rq) {
    Member member = memberRepository.findById(rq.getMemberId())
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

    SecurityUtil.checkAuthority(member.getAccountId()); // 유효한 요청인지 검증하는 메서드

    Todo parentTodo = getParentTodo(rq.getParentId());

    Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
            .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

    Todo todo = TodoFactory.createTodo(member, workspace, parentTodo, rq);
    todoRepository.save(todo);

    return todo.getId();
}
~~~

- 컨트롤러의 역할은 단순하게 사용자로부터 요청을 받아서 서비스에 넘겨주는 역할만 하도록 구현하였다.
- 하지만 유효한 요청인지에 대해 검증하는 부분을 컨트롤러로 빼게 된다면, 서비스 로직이 제어의 흐름을 관리하는데만 더 집중할 수 있을 것이다. 또한 로직의 흐름도 깔끔해질 것이다.

## 10) 기존 changeCompleteStatus 메서드를 봤을땐 Complete 상태로 변경한다는 느낌이 명확하지 않다. todo.changeStatus(status)  -> todo.changeStatus(TodoStatus.Completed) 로 변경하는게 더 메서드의 역할을 파악하기 좋을 것이다.

- 기존 changeCompleteStatus() 메서드를 보면 아래와 같다. 메서드명을 뺴고 인자와 로직을 자세히보면 전혀 Complete 상태로 변경한다는 메서드의 역할을 알 수 없다.

~~~
private void changeCompleteStatus(Long todoId, TodoStatus status) {
    Todo todo = todoRepository.findByIdFetchJoinMemberAndChilds(todoId);
    if(!todo.isAllChildsCompleted()) {
        throw new CustomException(ALL_CHILD_TODO_NOT_COMPLETED);
    }

    SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

    todo.changeStatus(status);
}
~~~

- 이는 다음과 같이 바꿈으로써 Todo의 상태를 Complete 상태로 변경하는 메서드의 역할을 좀 더 명시적으로 나타낼 수 있다.

~~~
private void changeCompleteStatus(Long todoId) {
    Todo todo = todoRepository.findByIdFetchJoinMemberAndChilds(todoId);
    if(!todo.isAllChildsCompleted()) {
        throw new CustomException(ALL_CHILD_TODO_NOT_COMPLETED);
    }

    SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

    todo.changeStatus(TodoStatus.COMPLETED);
}
~~~

## 11) changeStatus() 메서드에서 단순하게 COMPLETED 상태일땐 하위 Todo들이 전부 완료된 상태인지 검증하는 조건만 추가해줌으로써 리팩토링할 수 있다.

- 기존 코드는 다음과 같이 changeStatus() 를 호출하고 Completed 상태로 변경하는 요청일 경우 changeCompleteStatus()를 호출하고 있다.

~~~
@Transactional
public void changeStatus(Long todoId, TodoStatusUpdateRequestDto rq) {
    TodoStatus status = rq.getStatus();
    if (TodoStatus.COMPLETED.equals(status)) {
        changeCompleteStatus(todoId, status);
        return;
    }

    Todo todo = todoRepository.findByIdFetchJoinMember(todoId);

    SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

    todo.changeStatus(status);
}

private void changeCompleteStatus(Long todoId, TodoStatus status) {
    Todo todo = todoRepository.findByIdFetchJoinMemberAndChilds(todoId);
    if(!todo.isAllChildsCompleted()) {
        throw new CustomException(ALL_CHILD_TODO_NOT_COMPLETED);
    }

    SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

    todo.changeStatus(status);
}
~~~

- 하지만 단순하게 COMPLETED 상태일땐 하위 Todo들이 전부 완료된 상태인지 검증하는 조건만 추가해줌으로써 코드 중복을 줄일 수 있다.

~~~
@Transactional
public void changeStatus(Long todoId, TodoStatusUpdateRequestDto rq) {
    TodoStatus status = rq.getStatus();
    Todo todo;
    if (TodoStatus.COMPLETED.equals(status)) {
        todo = todoRepository.findByIdFetchJoinMemberAndChilds(todoId);
        checkAllChildsCompleted(todo);
    } else {
        todo = todoRepository.findByIdFetchJoinMember(todoId);
    }

    SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

    todo.changeStatus(status);
}

private void checkAllChildsCompleted(Todo todo) {
    if(!todo.isAllChildsCompleted()) {
        throw new CustomException(ALL_CHILD_TODO_NOT_COMPLETED);
    }
}
~~~

- 또한 `TodoStatus.COMPLETED.equals(status)` 조건문을 `TodoStatus.COMPLETED == status` 이렇게 좀 더 명시적으로 변경해줘도 된다. <b>이보다 더 좋은건 TodoStatus Enum타입에 비교메서드를 추가하는 것이다.</b>

- TodoStatus.java

~~~
public enum TodoStatus {
   UNCOMPLETED, COMPLETED;

   public Boolean isEqualTo(TodoStatus status) {
      return this.name() == status.name();
   }
}

~~~

- TodoService.java

~~~
@Transactional
public void changeStatus(Long todoId, TodoStatusUpdateRequestDto rq) {
    TodoStatus status = rq.getStatus();
    Todo todo;
    
    if (TodoStatus.COMPLETED.isEqualTo(status)) {
        todo = todoRepository.findByIdFetchJoinMemberAndChilds(todoId);
        checkAllChildsCompleted(todo);
    } else {
        todo = todoRepository.findByIdFetchJoinMember(todoId);
    }   
    ...
}
~~~

## 12) WorkspaceService의 validateWorkspaceAuthority(Workspace workspace) 검증 메서드를 호출할 땐 무조건 페치 조인으로 여러 엔티티들을 조인해서 가져와야 한다. 이러한 검증 작업을 페치 조인 없이 컨트롤러단에서 수행하면 더 좋다.

- 참가자를 삭제할때 요청하는 클라이언트가 해당 작업 공간에 속해있어야만 한다고 요구사항을 제한하였다.
- 그래서 기존 코드는 다음과 같이 workspace 엔티티를 가져올때 여러 엔티티들을 동시에 페치조인으로 가져와서 검증하는 작업이 수행되었다.

~~~
@Transactional
public void deleteParticipantByMemberId(Long memberId, Long workspaceId) {
    Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId);

    validateWorkspaceAuthority(workspace);

    workspace.getParticipantGroup().removeParticipant(memberId);
}

private void validateWorkspaceAuthority(Workspace workspace) {
    if (workspace == null) {
        throw new CustomException(WORKSPACE_NOT_FOUND);
    }

    ParticipantGroup participantGroup = workspace.getParticipantGroup();
    if (!participantGroup.isExistByAccountId(SecurityUtil.getCurrentAccountId())) {
        throw new CustomException(UNAUTHORIZED_MEMBER);
    }

}
~~~

- 하지만 위에서도 언급했지만 이러한 검증 역할을 컨트롤러로 위임한다면 좀 더 서비스 본연의 역할을 제대로 수행할 수 있을 것이다.