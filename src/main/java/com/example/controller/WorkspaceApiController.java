package com.example.controller;

import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class WorkspaceApiController {

    private final WorkspaceService workspaceService;

    @PostMapping("/workspace")
    public Long save(@RequestBody WorkspaceSaveRequestDto request) {
        return workspaceService.save(request);
    }
}
