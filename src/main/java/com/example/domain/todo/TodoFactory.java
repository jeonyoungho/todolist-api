package com.example.domain.todo;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.todo.TodoSaveRequestDto;
import com.example.domain.user.User;

public class TodoFactory {

    public static Todo createTodo(User user, TodoWorkspace todoWorkspace, Todo parent, TodoSaveRequestDto request) {
        Todo todo = null;
        if (request instanceof BasicTodoSaveRequestDto) {
            BasicTodoSaveRequestDto basicTodoRequest = (BasicTodoSaveRequestDto) request;
            todo = BasicTodo.createBasicTodo(user, todoWorkspace, basicTodoRequest.getContent(), parent, basicTodoRequest.getExpectedTime());
        }

        return todo;
    }
}
