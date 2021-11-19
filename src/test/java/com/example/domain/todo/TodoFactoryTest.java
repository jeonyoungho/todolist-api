package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.domain.member.Address;
import com.example.domain.member.Member;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class TodoFactoryTest {

    @Test
    public void createTodo_GivenValidInput_Success() throws Exception {
        // given
        final Long memberId = 1L;
        final String content = "test-content";
        final Long parentId = 1L;
        final int expectedTime = 10;

        Member member = Member.builder()
                .userId("test-id")
                .password("test-pw")
                .username("test-user")
                .address(Address.builder()
                        .street("test-street")
                        .city("test-city")
                        .zipcode("test-zipcode")
                        .build())
                .build();

        Participant participant = Participant.create(member);
        Workspace workspace = Workspace.create("test-workspace", participant);
        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);

        Todo parent = BasicTodo.createBasicTodo(member, todoWorkspace, "test2", null, 10);

        // when
        Todo result = TodoFactory.createTodo(member, todoWorkspace, parent, BasicTodoSaveRequestDto.builder()
                .memberId(memberId)
                .content(content)
                .parentId(parentId)
                .expectedTime(expectedTime)
                .build());

        // then
        Assertions.assertThat(result.getContent()).isEqualTo(content);
        System.out.println(result);
    }

}