package com.sentinel.cases.exception;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            CaseNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidCaseStatusTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTransition(
            InvalidCaseStatusTransitionException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            Exception exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request: " + exception.getMessage()
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String message) {

        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", Instant.now(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                )
        );
    }
}