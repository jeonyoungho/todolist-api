package com.example.domain.todo;

import com.example.api.dto.todo.basic.BasicTodoResponseDto;
import com.example.domain.TestQuerydslConfig;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Import(TestQuerydslConfig.class)
@RunWith(SpringRunner.class)
@DataJpaTest
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

        Workspace workspace = Workspace.create("test-workspace", Participant.create(member));
        workspaceRepository.save(workspace);

        final String content = "todo-test-content";
        final int expectedTime = 10;
        Todo todo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), content, null, expectedTime);
        todoRepository.save(todo);

        // when
        BasicTodo result= (BasicTodo) todoRepository.findById(todo.getId()).get();

        // then
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getMember().getAccountId()).isEqualTo(member.getAccountId());
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getExpectedTime()).isEqualTo(10);
    }

    @Test
    public void findById_ValidInputByBasicTodo_False() {
        // given

        // when
        Optional todo = todoRepository.findById(1L);

        // then
        assertThat(todo.isPresent()).isFalse();
    }

    @Test
    public void findByIdFetchJoinChilds_ValidInput_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));
        workspaceRepository.save(workspace);

        final String parentTodoContent = "parent-todo-test-content";
        Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), parentTodoContent, null, 10);
        todoRepository.save(parentTodo);

        Todo childTodo1 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child1-todo-test-content", parentTodo, 20);
        todoRepository.save(childTodo1);

        Todo childTodo2 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child2-todo-test-content", parentTodo, 20);
        todoRepository.save(childTodo2);

        em.flush();
        em.clear();

        // when
        Todo result = todoRepository.findByIdFetchJoinChilds(parentTodo.getId());

        // then
        assertThat(result.getChilds().size()).isEqualTo(2);
        assertThat(result.getContent()).isEqualTo(parentTodoContent);
    }

    @Test
    public void findByIdFetchJoinTodoWorkspaceGroupAndChilds_ValidInputByBasicTodo_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));
        workspaceRepository.save(workspace);

        final String parentTodoContent = "parent-todo-test-content";
        Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), parentTodoContent, null, 10);
        todoRepository.save(parentTodo);

        Todo childTodo1 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child1-todo-test-content", parentTodo, 10);
        todoRepository.save(childTodo1);

        Todo childTodo2 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child2-todo-test-content", parentTodo, 10);
        todoRepository.save(childTodo2);

        em.flush();
        em.clear();

        // when
        Todo result = todoRepository.findByIdFetchJoinTodoWorkspaceGroupAndChilds(parentTodo.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(parentTodoContent);
        assertThat(result.getChilds().size()).isEqualTo(2);
    }

    @Test
    public void findByIdFetchJoinTodoWorkspaceGroupAndChilds_NotExistedTodoId_IsNull() {
        // given

        // when
        Todo result = todoRepository.findByIdFetchJoinTodoWorkspaceGroupAndChilds(33L);

        // then
        assertThat(result).isNull();
    }


    @Test(expected = RuntimeException.class)
    public void findByIdFetchJoinTodoWorkspaceGroupAndChilds_MultipleQueryResults_ThrowPersistenceException() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));
        workspaceRepository.save(workspace);

        final String parentTodoContent = "parent-todo-test-content";
        Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), parentTodoContent, null, 10);
        todoRepository.save(parentTodo);

        Todo childTodo1 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child1-todo-test-content", parentTodo, 10);
        todoRepository.save(childTodo1);

        Todo childTodo2 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child2-todo-test-content", parentTodo, 10);
        todoRepository.save(childTodo2);

        // when
        Todo result = todoRepository.findByIdFetchJoinTodoWorkspaceGroupAndChilds(null);

        // then
        fail("해당 메소드의 결과는 단건이 아니기에 예외가 발생해야 합니다.");
    }

    @Test
    public void findAllBasicTodos_ValidInput_Success() {
        // given
        memberRepository.save(member);

        Workspace workspace = Workspace.create("test-workspace", Participant.create(member));
        workspaceRepository.save(workspace);

        Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "parent-todo-test-content", null, 10);
        todoRepository.save(parentTodo);

        for (int i=0; i<10; i++) {
            Todo childTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child-todo-test-content" + i, parentTodo, 20);
            todoRepository.save(childTodo);
        }

        // when
        int page = 0;
        final int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<BasicTodoResponseDto> result = todoRepository.findAllBasicTodos(pageable, workspace.getId(), TodoStatus.UNCOMPLETED);

        // then
        assertThat(result.getTotalElements()).isEqualTo(11); // 부모 Todo 1 + 자식 Todo 10
        assertThat(result.getSize()).isEqualTo(size);
    }
}