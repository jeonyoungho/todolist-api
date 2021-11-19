package com.example.domain.todo;

public interface TodoRepositoryCustom {
    Todo findByIdFetchJoinChilds(Long todoId);
    Todo findByIdFetchJoinTodoWorkspaceGroupAndChilds(Long todoId);
}
