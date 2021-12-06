package com.example.domain.todo;

import com.example.domain.member.Member;
import com.example.domain.workspace.Workspace;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@DynamicUpdate
@DiscriminatorValue(TodoConstants.BASIC_TODO_TYPE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BasicTodo extends Todo {
    @Column(nullable = false)
    private int expectedTime;

    @Builder
    public BasicTodo(Member member, Workspace workspace, String content, Todo parent, TodoStatus status, int expectedTime) {
        super(member, workspace, content, parent, status);
        this.expectedTime = expectedTime;
    }

    //== 생성 메서드 ==//
    public static BasicTodo createBasicTodo(Member member, Workspace workspace, String content, Todo parent, int expectedTime) {
        return BasicTodo.builder()
                .member(member)
                .workspace(workspace)
                .content(content)
                .parent(parent)
                .status(TodoStatus.UNCOMPLETED)
                .expectedTime(expectedTime)
                .build();
    }
}
