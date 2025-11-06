package com.xyz.carrental.booking.dto;

import java.util.UUID;

/**
 * Response payload returned after successfully confirming a booking.
 *
 * @param bookingId unique identifier of the newly created booking
 */
public record ConfirmBookingResponse(UUID bookingId) {}
