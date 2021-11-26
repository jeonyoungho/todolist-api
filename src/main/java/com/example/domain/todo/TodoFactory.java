package com.example.domain.todo;

import com.example.controller.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.controller.dto.todo.TodoSaveRequestDto;
import com.example.domain.member.Member;

public class TodoFactory {

    public static Todo createTodo(Member member, TodoWorkspace todoWorkspace, Todo parent, TodoSaveRequestDto request) {
        Todo todo = null;
        if (request instanceof BasicTodoSaveRequestDto) {
            BasicTodoSaveRequestDto basicTodoRequest = (BasicTodoSaveRequestDto) request;
            todo = BasicTodo.createBasicTodo(member, todoWorkspace, basicTodoRequest.getContent(), parent, basicTodoRequest.getExpectedTime());
        }

        return todo;
    }
}
