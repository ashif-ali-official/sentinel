package com.sentinel.appeals.controller;
import com.sentinel.appeals.service.*;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
@RestControllerAdvice
public class AppealExceptionHandler {
    @ExceptionHandler({AppealNotFoundException.class,AppealableSanctionNotFoundException.class}) public ResponseEntity<Map<String,Object>> missing(RuntimeException e){return body(HttpStatus.NOT_FOUND,e.getMessage());}
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,Object>> invalid(RuntimeException e){return body(HttpStatus.BAD_REQUEST,e.getMessage());}
    @ExceptionHandler({IllegalStateException.class,ObjectOptimisticLockingFailureException.class}) public ResponseEntity<Map<String,Object>> conflict(RuntimeException e){return body(HttpStatus.CONFLICT,e.getMessage());}
    private ResponseEntity<Map<String,Object>> body(HttpStatus s,String m){return ResponseEntity.status(s).body(Map.of("timestamp",Instant.now(),"status",s.value(),"error",s.getReasonPhrase(),"message",m));}
}
