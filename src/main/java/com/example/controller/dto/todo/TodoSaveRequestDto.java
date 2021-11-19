package com.example.controller.dto.todo;

import com.example.domain.member.Member;
import com.example.domain.todo.Todo;
import com.example.domain.todo.TodoStatus;
import com.example.domain.todo.TodoWorkspaceGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public abstract class TodoSaveRequestDto {

    @NotNull
    private Long memberId;

    @NotNull
    private Long workspaceId;

    @NotBlank
    private String content;

    @NotNull
    private Long parentId;

    public TodoSaveRequestDto(Long memberId, Long workspaceId, String content, Long parentId) {
        this.memberId = memberId;
        this.workspaceId = workspaceId;
        this.content = content;
        this.parentId = parentId;
    }
}
