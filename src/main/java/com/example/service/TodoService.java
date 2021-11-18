package com.example.service;

import com.example.domain.todo.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public void save() {

    }

}
