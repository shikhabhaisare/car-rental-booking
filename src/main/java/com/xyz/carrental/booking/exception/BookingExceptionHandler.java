package com.xyz.carrental.booking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for booking-related operations.
 * <p>
 * Handles validation errors, business exceptions, and generic internal server errors, returning structured error responses.
 */
@RestControllerAdvice
public class BookingExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BookingExceptionHandler.class);

    /**
     * Handles Spring's {@link MethodArgumentNotValidException} triggered by validation annotations.
     *
     * @param ex the validation exception
     * @return a structured error response with field-level and global validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationAnnotations(MethodArgumentNotValidException ex) {
        // Field-level errors
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "rejectedValue", err.getRejectedValue(),
                        "message", err.getDefaultMessage()
                ))
                .toList();

        // Class-level (global) errors
        var globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(err -> Map.of(
                        "object", err.getObjectName(),
                        "message", err.getDefaultMessage()
                ))
                .toList();

        Map<String, Object> response = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Failed",
                "message", "Request contains invalid fields",
                "fieldErrors", fieldErrors,
                "globalErrors", globalErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles custom {@link BookingValidationException}.
     *
     * @param ex the booking validation exception
     * @return a structured error response with the validation message
     */
    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<Map<String, Object>> handleBookingValidationException(BookingValidationException ex) {
        log.warn("Custom validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", Instant.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Validation Failed",
                        "message", ex.getMessage()
                )
        );
    }

    /**
     * Handles general {@link BookingException}.
     *
     * @param ex the booking exception
     * @return a structured error response with the exception message
     */
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, Object>> handleBookingException(BookingException ex) {
        log.warn("Booking error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Booking Error",
                        "message", ex.getMessage()
                ));
    }

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex the generic exception
     * @return a structured internal server error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Internal server error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                ));
    }
}
