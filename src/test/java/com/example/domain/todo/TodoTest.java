package com.example.domain.todo;

import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoTest {

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
    }

    @Test
    public void hasParent_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace", Participant.create(member));

        Todo parentTodo = BasicTodo.createBasicTodo(member, workspace, "parent-todo-test-content", null, 10);
        Todo childTodo = BasicTodo.createBasicTodo(member, workspace, "child-todo-test-content", parentTodo, 20);

        // when & then
        assertThat(parentTodo.hasParent()).isFalse();
        assertThat(childTodo.hasParent()).isTrue();
    }

}