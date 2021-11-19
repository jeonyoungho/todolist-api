package com.example.domain.todo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.example.domain.todo.QTodo.todo;
import static com.example.domain.todo.QTodoWorkspace.todoWorkspace;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Todo findByIdFetchJoinChilds(Long todoId) {
        QTodo subTodo = new QTodo("subTodo");

        return queryFactory
                .select(todo).distinct()
                .from(todo)
                .leftJoin(todo.childs, subTodo).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
    }

    @Override
    public Todo findByIdFetchJoinTodoWorkspaceGroupAndChilds(Long todoId) {
        QTodo subTodo = new QTodo("subTodo");

        return queryFactory
                .select(todo).distinct()
                .from(todo)
                .leftJoin(todo.todoWorkspaceGroup.todoWorkspaces, todoWorkspace).fetchJoin()
                .leftJoin(todo.childs, subTodo).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
    }
}
