package com.example.controller.dto.todo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class BasicTodoSaveRequestDto extends TodoSaveRequestDto {
    private int expectedTime;

    @Builder
    public BasicTodoSaveRequestDto(Long memberId, Long workspaceId, String content, Long parentId, int expectedTime) {
        super(memberId, workspaceId, content, parentId);
        this.expectedTime = expectedTime;
    }
}
