package com.example.domain.todo;

import com.example.api.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        Todo parent = BasicTodo.createBasicTodo(member, workspace, "test2", null, 10);

        // when
        final String content = "test-content";
        final int expectedTime = 20;
        BasicTodoSaveRequestDto requestDto = BasicTodoSaveRequestDto.create(member.getId(), workspace.getId(), content, parent.getId(), expectedTime);
        BasicTodo result = (BasicTodo) TodoFactory.createTodo(member, workspace, parent, requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getExpectedTime()).isEqualTo(expectedTime);
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getTodoWorkspaceGroup().getTodoWorkspaces().size()).isEqualTo(1);
    }

}