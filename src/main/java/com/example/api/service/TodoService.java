package com.example.api.service;

import com.example.api.dto.todo.TodoStatusUpdateRequestDto;
import com.example.api.dto.todo.basic.BasicTodoResponseDto;
import com.example.api.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.BasicTodo;
import com.example.domain.todo.Todo;
import com.example.domain.todo.TodoRepository;
import com.example.domain.todo.TodoStatus;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.exception.CustomException;
import com.example.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public Long saveBasicTodo(BasicTodoSaveRequestDto rq) {
        Member member = memberRepository.findById(rq.getMemberId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        SecurityUtil.checkValidRequest(member.getAccountId());

        Todo parentTodo = getParentTodo(rq.getParentId());

        Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
                .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        Todo todo = BasicTodo.create(member, workspace, rq.getContent(), parentTodo, rq.getExpectedTime());
        todoRepository.save(todo);

        return todo.getId();
    }

    private Todo getParentTodo(Long parentId) {
        if (parentId == null) {
            return null;
        }

        Optional optional = todoRepository.findById(parentId);
        if (!optional.isPresent()) {
            throw new CustomException(TODO_NOT_FOUND);
        }

        Todo parentTodo = (Todo) optional.get();
        if (parentTodo.hasParent()) { // 상위 Todo 하나만을 가질 수 있기에 만약 부모 Todo 가 부모를 가지고 있을 경우 예외 발생
            throw new CustomException(INVALID_PARENT_TODO);
        }

        return parentTodo;
    }

    public Page<BasicTodoResponseDto> findAllBasicTodos(Pageable pageable, Long workspaceId, TodoStatus status) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new CustomException(WORKSPACE_NOT_FOUND);
        }

        if (!workspaceRepository.existsByIdAndCurrentAccountId(workspaceId, SecurityUtil.getCurrentAccountId())) {
            throw new CustomException(INVALID_REQUEST);
        }

        return todoRepository.findAllBasicTodos(pageable, workspaceId, status);
    }


    @Transactional
    public void changeStatus(Long todoId, TodoStatusUpdateRequestDto rq) {
        TodoStatus status = rq.getStatus();
        Todo todo;
        if (TodoStatus.COMPLETED.isEqualTo(status)) {
            todo = todoRepository.findByIdFetchJoinMemberAndChilds(todoId)
                    .orElseThrow(() -> new CustomException(TODO_NOT_FOUND));

            checkAllChildsCompleted(todo);
        } else {
            todo = todoRepository.findByIdFetchJoinMember(todoId)
                    .orElseThrow(() -> new CustomException(TODO_NOT_FOUND));
        }

        SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

        todo.changeStatus(status);
    }

    private void checkAllChildsCompleted(Todo todo) {
        if(!todo.isAllChildsCompleted()) {
            throw new CustomException(ALL_CHILD_TODO_NOT_COMPLETED);
        }
    }

    @Transactional
    public void delete(Long todoId) {
        Todo todo = todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(todoId)
                .orElseThrow(() -> new CustomException(TODO_NOT_FOUND));

        SecurityUtil.checkValidRequest(todo.getMember().getAccountId());

        todo.clearChilds();

        todoRepository.deleteById(todoId);
    }
}
