package com.example.service;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.BasicTodo;
import com.example.domain.todo.TodoRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.factory.UserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TodoServiceTest {

    @Autowired
    TodoService todoService;
    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;
    @Autowired
    TodoRepository<com.example.domain.todo.Todo> todoRepository;

    @Test
    public void saveBasicTodo_GivenValidInput_Success() throws Exception {
//        System.out.println("todoService = " + userRepository.findAll().size());

        // given
        Member member = UserFactory.createUser();
        memberRepository.save(member);

        Participant participant = Participant.create(member);

        final String testWorkspaceName= "test-workspace";
        Workspace workspace = Workspace.create(testWorkspaceName, participant);
        workspaceRepository.save(workspace);

        em.flush();
        em.clear();

        final String content = "todo-test-content";
        BasicTodoSaveRequestDto request = BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .workspaceId(workspace.getId())
                .content(content)
                .parentId(null)
                .expectedTime(10)
                .build();

        // when
        Long result = todoService.saveBasicTodo(request);
        BasicTodo basicTodo = (BasicTodo) todoRepository.findById(result).get();

        // then
        assertThat(basicTodo.getContent()).isEqualTo(content);
    }

}