package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class TodoFactoryTest {

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void createTodo_GivenValidInput_Success() {
        // given
        Participant participant = Participant.create(member);

        Workspace workspace = Workspace.create("test-workspace", participant);

        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);

        Todo parent = BasicTodo.createBasicTodo(member, todoWorkspace, "test2", null, 10);

        // when
        final String content = "test-content";
        Todo result = TodoFactory.createTodo(member, todoWorkspace, parent, BasicTodoSaveRequestDto.builder()
                .memberId(member.getId())
                .content(content)
                .parentId(parent.getId())
                .expectedTime(10)
                .build());

        // then
        Assertions.assertThat(result.getContent()).isEqualTo(content);
    }

}