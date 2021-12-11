package com.example.api.dto.todo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@SuperBuilder
@Getter
@NoArgsConstructor
public abstract class TodoSaveRequestDto {

    @Schema(description = "회원 계정 고유 식별자", required = true)
    @NotNull
    private Long memberId;

    @Schema(description = "저장될 작업 공간 고유 식별자", required = true)
    @NotNull
    private Long workspaceId;

    @Schema(description = "Todo 내용", maxLength = 50, required = true)
    @NotBlank
    private String content;

    @Schema(description = "상위 Todo 고유 식별자", required = true)
    @NotNull
    private Long parentId;
}
