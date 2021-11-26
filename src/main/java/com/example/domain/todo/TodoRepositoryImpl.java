package com.example.domain.todo;

import com.example.controller.dto.todo.basic.BasicTodoResponseDto;
import com.example.controller.dto.todo.basic.QBasicTodoResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.domain.member.QMember.member;
import static com.example.domain.todo.QBasicTodo.basicTodo;
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
                .where(todoIdEq(todoId))
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
                .where(todoIdEq(todoId))
                .fetchOne();
    }

    @Override
    public Page<BasicTodoResponseDto> findAllBasicTodos(Pageable pageable, Long workspaceId, TodoStatus todoStatus) {
        List<BasicTodoResponseDto> content = queryFactory
                .select(
                    new QBasicTodoResponseDto(
                            basicTodo.id,
                            member.name.as("memberName"),
                            basicTodo.content.as("todoContent"),
                            basicTodo.status.as("todoStatus"),
                            basicTodo.expectedTime
                    )
                )
                .from(basicTodo)
                .leftJoin(basicTodo.member, member)
                .leftJoin(basicTodo.todoWorkspaceGroup.todoWorkspaces, todoWorkspace)
                .where(
                        workspaceNameInTodoWorkspaceEq(workspaceId),
                        basicTodoStatus(todoStatus)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Todo> countQuery = queryFactory
                .select(todo)
                .from(todo)
                .leftJoin(todo.todoWorkspaceGroup.todoWorkspaces, todoWorkspace)
                .where(
                    workspaceNameInTodoWorkspaceEq(workspaceId),
                    todoStatusEq(todoStatus)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }

    private BooleanExpression workspaceNameInTodoWorkspaceEq(Long workspaceId) {
        return workspaceId != null ? todoWorkspace.workspace.id.eq(workspaceId) : null;
    }

    private BooleanExpression basicTodoStatus(TodoStatus todoStatus) {
        return todoStatus != null ? basicTodo.status.eq(todoStatus) : null;
    }

    private BooleanExpression todoStatusEq(TodoStatus todoStatus) {
        return todoStatus != null ? todo.status.eq(todoStatus) : null;
    }
}
