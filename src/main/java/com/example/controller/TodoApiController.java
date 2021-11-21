package com.example.controller;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.todo.TodoStatusUpdateRequestDto;
import com.example.service.TodoService;
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

@Tag(name = "Todo", description = "할 일(Todo) API")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class TodoApiController {

    private final TodoService todoService;

    @Operation(summary = "기본 할 일(Basic-Todo) 등록", description = "기본 할 일(Basic-Todo)과 관련된 값들을 파라미터로 받아 할 일을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "기본 할 일(Todo) 등록 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    @PostMapping("/todo/basic")
    public ResponseEntity<Long> addBasicTodo(@Valid @RequestBody BasicTodoSaveRequestDto rq) {
        Long savedTodoId = todoService.saveBasicTodo(rq);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTodoId);
    }

    @Operation(summary = "할 일(Todo) 상태 변경", description = "할 일(Todo)의 고유 식별자와 변경할 상태 값을 파라미터로 받아 할 일(Todo)의 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "할 일(Todo)의 상태 변경 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근")
    })
    @PatchMapping("/todo/{todoId}")
    public ResponseEntity<Void> changeStatus(@PathVariable Long todoId, @Valid @RequestBody TodoStatusUpdateRequestDto rq) throws Throwable {
        todoService.changeStatus(todoId, rq);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "할 일(Todo) 삭제", description = "할 일(Todo)의 고유 식별자를 파라미터로 받아 할 일(Todo)을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "할 일(Todo) 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근")
    })
    @DeleteMapping("/todo/{todoId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "할 일(Todo)의 고유 식별자", schema = @Schema(implementation = Long.class))
            @PathVariable Long todoId) throws Throwable {
        todoService.delete(todoId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
