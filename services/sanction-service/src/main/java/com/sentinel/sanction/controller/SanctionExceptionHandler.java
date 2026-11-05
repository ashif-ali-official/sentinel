package com.sentinel.sanction.controller;

import com.sentinel.sanction.service.SanctionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class SanctionExceptionHandler {
    @ExceptionHandler(SanctionNotFoundException.class) public ResponseEntity<Map<String,Object>> notFound(RuntimeException e) { return body(HttpStatus.NOT_FOUND,e.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,Object>> badRequest(RuntimeException e) { return body(HttpStatus.BAD_REQUEST,e.getMessage()); }
    @ExceptionHandler({IllegalStateException.class,ObjectOptimisticLockingFailureException.class}) public ResponseEntity<Map<String,Object>> conflict(RuntimeException e) { return body(HttpStatus.CONFLICT,e.getMessage()); }
    private ResponseEntity<Map<String,Object>> body(HttpStatus s,String m) { return ResponseEntity.status(s).body(Map.of("timestamp", Instant.now(),"status",s.value(),"error",s.getReasonPhrase(),"message",m)); }
}
