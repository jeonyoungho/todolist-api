package com.example.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String accountId) {
        super("Could not found user with account id: " + accountId);
    }
}
