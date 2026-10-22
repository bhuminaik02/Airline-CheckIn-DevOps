package com.airline.checkin.booking.api;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.hibernate.exception.ConstraintViolationException;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrityViolation(DataIntegrityViolationException exception) {
        String constraintName = findConstraintName(exception);
        String message;
        if (constraintName != null && constraintName.toLowerCase(java.util.Locale.ROOT)
                .contains("uk_flight_seat_reservation")) {
            message = "Seat is already assigned on this flight.";
        } else if (constraintName != null && constraintName.toLowerCase(java.util.Locale.ROOT)
                .contains("uk_bookings_reference")) {
            message = "That booking reference is already in use.";
        } else {
            throw exception;
        }
        return ResponseEntity.status(409).body(new ApiError("409 CONFLICT", message));
    }

    private String findConstraintName(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                return violation.getConstraintName();
            }
        }
        return null;
    }
}
