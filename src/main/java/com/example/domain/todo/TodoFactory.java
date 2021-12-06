package com.example.domain.todo;

import com.example.api.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.api.dto.todo.TodoSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.workspace.Workspace;

public class TodoFactory {

    public static Todo createTodo(Member member, Workspace workspace, Todo parent, TodoSaveRequestDto request) {
        Todo todo = null;
        if (request instanceof BasicTodoSaveRequestDto) {
            BasicTodoSaveRequestDto basicTodoRequest = (BasicTodoSaveRequestDto) request;
            todo = BasicTodo.createBasicTodo(member, workspace, basicTodoRequest.getContent(), parent, basicTodoRequest.getExpectedTime());
        }

        return todo;
    }
}
