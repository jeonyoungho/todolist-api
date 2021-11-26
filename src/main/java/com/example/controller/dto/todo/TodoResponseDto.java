package com.example.controller.dto.todo;

import com.example.domain.todo.TodoStatus;
import lombok.Getter;
import lombok.ToString;

@ToString(of = {"todoId", "memberName", "todoContent", "todoStatus"})
@Getter
public abstract class TodoResponseDto {

    private Long todoId;

    private String memberName;

    private String todoContent;

//    private Set<Long> childs = new LinkedHashSet<>();

    private TodoStatus todoStatus;

    public TodoResponseDto(Long todoId, String memberName, String todoContent, TodoStatus todoStatus) {
        this.todoId = todoId;
        this.memberName = memberName;
        this.todoContent = todoContent;
        this.todoStatus = todoStatus;
    }
}
