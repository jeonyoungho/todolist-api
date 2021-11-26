package com.example.domain.todo;

import com.example.controller.dto.todo.basic.BasicTodoResponseDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.service.TodoService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TodoRepositoryImplTest {

    @Autowired
    TodoRepository todoRepository;
    @Autowired
    EntityManager em;
    @Autowired
    TodoService todoService;
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
    public void findById_GivenValidInput_Success() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace", participant);
        workspaceRepository.save(workspace);

        Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "parent-todo-test-content", null, 10);
        todoRepository.save(parentTodo);

        // when
        BasicTodo result= (BasicTodo) todoRepository.findById(parentTodo.getId()).get();

        // then
        assertThat(result.getExpectedTime()).isEqualTo(10);
    }

    @Test
    public void findByIdFetchJoinTodoWorkspaceGroupAndChilds_GivenValidInput_Success() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace", participant);
        workspaceRepository.save(workspace);

        Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "parent-todo-test-content", null, 10);
        todoRepository.save(parentTodo);

        Todo childTodo1 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child1-todo-test-content", parentTodo, 10);
        todoRepository.save(childTodo1);

        Todo childTodo2 = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child2-todo-test-content", parentTodo, 10);
        todoRepository.save(childTodo2);

        em.flush();
        em.clear();

        // when
        Todo result = todoRepository.findByIdFetchJoinTodoWorkspaceGroupAndChilds(parentTodo.getId());
        Set<Todo> childs = result.getChilds();

        // then
        Assertions.assertThat(childs.size()).isEqualTo(2);
    }

    @Test
    public void findAllTodos_GivenValidInput_Success() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace", participant);
        workspaceRepository.save(workspace);

        Todo parentTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "parent-todo-test-content", null, 10);
        todoRepository.save(parentTodo);

        for (int i=0; i<10; i++) {
            Todo childTodo = BasicTodo.createBasicTodo(member, TodoWorkspace.create(workspace), "child-todo-test-content" + i, parentTodo, 20);
            todoRepository.save(childTodo);
        }

        em.flush();
        em.clear();

        // when
        final int page = 0;
        final int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        Page<BasicTodoResponseDto> result = todoRepository.findAllBasicTodos(pageable, workspace.getId(), TodoStatus.UNCOMPLETED);

        // then
        assertThat(result.getTotalElements()).isEqualTo(11); // 부모 Todo 1 + 자식 Todo 10
        assertThat(result.getSize()).isEqualTo(size);
    }

    @Test
    public void hasTodo_GivenValidInput_Success() {
        // given
        memberRepository.save(member);

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace", participant);
        workspaceRepository.save(workspace);

        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);
        Todo parentTodo = BasicTodo.createBasicTodo(member, todoWorkspace, "parent-todo-test-content", null, 10);
        todoRepository.save(parentTodo);
        Long parentTodoId = parentTodo.getId();

        Todo childTodo = BasicTodo.createBasicTodo(member, todoWorkspace, "child-todo-test-content", parentTodo, 20);
        todoRepository.save(childTodo);
        Long childTodoId = childTodo.getId();

        // when
        BasicTodo parentResult = (BasicTodo) todoRepository.findById(parentTodoId).get();
        BasicTodo childResult = (BasicTodo) todoRepository.findById(childTodoId).get();

        // then
        assertThat(parentResult.hasParent()).isFalse();
        assertThat(childResult.hasParent()).isTrue();
    }
}