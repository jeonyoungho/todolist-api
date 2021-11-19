package com.example.controller;

import com.example.controller.dto.todo.BasicTodoSaveRequestDto;
import com.example.controller.dto.todo.TodoSaveRequestDto;
import com.example.controller.dto.todo.TodoStatusUpdateRequestDto;
import com.example.domain.todo.TodoStatus;
import com.example.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class TodoApiController {

    private final TodoService todoService;

    @PostMapping("/todo/basic")
    public ResponseEntity<Long> addBasicTodo(@Valid @RequestBody BasicTodoSaveRequestDto rq) {
        return new ResponseEntity<>(todoService.saveBasicTodo(rq), HttpStatus.CREATED);
    }

    @PatchMapping("/todo/{todoId}")
    public ResponseEntity<Long> changeStatus(@PathVariable Long todoId, @Valid @RequestBody TodoStatusUpdateRequestDto rq) throws Throwable {
        return new ResponseEntity<>(todoService.changeStatus(todoId, rq), HttpStatus.OK);
    }

    @DeleteMapping("/todo/{todoId}")
    public ResponseEntity<Long> delete(@PathVariable Long todoId) throws Throwable {
        todoService.delete(todoId);
        return new ResponseEntity<>(todoId, HttpStatus.OK);
    }
}
