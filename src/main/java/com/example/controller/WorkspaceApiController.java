package com.example.controller;

import com.example.controller.dto.member.MemberResponseDto;
import com.example.controller.dto.workspace.WorkspaceResponseDto;
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
    public ResponseEntity<Long> save(@Valid @RequestBody WorkspaceSaveRequestDto rq) {
        return new ResponseEntity<>(workspaceService.saveWorkspace(rq), HttpStatus.CREATED);
    }

    @PostMapping("/workspace/participant")
    public ResponseEntity<Long> addParticipants(@Valid @RequestBody AddParticipantsRequestDto rq) {
        return new ResponseEntity<>(workspaceService.addParticipants(rq), HttpStatus.CREATED);
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDto> findById(@PathVariable Long workspaceId) {
        return new ResponseEntity<>(workspaceService.findById(workspaceId), HttpStatus.OK);
    }

    @GetMapping("/workspace/members/{workspaceId}")
    public ResponseEntity<List<MemberResponseDto>> findMembersById(@PathVariable Long workspaceId) {
        return new ResponseEntity<>(workspaceService.findMembersById(workspaceId), HttpStatus.OK);
    }

    @GetMapping("/workspace/{memberId}")
    public ResponseEntity<List<WorkspaceResponseDto>> findAllByMemberId(@PathVariable Long memberId) {
        return new ResponseEntity<>(workspaceService.findAllByMemberId(memberId), HttpStatus.OK);
    }

    @DeleteMapping("/workspace/{workspaceId}")
    public ResponseEntity<Long> deleteById(@PathVariable Long workspaceId) {
        return new ResponseEntity<>(workspaceService.deleteById(workspaceId), HttpStatus.OK);
    }

    @DeleteMapping("/worksapce/{memberId}/{workspaceId}")
    public ResponseEntity<Long> deleteParticipantByMemberId(@PathVariable Long memberId, @PathVariable Long workspaceId) {
        return new ResponseEntity<>(workspaceService.deleteParticipantByMemberId(memberId, workspaceId), HttpStatus.OK);
    }


}
