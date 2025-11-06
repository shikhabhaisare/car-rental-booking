package com.xyz.carrental.booking.exception;

/**
 * General exception thrown for errors related to car bookings.
 * <p>
 * Used for unexpected business or system conditions during booking operations.
 */
public class BookingException extends RuntimeException {

    /**
     * Constructs a new {@code BookingException} with the specified detail message.
     *
     * @param message the detail message
     */
    public BookingException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code BookingException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param ex      the cause of the exception
     */
    public BookingException(String message, Exception ex){
        super(message, ex);
    }
}
