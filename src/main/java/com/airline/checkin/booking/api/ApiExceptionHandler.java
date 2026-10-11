package com.airline.checkin.booking.api;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatusException(ResponseStatusException exception) {
        HttpStatusCode status = exception.getStatusCode();
        String message = exception.getReason() == null ? "Request could not be completed." : exception.getReason();
        return ResponseEntity.status(status).body(new ApiError(status.toString(), message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? "Invalid input." : error.getDefaultMessage())
                .orElse("Invalid input.");
        return ResponseEntity.badRequest().body(new ApiError("400 BAD_REQUEST", message));
    }
}
