package com.example.domain.todo;

import com.example.api.dto.todo.basic.BasicTodoResponseDto;
import com.example.config.CustomDataJpaTest;
import com.example.config.TestQuerydslConfig;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(TestQuerydslConfig.class)
@RunWith(SpringRunner.class)
@CustomDataJpaTest
public class TodoRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void findById_ValidInputByBasicTodo_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace", member);
        workspaceRepository.save(workspace);

        final String content = "todo-test-content";
        final int expectedTime = 10;
        Todo todo = BasicTodo.create(member, workspace, content, null, expectedTime);
        todoRepository.save(todo);

        em.flush();
        em.clear();

        // when
        BasicTodo result= (BasicTodo) todoRepository.findById(todo.getId()).get();

        // then
        assertAll(
                () -> assertThat(result.getMember().getAccountId()).isEqualTo(member.getAccountId()),
                () -> assertThat(result.getContent()).isEqualTo(content),
                () -> assertThat(result.getExpectedTime()).isEqualTo(expectedTime)
        );
    }

    @Test
    public void findById_ValidInputByBasicTodo_False() {
        // when
        Optional todo = todoRepository.findById(1L);

        // then
        assertThat(todo.isPresent()).isFalse();
    }

    @Test
    public void findByIdFetchJoinMember_ValidInput_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace", member);
        workspaceRepository.save(workspace);

        final String content = "parent-todo-test-content";
        Todo parentTodo = BasicTodo.create(member, workspace, content, null, 10);
        todoRepository.save(parentTodo);

        em.flush();
        em.clear();

        // when
        Todo result = todoRepository.findByIdFetchJoinMember(parentTodo.getId()).get();

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).isEqualTo(content),
                () -> assertThat(result.getMember()).isNotNull(),
                () -> assertThat(result.getMember().getAccountId()).isEqualTo("test-id")
        );
    }

    @Test
    public void findByIdFetchJoinMemberAndChilds_ValidInput_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace-name", member);
        workspaceRepository.save(workspace);

        final String parentTodoContent = "parent-todo-test-content";
        Todo parentTodo = BasicTodo.create(member, workspace, parentTodoContent, null, 10);
        todoRepository.save(parentTodo);

        Todo childTodo1 = BasicTodo.create(member, workspace, "child1-todo-test-content", parentTodo, 20);
        todoRepository.save(childTodo1);

        Todo childTodo2 = BasicTodo.create(member, workspace, "child2-todo-test-content", parentTodo, 20);
        todoRepository.save(childTodo2);

        em.flush();
        em.clear();

        // when
        Todo result = todoRepository.findByIdFetchJoinMemberAndChilds(parentTodo.getId()).get();

        // then
        assertAll(
                () -> assertThat(result.getChilds().size()).isEqualTo(2),
                () -> assertThat(result.getContent()).isEqualTo(parentTodoContent)
        );
    }

    @Test
    public void findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds_ValidInputByBasicTodo_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace-name", member);
        workspaceRepository.save(workspace);

        Member member2 = Member.create("test-id2", "test-pw", "test-name2", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        memberRepository.save(member2);

        Workspace workspace2 = Workspace.create("test-workspace-name2", member);
        workspaceRepository.save(workspace2);

        final String parentTodoContent = "parent-todo-test-content";
        Todo parentTodo = BasicTodo.create(member, workspace, parentTodoContent, null, 1);
        parentTodo.addTodoWorkspace(workspace2);
        todoRepository.save(parentTodo);


        Todo childTodo1 = BasicTodo.create(member, workspace, "child1-todo-test-content", parentTodo, 10);
        todoRepository.save(childTodo1);

        Todo childTodo2 = BasicTodo.create(member, workspace, "child2-todo-test-content", parentTodo, 20);
        todoRepository.save(childTodo2);

        Todo childTodo3 = BasicTodo.create(member, workspace, "child3-todo-test-content", parentTodo, 30);
        todoRepository.save(childTodo3);

        em.flush();
        em.clear();

        // when
        Todo result = todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(parentTodo.getId()).get();

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getTodoWorkspaceGroup().getTodoWorkspaces().size()).isEqualTo(2),
                () -> assertThat(result.getContent()).isEqualTo(parentTodoContent),
                () -> assertThat(result.getChilds().size()).isEqualTo(3)
        );
    }

    @Test
    public void findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds_NotExistedTodoId_False() {
        // when
        long fakeTodoId = 33L;
        Optional<Todo> result = todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(fakeTodoId);

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void findAllBasicTodos_ValidInput_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace", member);
        workspaceRepository.save(workspace);

        Todo parentTodo = BasicTodo.create(member, workspace, "parent-todo-test-content", null, 10);
        todoRepository.save(parentTodo);

        for (int i=0; i<10; i++) {
            Todo childTodo = BasicTodo.create(member, workspace, "child-todo-test-content" + i, parentTodo, 20);
            todoRepository.save(childTodo);
        }

        em.flush();
        em.clear();

        // when
        int page = 2;
        final int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<BasicTodoResponseDto> result = todoRepository.findAllBasicTodos(pageable, workspace.getId(), TodoStatus.UNCOMPLETED);

        // then
        assertAll(
                () -> assertThat(result.getTotalElements()).isEqualTo(11), // 부모 Todo 1 + 자식 Todo 10
                () -> assertThat(result.getSize()).isEqualTo(size)
        );

    }
}
