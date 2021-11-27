package com.example.domain.todo;

import com.example.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@DiscriminatorValue(TodoConstants.BASIC_TODO_TYPE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BasicTodo extends Todo {
    @Column(nullable = false)
    private int expectedTime;

    @Builder
    public BasicTodo(Member member, TodoWorkspace todoWorkspace, String content, Todo parent, TodoStatus status, int expectedTime) {
        super(member, todoWorkspace, content, parent, status);
        this.expectedTime = expectedTime;
    }

    public static BasicTodo createBasicTodo(Member member, TodoWorkspace todoWorkspace, String content, Todo parent, int expectedTime) {
        return BasicTodo.builder()
                .member(member)
                .todoWorkspace(todoWorkspace)
                .content(content)
                .parent(parent)
                .status(TodoStatus.UNCOMPLETED)
                .expectedTime(expectedTime)
                .build();
    }
}
