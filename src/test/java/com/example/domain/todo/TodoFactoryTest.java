package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.domain.user.Address;
import com.example.domain.user.User;
import com.example.domain.user.UserRole;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TodoFactoryTest {

    @Test
    public void createTodo_GivenValidInput_Success() throws Exception {
        // given
        final Long memberId = 1L;
        final String content = "test-content";
        final Long parentId = 1L;
        final int expectedTime = 10;

        User user = User.builder()
                .accountId("test-id")
                .accountPw("test-pw")
                .name("test-user")
                .address(Address.builder()
                        .street("test-street")
                        .city("test-city")
                        .zipcode("test-zipcode")
                        .build())
                .role(UserRole.ROLE_USER)
                .build();

        Participant participant = Participant.create(user);
        Workspace workspace = Workspace.create("test-workspace", participant);
        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);

        Todo parent = BasicTodo.createBasicTodo(user, todoWorkspace, "test2", null, 10);

        // when
        Todo result = TodoFactory.createTodo(user, todoWorkspace, parent, BasicTodoSaveRequestDto.builder()
                .memberId(memberId)
                .content(content)
                .parentId(parentId)
                .expectedTime(expectedTime)
                .build());

        // then
        Assertions.assertThat(result.getContent()).isEqualTo(content);
    }

}