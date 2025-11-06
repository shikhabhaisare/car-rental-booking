package com.xyz.carrental.booking.service;

import com.xyz.carrental.booking.dto.BookingDetailsResponse;
import com.xyz.carrental.booking.dto.ConfirmBookingRequest;

import java.util.UUID;

/**
 * Service interface for managing car bookings.
 * <p>
 * Defines operations for confirming a booking and retrieving booking details.
 */
public interface BookingService {

    /**
     * Confirms a new car booking based on the provided request.
     *
     * @param request the booking confirmation request containing customer and booking details
     * @return the unique ID of the confirmed booking
     */
    UUID confirmBooking(ConfirmBookingRequest request);

    /**
     * Retrieves detailed information for a booking by its ID.
     *
     * @param id the unique identifier of the booking
     * @return the booking details response
     */
    BookingDetailsResponse getBookingDetails(UUID id);
}
