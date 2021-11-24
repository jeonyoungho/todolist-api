package com.example.exception;

public class TodoInvalidUpdateException extends RuntimeException {
    public TodoInvalidUpdateException(String message) {
        super(message);
    }
}
