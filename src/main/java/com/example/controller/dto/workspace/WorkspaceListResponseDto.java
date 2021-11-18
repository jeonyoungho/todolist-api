package com.example.controller.dto.workspace;

import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import lombok.Getter;

import java.util.List;

@Getter
public class WorkspaceListResponseDto {

    private Long id;
    private String name;

    public WorkspaceListResponseDto(Workspace entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
