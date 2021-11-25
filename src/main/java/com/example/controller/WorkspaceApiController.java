package com.example.controller;

import com.example.controller.dto.member.MemberResponseDto;
import com.example.controller.dto.workspace.AddParticipantsRequestDto;
import com.example.controller.dto.workspace.WorkspaceResponseDto;
import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.exception.ErrorDetails;
import com.example.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Workspace", description = "작업공간 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class WorkspaceApiController {

    private final WorkspaceService workspaceService;

    @Operation(summary = "작업 공간 등록", description = "회원 고유 식별자와 작업 공간 이름을 인자로 받아 작업 공간을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작업 공간 등록 성공", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("/workspace")
    public ResponseEntity<Long> save(@Valid @RequestBody WorkspaceSaveRequestDto rq) {
        final Long savedWorkspaceId = workspaceService.saveWorkspace(rq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedWorkspaceId);
    }

    @Operation(summary = "작업 공간의 참가자 리스트 추가", description = "작업 공간 고유 식별자와 추가할 참가자 리스트를 인자로 받아 참가자 리스트를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "참가자 리스트 추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("/workspace/participant")
    public ResponseEntity<Void> addParticipants(@Valid @RequestBody AddParticipantsRequestDto rq) {
        workspaceService.addParticipants(rq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Operation(summary = "식별자로 단일 작업 공간 조회", description = "작업 공간 고유 식별자를 인자로 받아 단일 작업 공간 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단일 작업 공간 조회 성공", content = @Content(schema = @Schema(implementation = WorkspaceResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<WorkspaceResponseDto> findById(
            @Parameter(description = "작업 공간 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long workspaceId) {

        final WorkspaceResponseDto result = workspaceService.findById(workspaceId);
        return ResponseEntity.ok()
                .body(result);
    }

    @Operation(summary = "작업 공간에 속한 회원 리스트 조회", description = "작업 공간 고유 식별자를 인자로 받아 작업 공간에 속한 회원 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업 공간에 속한 회원 리스트 조회 성공", content = @Content(schema = @Schema(implementation = MemberResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("/workspace/{workspaceId}/members")
    public ResponseEntity<List<MemberResponseDto>> findMembersById(
            @Parameter(description = "작업 공간 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long workspaceId) {

        final List<MemberResponseDto> result = workspaceService.findMembersById(workspaceId);
        return ResponseEntity.ok()
                .body(result);
    }

    @Operation(summary = "회원의 작업 공간 리스트 조회", description = "회원 고유 식별자를 인자로 받아 해당 회원의 작업 공간 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원의 작업 공간 리스트 조회 성공", content = @Content(schema = @Schema(implementation = WorkspaceResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("/workspace/all/{memberId}")
    public ResponseEntity<List<WorkspaceResponseDto>> findAllByMemberId(
            @Parameter(description = "회원 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long memberId) {

        final List<WorkspaceResponseDto> result = workspaceService.findAllByMemberId(memberId);
        return ResponseEntity.ok()
                .body(result);
    }

    @Operation(summary = "식별자로 단일 작업 공간 삭제", description = "작업 공간 고유 식별자를 인자로 받아 해당 작업 공간을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "식별자로 단일 작업 공간 삭제"),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @DeleteMapping("/workspace/{workspaceId}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "작업 공간 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long workspaceId) {

        workspaceService.deleteById(workspaceId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "회원 고유 식별자와 작업 공간 고유 식별자로 참가자 리스트에서 삭제", description = "회원 고유 식별자와 작업 공간 고유 식별자를 인자로 받아 참가자 리스트에서 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 고유 식별자와 작업 공간 고유 식별자로 참가자 리스트에서 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @DeleteMapping("/worksapce/{memberId}/{workspaceId}")
    public ResponseEntity<Void> deleteParticipantByMemberId(
            @Parameter(description = "회원 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long memberId,
            @Parameter(description = "작업 공간 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long workspaceId) {

        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


}
