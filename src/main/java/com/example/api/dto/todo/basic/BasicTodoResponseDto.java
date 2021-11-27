package com.example.api.dto.todo.basic;

import com.example.api.dto.todo.TodoResponseDto;
import com.example.domain.todo.TodoStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class BasicTodoResponseDto extends TodoResponseDto {

    private int expectedTime;

    @QueryProjection
    public BasicTodoResponseDto(Long todoId, String memberName, String todoContent, TodoStatus todoStatus, int expectedTime) {
        super(todoId, memberName, todoContent, todoStatus);
        this.expectedTime = expectedTime;
    }
}
