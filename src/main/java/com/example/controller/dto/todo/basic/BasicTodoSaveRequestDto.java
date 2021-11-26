package com.example.controller.dto.todo.basic;

import com.example.controller.dto.todo.TodoSaveRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "기본 TODO 등록 요청 DTO")
@Getter
@NoArgsConstructor
public class BasicTodoSaveRequestDto extends TodoSaveRequestDto {
    @Schema(description = "예상 소요 시간(시간 단위)", required = true)
    private int expectedTime;

    @Builder
    public BasicTodoSaveRequestDto(Long memberId, Long workspaceId, String content, Long parentId, int expectedTime) {
        super(memberId, workspaceId, content, parentId);
        this.expectedTime = expectedTime;
    }

    public static BasicTodoSaveRequestDto create(Long memberId, Long workspaceId, String content, Long parentId, int expectedTime) {
        return BasicTodoSaveRequestDto.builder()
                .memberId(memberId)
                .workspaceId(workspaceId)
                .content(content)
                .parentId(parentId)
                .expectedTime(expectedTime)
                .build();
    }
}
