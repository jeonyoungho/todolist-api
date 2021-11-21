package com.example.controller.dto.workspace;

import com.example.domain.workspace.Workspace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "작업 공간 응답 DTO")
@Getter
public class WorkspaceResponseDto {

    @Schema(description = "작업 공간 고유 식별자", required = true)
    private Long id;

    @Schema(description = "작업 공간 이름", required = true)
    private String name;

    public WorkspaceResponseDto(Workspace entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
