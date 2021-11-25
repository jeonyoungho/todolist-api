package com.example.service;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.BasicTodo;
import com.example.domain.todo.TodoRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TodoServiceTest {

    @Autowired
    TodoService todoService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;
    @Autowired
    TodoRepository<com.example.domain.todo.Todo> todoRepository;

    @Test
    public void saveBasicTodo_GivenValidInput_Success() throws Exception {
        // given
        Member member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        memberRepository.save(member);

        Participant participant = Participant.create(member);

        Workspace workspace = Workspace.create("test-workspace", participant);
        workspaceRepository.save(workspace);

        final String content = "todo-test-content";
        BasicTodoSaveRequestDto request = BasicTodoSaveRequestDto.create(member.getId(), workspace.getId(), content, null, 10);

        // when
        Long result = todoService.saveBasicTodo(request);
        BasicTodo basicTodo = (BasicTodo) todoRepository.findById(result).get();

        // then
        assertThat(basicTodo.getContent()).isEqualTo(content);
    }

}