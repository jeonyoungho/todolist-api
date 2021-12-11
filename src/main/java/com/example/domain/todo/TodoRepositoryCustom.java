package com.example.domain.todo;

import com.example.api.dto.todo.basic.BasicTodoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoRepositoryCustom {
    Optional<Todo> findByIdFetchJoinMember(Long todoId);
    Optional<Todo> findByIdFetchJoinMemberAndChilds(Long todoId);
    Optional<Todo> findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(Long todoId);
    Page<BasicTodoResponseDto> findAllBasicTodos(Pageable pageable, Long workspaceId, TodoStatus todoStatus);
}
