package com.example.controller;

import com.example.controller.dto.workspace.WorkspaceListResponseDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class WorkspaceApiController {

    private final WorkspaceService workspaceService;

    @PostMapping("/workspace")
    public ResponseEntity<Long> saveWorkspace(@Valid @RequestBody WorkspaceSaveRequestDto rq) {
        return new ResponseEntity<>(workspaceService.saveWorkspace(rq), HttpStatus.CREATED);

    }

    @PostMapping("/workspace/participant")
    public ResponseEntity<Long> addParticipants(AddParticipantsRequestDto rq) {
        return new ResponseEntity<>(workspaceService.addParticipants(rq), HttpStatus.CREATED);
    }

    @GetMapping("/workspaces/{memberId}")
    public ResponseEntity<List<WorkspaceListResponseDto>> findWorkspacesByMemberId(@PathVariable Long memberId) {
        return new ResponseEntity<>(workspaceService.findAllByMemberId(memberId), HttpStatus.OK);
    }

}
