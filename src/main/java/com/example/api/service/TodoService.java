package com.example.api.service;

import com.example.api.dto.todo.TodoStatusUpdateRequestDto;
import com.example.api.dto.todo.basic.BasicTodoResponseDto;
import com.example.api.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.*;
import com.example.domain.workspace.ParticipantGroup;
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
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public Long saveBasicTodo(BasicTodoSaveRequestDto rq) {
        Member member = memberRepository.findById(rq.getMemberId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        SecurityUtil.checkAuthority(member.getAccountId());

        Todo parentTodo = getParentTodo(rq.getParentId());

        Workspace workspace = workspaceRepository.findById(rq.getWorkspaceId())
                .orElseThrow(() -> new CustomException(WORKSPACE_NOT_FOUND));

        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);

        Todo todo = TodoFactory.createTodo(member, todoWorkspace, parentTodo, rq);
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
        if (parentTodo.hasParent()) { // 만약 부모 Todo 가 부모를 가지고 있을 경우 예외 발생
            throw new CustomException(INVALID_PARENT_TODO);
        }

        return parentTodo;
    }

    @Transactional(readOnly = true)
    public Page<BasicTodoResponseDto> findAllBasicTodos(Pageable pageable, Long workspaceId, TodoStatus status) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new CustomException(WORKSPACE_NOT_FOUND);
        }

        Workspace workspace = workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId);
        ParticipantGroup participantGroup = workspace.getParticipantGroup();
        if (!participantGroup.isExistByAccountId(SecurityUtil.getCurrentAccountId())) {
            throw new CustomException(UNAUTHORIZED_MEMBER);
        }

        return todoRepository.findAllBasicTodos(pageable, workspaceId, status);
    }


    @Transactional
    public void changeStatus(Long todoId, TodoStatusUpdateRequestDto rq) {
        TodoStatus status = rq.getStatus();
        if (TodoStatus.COMPLETED.equals(status)) {
            changeCompleteStatus(todoId, status);
            return;
        }

        Todo todo = todoRepository.findByIdFetchJoinMember(todoId);

        SecurityUtil.checkAuthority(todo.getMember().getAccountId());

        todo.changeStatus(status);
    }

    private void changeCompleteStatus(Long todoId, TodoStatus status) {
        Todo todo = todoRepository.findByIdFetchJoinMemberAndChilds(todoId);
        if(!todo.isAllChildsCompleted()) {
            throw new CustomException(ALL_CHILD_TODO_NOT_COMPLETED);
        }

        SecurityUtil.checkAuthority(todo.getMember().getAccountId());

        todo.changeStatus(status);
    }

    @Transactional
    public void delete(Long todoId) {
        if (!todoRepository.existsById(todoId)) {
            throw new CustomException(TODO_NOT_FOUND);
        }

        Todo todo = todoRepository.findByIdFetchJoinMemberAndTodoWorkspaceGroupAndChilds(todoId);

        SecurityUtil.checkAuthority(todo.getMember().getAccountId());

        if(todo.childsSize() > 0) {
            todo.clearChilds();
        }

        todoRepository.deleteById(todoId);
    }
}
