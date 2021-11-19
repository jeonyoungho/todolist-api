package com.example.controller.dto.todo;

import com.example.domain.todo.TodoStatus;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class TodoStatusUpdateRequestDto {
    @NotNull
    TodoStatus status;
}
