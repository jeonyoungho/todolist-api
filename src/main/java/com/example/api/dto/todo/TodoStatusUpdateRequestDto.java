package com.example.api.dto.todo;

import com.example.domain.todo.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Schema(description = "Todo 상태 변경 요청 DTO")
@Getter
public class TodoStatusUpdateRequestDto {
    @Schema(description = "변경 할 Todo 상태명", required = true, allowableValues = {"UNCOMPLETED", "COMPLETED"})
    @NotNull
    TodoStatus status;

    @Builder
    public TodoStatusUpdateRequestDto(TodoStatus status) {
        this.status = status;
    }
}
