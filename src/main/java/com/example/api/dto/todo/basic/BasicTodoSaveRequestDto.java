package com.example.api.dto.todo.basic;

import com.example.api.dto.todo.TodoSaveRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Schema(description = "기본 TODO 등록 요청 DTO")
@Getter
@NoArgsConstructor
public class BasicTodoSaveRequestDto extends TodoSaveRequestDto {
    @Schema(description = "예상 소요 시간(시간 단위)", required = true)
    private int expectedTime;

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
