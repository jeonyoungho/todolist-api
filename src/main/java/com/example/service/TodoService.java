package com.example.service;

import com.example.controller.dto.member.MemberResponseDto;
import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.todo.TodoSaveRequestDto;
import com.example.controller.dto.todo.TodoStatusUpdateRequestDto;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.todo.*;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final EntityManager em;

    @Transactional
    public Long saveBasicTodo(BasicTodoSaveRequestDto rq) {
        Long memberId = rq.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found member with id " + memberId));

        Todo parent = null;
        Long parentId = rq.getParentId();
        if (parentId != null && todoRepository.existsById(parentId)) {
            parent = (BasicTodo) todoRepository.findById(parentId).get();
        }

        Long workspaceId = rq.getWorkspaceId();
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found workspace with id " + workspaceId));
        TodoWorkspace todoWorkspace = TodoWorkspace.create(workspace);

        Todo todo = TodoFactory.createTodo(member, todoWorkspace, parent, rq);
        todoRepository.save(todo);
        return todo.getId();
    }

    @Transactional
    public void changeStatus(Long todoId, TodoStatusUpdateRequestDto rq) throws Throwable {
        TodoStatus status = rq.getStatus();
        if (TodoStatus.COMPLETED.equals(status)) {
            changeCompleteStatus(todoId, status);
            return;
        }

        Todo todo = (Todo) todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("Could not found todo with id " + todoId));

        todo.changeStatus(status);
    }

    private void changeCompleteStatus(Long todoId, TodoStatus status) {
        Todo todo = todoRepository.findByIdFetchJoinChilds(todoId);

        if(!todo.isAllChildCompleted()) {
            throw new IllegalArgumentException("todo with id " + todoId + " has not completed all sub-todos");
        }

        todo.changeStatus(status);
    }

    @Transactional
    public void delete(Long todoId) throws Throwable { // TODO: 쿼리 최적화 생각해 볼 것 (하위 Todo들을 전부 가져와서 전부 완료됐는지 체크해보는 쿼리를 날리고 delete 쿼리 날리고?)
//        Todo todo = (Todo) todoRepository.findById(todoId)
//                .orElseThrow(() -> new IllegalArgumentException("Could not found todo with id " + todoId));

        Todo todo = todoRepository.findByIdFetchJoinTodoWorkspaceGroupAndChilds(todoId);

        em.flush();
        em.clear();

        if(todo.childsSize() > 0) {
            todo.clearChilds();
        }

        em.flush();
        em.clear();

        todoRepository.deleteById(todoId);
    }
}
