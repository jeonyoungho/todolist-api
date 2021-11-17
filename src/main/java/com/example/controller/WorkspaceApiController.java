package com.example.controller;

import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class WorkspaceApiController {

    private final WorkspaceService workspaceService;

    @GetMapping("/workspace")
    public Long createWorkspace(WorkspaceSaveRequestDto request) {
        return workspaceService.saveWorkspace(request);
    }
}
