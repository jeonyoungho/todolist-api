package com.example.api.service;

import com.example.api.dto.todo.TodoStatusUpdateRequestDto;
import com.example.api.dto.todo.basic.BasicTodoResponseDto;
import com.example.api.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.BasicTodo;
import com.example.domain.todo.Todo;
import com.example.domain.todo.TodoRepository;
import com.example.domain.todo.TodoStatus;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.exception.CustomException;
import com.example.util.SecurityUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TodoServiceTest {

    @InjectMocks
    TodoService todoService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    WorkspaceRepository workspaceRepository;
    @Mock
    TodoRepository todoRepository;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        // 권한 추가
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
        UserDetails principal = new User(member.getAccountId(), "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }

    @Test
    public void saveBasicTodo_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);

        Todo parentTodo = BasicTodo.create(member, workspace, "parent-todo-test-content", null, 10);
        ReflectionTestUtils.setField(parentTodo, "id", anyLong());

        BasicTodoSaveRequestDto rq = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .workspaceId(workspace.getId())
                .parentId(parentTodo.getId())
                .build();

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));
        when(todoRepository.findById(rq.getParentId())).thenReturn(Optional.of(parentTodo));
        when(workspaceRepository.findById(rq.getWorkspaceId())).thenReturn(Optional.of(workspace));
        when(todoRepository.save(any(Todo.class))).thenReturn(any(Todo.class));

        // when
        Long savedTodoId = todoService.saveBasicTodo(rq);

        // then
        assertAll(
            () -> verify(memberRepository).findById(rq.getMemberId()),
            () -> verify(memberRepository, times(1)).findById(rq.getMemberId()),

            () -> verify(todoRepository).findById(rq.getParentId()),
            () -> verify(todoRepository, times(1)).findById(rq.getParentId()),

            () -> verify(workspaceRepository).findById(rq.getWorkspaceId()),
            () -> verify(workspaceRepository, times(1)).findById(rq.getWorkspaceId()),

            () -> verify(todoRepository).save(any(Todo.class)),
            () -> verify(todoRepository, times(1)).save(any(Todo.class))
        );

    }

    @Test(expected = CustomException.class)
    public void saveBasicTodo_NotExistedMember_ThrowCustomException() {
        // given
        BasicTodoSaveRequestDto rq = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .build();

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.empty());


        // when
        Long savedTodoId = todoService.saveBasicTodo(rq);

        // then
        fail("회원 조회시 예외가 발생해야 합니다.");

    }

    @Test(expected = CustomException.class)
    public void saveBasicTodo_InvalidRequestByClient_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        BasicTodoSaveRequestDto rq = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .build();

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));

        // when
        Long savedTodoId = todoService.saveBasicTodo(rq);

        // then
        fail("Todo 저장 유효성 체크시 예외가 발생해야 합니다.");

    }

    @Test
    public void saveBasicTodo_ParentTodoIsNull_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);
        Todo todo = BasicTodo.create(member, workspace, "todo-test-content", null, 10);

        BasicTodoSaveRequestDto rq = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .workspaceId(workspace.getId())
                .build();

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));
        when(workspaceRepository.findById(rq.getWorkspaceId())).thenReturn(Optional.of(workspace));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // when
        Long savedTodoId = todoService.saveBasicTodo(rq);

        // then
        assertAll(
            () -> verify(memberRepository).findById(rq.getMemberId()),
            () -> verify(memberRepository, times(1)).findById(rq.getMemberId()),

            () -> verify(workspaceRepository).findById(rq.getWorkspaceId()),
            () -> verify(workspaceRepository, times(1)).findById(rq.getWorkspaceId()),

            () -> verify(todoRepository).save(any(Todo.class)),
            () -> verify(todoRepository, times(1)).save(any(Todo.class))
        );
    }

    @Test(expected = CustomException.class)
    public void saveBasicTodo_NotExistedParentTodo_ThrowCustomException() {
        // given
        BasicTodoSaveRequestDto rq = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .parentId(1L)
                .build();

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));
        when(todoRepository.findById(rq.getParentId())).thenReturn(Optional.empty());

        // when
        Long savedTodoId = todoService.saveBasicTodo(rq);

        // then
        fail("부모 Todo 엔티티를 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void saveBasicTodo_ParentTodoIsNotRoot_ThrowCustomException() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);

        Todo grandParentTodo = BasicTodo.create(member, workspace, "grand-parent-todo-test-content", null, 10);
        Todo parentTodo = BasicTodo.create(member, workspace, "parent-todo-test-content", grandParentTodo, 20);
        ReflectionTestUtils.setField(parentTodo, "id", anyLong());

        BasicTodoSaveRequestDto rq = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .parentId(parentTodo.getId())
                .build();

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(parentTodo));

        // when
        Long savedTodoId = todoService.saveBasicTodo(rq);

        // then
        fail("부모 Todo 의 부모가 존재하는지 검증시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void saveBasicTodo_NotExistedWorkspace_ThrowCustomException() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);

        BasicTodoSaveRequestDto rq = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .workspaceId(workspace.getId())
                .build();

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));
        when(workspaceRepository.findById(rq.getWorkspaceId())).thenReturn(Optional.empty());

        // when
        Long savedTodoId = todoService.saveBasicTodo(rq);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void findAllBasicTodos_ValidInput_Success() {
        // given
        final Pageable pageable = PageRequest.of(0, 10);
        final Long workspaceId = 1L;
        final TodoStatus status = TodoStatus.UNCOMPLETED;

        // mocking
        when(workspaceRepository.existsById(workspaceId)).thenReturn(true);
        when(workspaceRepository.existsByIdAndCurrentAccountId(workspaceId, SecurityUtil.getCurrentAccountId())).thenReturn(true);
        when(todoRepository.findAllBasicTodos(pageable, workspaceId, status)).thenReturn(any(Page.class));

        // when
        Page<BasicTodoResponseDto> result = todoService.findAllBasicTodos(pageable, workspaceId, status);

        // then
        assertAll(
            () -> verify(workspaceRepository).existsById(workspaceId),
            () -> verify(workspaceRepository, times(1)).existsById(workspaceId),

            () -> verify(workspaceRepository).existsByIdAndCurrentAccountId(workspaceId, SecurityUtil.getCurrentAccountId()),
            () -> verify(workspaceRepository, times(1)).existsByIdAndCurrentAccountId(workspaceId, SecurityUtil.getCurrentAccountId()),

            () -> verify(todoRepository).findAllBasicTodos(pageable, workspaceId, status),
            () -> verify(todoRepository, times(1)).findAllBasicTodos(pageable, workspaceId, status)
        );
    }

    @Test(expected = CustomException.class)
    public void findAllBasicTodos_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Pageable pageable = PageRequest.of(0, 10);
        final Long workspaceId = 1L;
        final TodoStatus status = TodoStatus.UNCOMPLETED;

        // mocking
        when(workspaceRepository.existsById(workspaceId)).thenReturn(false);

        // when
        Page<BasicTodoResponseDto> result = todoService.findAllBasicTodos(pageable, workspaceId, status);

        // then
        fail("작업 공간이 존재하는지 확인시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void findAllBasicTodos_MemberNotInWorkspace_ThrowCustomException() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);

        final Pageable pageable = PageRequest.of(0, 10);
        final Long workspaceId = 1L;
        final TodoStatus status = TodoStatus.UNCOMPLETED;

        // mocking
        when(workspaceRepository.existsById(workspaceId)).thenReturn(true);
        when(workspaceRepository.existsByIdAndCurrentAccountId(workspaceId, SecurityUtil.getCurrentAccountId())).thenReturn(false);

        // when
        Page<BasicTodoResponseDto> result = todoService.findAllBasicTodos(pageable, workspaceId, status);

        // then
        fail("권한이 있는 사용자인지 검증시 예외가 발생해야 합니다.");
    }

    @Test
    public void changeStatus_ValidInputWithUncompletedStatus_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);
        Todo todo = BasicTodo.create(member, workspace, "todo-test-content", null, 10);

        final Long todoId = anyLong();
        final TodoStatusUpdateRequestDto rq = TodoStatusUpdateRequestDto.builder()
                .status(TodoStatus.UNCOMPLETED)
                .build();

        // mocking
        when(todoRepository.findByIdFetchJoinMember(todoId)).thenReturn(Optional.of(todo));

        // when
        todoService.changeStatus(todoId, rq);

        // then
        assertAll(
            () -> verify(todoRepository).findByIdFetchJoinMember(todoId),
            () -> verify(todoRepository, times(1)).findByIdFetchJoinMember(todoId)
        );
    }

    @Test(expected = CustomException.class)
    public void changeStatus_NotExistedTodoWithUncompletedStatus_ThrowCustomException() {
        // given
        final Long todoId = anyLong();
        final TodoStatusUpdateRequestDto rq = TodoStatusUpdateRequestDto.builder()
                .status(TodoStatus.UNCOMPLETED)
                .build();

        // mocking
        when(todoRepository.findByIdFetchJoinMember(todoId)).thenReturn(Optional.empty());

        // when
        todoService.changeStatus(todoId, rq);

        // then
        fail("Todo 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void changeStatus_InvalidRequestByClientWithUncompletedStatus_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        Workspace workspace = Workspace.create("test-workspace", member);
        Todo todo = BasicTodo.create(member, workspace, "todo-test-content", null, 10);

        final Long todoId = anyLong();
        final TodoStatusUpdateRequestDto rq = TodoStatusUpdateRequestDto.builder()
                .status(TodoStatus.UNCOMPLETED)
                .build();

        // mocking
        when(todoRepository.findByIdFetchJoinMember(todoId)).thenReturn(Optional.of(todo));

        // when
        todoService.changeStatus(todoId, rq);

        // then
        fail("Todo 상태 변경 권한 체크시 예외가 발생해야 합니다.");
    }

    @Test
    public void changeStatus_ValidInputWithCompletedStatus_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);
        Todo todo = BasicTodo.create(member, workspace, "todo-test-content", null, 10);

        final Long todoId = anyLong();
        final TodoStatusUpdateRequestDto rq = TodoStatusUpdateRequestDto.builder()
                .status(TodoStatus.COMPLETED)
                .build();

        // mocking
        when(todoRepository.findByIdFetchJoinMemberAndChilds(todoId)).thenReturn(Optional.of(todo));

        // when
        todoService.changeStatus(todoId, rq);

        // then
        assertAll(
            () -> verify(todoRepository).findByIdFetchJoinMemberAndChilds(todoId),
            () -> verify(todoRepository, times(1)).findByIdFetchJoinMemberAndChilds(todoId)
        );
    }

    @Test(expected = CustomException.class)
    public void changeStatus_NotExistedTodoWithCompletedStatus_ThrowCustomException() {
        // given
        final Long todoId = anyLong();
        final TodoStatusUpdateRequestDto rq = TodoStatusUpdateRequestDto.builder()
                .status(TodoStatus.UNCOMPLETED)
                .build();

        // mocking
        when(todoRepository.findByIdFetchJoinMember(todoId)).thenReturn(Optional.empty());

        // when
        todoService.changeStatus(todoId, rq);

        // then
        fail("Todo 조회시 예외가 발생해야 합니다.");
    }


    @Test(expected = CustomException.class)
    public void changeStatus_IsAllChildNotCompletedWithCompletedStatus_ThrowCustomException() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);
        Todo parentTodo = BasicTodo.create(member, workspace, "parent-todo-test-content", null, 10);
        Set<BasicTodo> childs = Collections.singleton(BasicTodo.create(member, workspace, "child-todo-test-content", parentTodo, 20));
        ReflectionTestUtils.setField(parentTodo, "childs", childs);

        final Long todoId = anyLong();
        final TodoStatusUpdateRequestDto rq = TodoStatusUpdateRequestDto.builder()
                .status(TodoStatus.COMPLETED)
                .build();

        // mocking
        when(todoRepository.findByIdFetchJoinMemberAndChilds(todoId)).thenReturn(Optional.of(parentTodo));

        // when
        todoService.changeStatus(todoId, rq);

        // then
        fail("모든 자식 Todo 들이 완료된 상태인지 검증시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void changeStatus_InvalidRequestByClientWithCompletedStatus_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        Workspace workspace = Workspace.create("test-workspace", member);
        Todo todo = BasicTodo.create(member, workspace, "todo-test-content", null, 10);

        final Long todoId = anyLong();
        final TodoStatusUpdateRequestDto rq = TodoStatusUpdateRequestDto.builder()
                .status(TodoStatus.COMPLETED)
                .build();

        // mocking
        when(todoRepository.findByIdFetchJoinMemberAndChilds(todoId)).thenReturn(Optional.of(todo));

        // when
        todoService.changeStatus(todoId, rq);

        // then
        fail("모든 자식 Todo 들이 완료된 상태인지 검증시 예외가 발생해야 합니다.");
    }

    @Test
    public void delete_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace", member);
        Todo parentTodo = BasicTodo.create(member, workspace, "parent-todo-test-content", null, 10);

        Todo childTodo = BasicTodo.create(member, workspace, "child-todo-test-content", parentTodo, 20);
        Set<Todo> childs = new LinkedHashSet<>();
        childs.add(childTodo);
        ReflectionTestUtils.setField(parentTodo, "childs", childs);

        // mocking
        when(todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(anyLong())).thenReturn(Optional.of(parentTodo));
        doNothing().when(todoRepository).deleteById(anyLong());

        // when
        todoService.delete(anyLong());

        // then
        assertAll(
                () -> verify(todoRepository).findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(anyLong()),
                () -> verify(todoRepository, times(1)).findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(anyLong()),

                () -> verify(todoRepository).deleteById(anyLong()),
                () -> verify(todoRepository, times(1)).deleteById(anyLong())
        );
    }

    @Test(expected = CustomException.class)
    public void delete_NotExistedTodo_ThrowCustomException() {
        // mocking
        when(todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(anyLong())).thenReturn(Optional.empty());

        // when
        todoService.delete(anyLong());

        // then
        fail("Todo 가 존재하는지 확인시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void delete_InvalidRequestByClient_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        Workspace workspace = Workspace.create("test-workspace", member);
        Todo parentTodo = BasicTodo.create(member, workspace, "parent-todo-test-content", null, 10);

        final Long todoId = anyLong();

        // mocking
        when(todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(todoId)).thenReturn(Optional.of(parentTodo));

        // when
        todoService.delete(todoId);

        // then
        fail("Todo 삭제 권한 체크시 예외가 발생해야 합니다.");
    }

}
