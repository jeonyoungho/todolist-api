package com.example.domain.todo;

import com.example.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@DiscriminatorValue("basic")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BasicTodo extends Todo {
    @Column(nullable = false)
    private int expectedTime;

    @Builder
    public BasicTodo(User user, TodoWorkspace todoWorkspace, String content, Todo parent, TodoStatus status, int expectedTime) {
        super(user, todoWorkspace, content, parent, status);
        this.expectedTime = expectedTime;
    }

    public static BasicTodo createBasicTodo(User user, TodoWorkspace todoWorkspace, String content, Todo parent, int expectedTime) {
        return BasicTodo.builder()
                .user(user)
                .todoWorkspace(todoWorkspace)
                .content(content)
                .parent(parent)
                .status(TodoStatus.UNCOMPLETED)
                .expectedTime(expectedTime)
                .build();
    }
}
