package com.example.domain.todo;

import com.example.controller.dto.todo.basic.BasicTodoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoRepositoryCustom {
    Todo findByIdFetchJoinChilds(Long todoId);
    Todo findByIdFetchJoinTodoWorkspaceGroupAndChilds(Long todoId);
    Page<BasicTodoResponseDto> findAllBasicTodos(Pageable pageable, Long workspaceId, TodoStatus todoStatus);
}
