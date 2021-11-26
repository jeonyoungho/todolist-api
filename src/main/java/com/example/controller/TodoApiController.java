package com.example.controller;

import com.example.controller.dto.todo.TodoStatusUpdateRequestDto;
import com.example.controller.dto.todo.basic.BasicTodoResponseDto;
import com.example.controller.dto.todo.basic.BasicTodoSaveRequestDto;
import com.example.domain.todo.TodoStatus;
import com.example.exception.ErrorDetails;
import com.example.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Todo", description = "Todo) API")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class TodoApiController {

    private final TodoService todoService;

    @Operation(summary = "기본 Todo 등록", description = "기본 Basic-Todo와 관련된 값들을 파라미터로 받아 할 일을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "기본 Todo 등록 성공", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "부모 Todo가 또 다른 부모 Todo를 가지게됨", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("/todo/basic")
    public ResponseEntity<Long> saveBasicTodo(@Valid @RequestBody BasicTodoSaveRequestDto rq) {
        final Long savedTodoId = todoService.saveBasicTodo(rq);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTodoId);
    }

    @Operation(summary = "Todo 상태 변경", description = "Todo의 고유 식별자와 변경할 상태 값을 파라미터로 받아 Todo의 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo의 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "하위 Todo들이 전체 완료되지 않은 요청"),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PatchMapping("/todo/{todoId}")
    public ResponseEntity<Void> changeStatus(@PathVariable Long todoId, @Valid @RequestBody TodoStatusUpdateRequestDto rq) throws Throwable {
        todoService.changeStatus(todoId, rq);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "Todo 상태별로 필터링 및 페이징을 적용하여 조회", description = "작업 공간 고유 식별자와 Todo 상태 및 페이징 값을 파라미터로 받아 Todo를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo 상태별로 필터링 및 페이징을 적용하여 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("/todos/basic")
    public ResponseEntity<Page<BasicTodoResponseDto>> findAllBasicTodos(
            @Parameter(description = "작업 공간 고유 식별자", schema = @Schema(implementation = Long.class)) @RequestParam Long workspaceId,
            @Parameter(description = "필터링을 적용할 Todo 상태", schema = @Schema(implementation = TodoStatus.class)) @RequestParam(required = false) TodoStatus status,
            @Parameter(description = "페이징 번호", schema = @Schema(implementation = Integer.class)) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이징 갯수", schema = @Schema(implementation = Integer.class)) @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BasicTodoResponseDto> results = todoService.findAllBasicTodos(pageable, workspaceId, status);

        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Todo 삭제", description = "Todo의 고유 식별자를 파라미터로 받아 Todo를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))

    })
    @DeleteMapping("/todo/{todoId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Todo의 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long todoId) {
        todoService.delete(todoId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
