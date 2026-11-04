package com.sentinel.player.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            PlayerNotFoundException exception
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler({
            DuplicatePlayerException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(
            Exception exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", exception instanceof DuplicatePlayerException
                        ? exception.getMessage()
                        : "A player with the supplied unique details already exists"));
    }

    @ExceptionHandler(InvalidPlayerRequestException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(
            InvalidPlayerRequestException exception
    ) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        return ResponseEntity.badRequest()
                .body(Map.of("error", message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", exception.getMessage()));
    }

}
