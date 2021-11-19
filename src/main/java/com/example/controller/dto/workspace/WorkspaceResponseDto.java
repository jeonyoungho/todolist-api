package com.example.controller.dto.workspace;

import com.example.domain.workspace.Workspace;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WorkspaceResponseDto {

    private Long id;
    private String name;

    public WorkspaceResponseDto(Workspace entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
