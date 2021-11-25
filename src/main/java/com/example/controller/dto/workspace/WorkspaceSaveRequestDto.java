package com.example.controller.dto.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "작업 공간 저장 요청 DTO")
@Getter
@NoArgsConstructor
public class WorkspaceSaveRequestDto {
    @Schema(description = "회원 고유 식별자", required = true)
    @NotNull
    private Long userId;

    @Schema(description = "작업 공간 이름", required = true)
    @NotBlank
    private String name;

    @Builder
    public WorkspaceSaveRequestDto(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static WorkspaceSaveRequestDto create(Long userId, String name) {
        return WorkspaceSaveRequestDto.builder()
                .userId(userId)
                .name(name)
                .build();
    }

}
