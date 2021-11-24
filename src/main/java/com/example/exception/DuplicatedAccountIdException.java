package com.example.exception;

public class DuplicatedAccountIdException extends RuntimeException {

    public DuplicatedAccountIdException(String message) {
        super(message);
    }
}
