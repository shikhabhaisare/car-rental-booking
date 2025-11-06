package com.xyz.carrental.booking.exception;

/**
 * Exception thrown for custom booking validation errors.
 * <p>
 * Examples include invalid driving license, reservation exceeding allowed duration, or other business rules.
 */
public class BookingValidationException extends RuntimeException {

    /**
     * Constructs a new {@code BookingValidationException} with the specified detail message.
     *
     * @param message the detail message
     */
    public BookingValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code BookingValidationException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public BookingValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
