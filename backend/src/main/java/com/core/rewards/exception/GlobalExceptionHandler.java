package com.core.rewards.exception;

import com.core.rewards.dto.ActionTrackerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ActionTrackerResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String traceId = "ERR-" + UUID.randomUUID().toString().substring(0, 8);
        
        String validationMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request payload parameters.");

        ActionTrackerResponse response = new ActionTrackerResponse(traceId, "BAD_REQUEST", validationMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ActionTrackerResponse> handleGenericErrors(Exception ex) {
        String traceId = "ERR-" + UUID.randomUUID().toString().substring(0, 8);
        ActionTrackerResponse response = new ActionTrackerResponse(traceId, "INTERNAL_ERROR", "An unexpected error occurred during processing.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
