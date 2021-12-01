package com.example.domain.todo;

import com.example.api.dto.todo.basic.BasicTodoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoRepositoryCustom {
    Todo findByIdFetchJoinMember(Long todoId);
    Todo findByIdFetchJoinMemberAndChilds(Long todoId);
    Todo findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(Long todoId);
    Page<BasicTodoResponseDto> findAllBasicTodos(Pageable pageable, Long workspaceId, TodoStatus todoStatus);
}
