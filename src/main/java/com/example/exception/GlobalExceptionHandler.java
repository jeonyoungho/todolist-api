package com.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import static com.example.exception.ErrorCode.DUPLICATE_RESOURCE;
import static com.example.exception.ErrorCode.NOT_MISMATCH_ACCOUNT;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorDetails> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorDetails.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = {ConstraintViolationException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<ErrorDetails> handleHibernateException() {
        log.error("handleHibernateException throw Exception : {}", DUPLICATE_RESOURCE);
        return ErrorDetails.toResponseEntity(DUPLICATE_RESOURCE);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    protected ResponseEntity<ErrorDetails> handleBadCredentialsException() {
        log.error("handleBadCredentialsException throw Exception : {}", NOT_MISMATCH_ACCOUNT);
        return ErrorDetails.toResponseEntity(NOT_MISMATCH_ACCOUNT);
    }

}