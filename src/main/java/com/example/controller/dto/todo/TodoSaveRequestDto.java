package com.example.controller.dto.todo;

import com.example.domain.member.Member;
import com.example.domain.todo.Todo;
import com.example.domain.todo.TodoStatus;
import com.example.domain.todo.TodoWorkspaceGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public abstract class TodoSaveRequestDto {

    @Schema(description = "회원 계정 고유 식별자", required = true)
    @NotNull
    private Long memberId;

    @Schema(description = "저장될 작업 공간 고유 식별자", required = true)
    @NotNull
    private Long workspaceId;

    @Schema(description = "할 일(Todo) 내용", maxLength = 50, required = true)
    @NotBlank
    private String content;

    @Schema(description = "상위 할 일(Todo) 고유 식별자", required = true)
    @NotNull
    private Long parentId;

    public TodoSaveRequestDto(Long memberId, Long workspaceId, String content, Long parentId) {
        this.memberId = memberId;
        this.workspaceId = workspaceId;
        this.content = content;
        this.parentId = parentId;
    }
}
