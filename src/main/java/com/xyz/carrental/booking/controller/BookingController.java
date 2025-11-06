package com.xyz.carrental.booking.controller;

import com.xyz.carrental.booking.dto.BookingDetailsResponse;
import com.xyz.carrental.booking.dto.ConfirmBookingRequest;
import com.xyz.carrental.booking.dto.ConfirmBookingResponse;
import com.xyz.carrental.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for handling car booking operations.
 * Provides endpoints to confirm and retrieve booking details.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    /**
     * Confirms a new car booking request.
     *
     * @param request the booking confirmation request payload
     * @return {@link ConfirmBookingResponse} containing the generated booking ID
     */
    @PostMapping
    public ResponseEntity<ConfirmBookingResponse> confirmBooking(@Valid @RequestBody ConfirmBookingRequest request) {
        log.info("Received booking request for carSegment: {}", request.carSegment());
        UUID bookingId = bookingService.confirmBooking(request);
        log.info("Booking confirmed successfully with bookingId={}", bookingId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ConfirmBookingResponse(bookingId));
    }

    /**
     * Retrieves booking details by booking ID.
     *
     * @param id the unique booking identifier
     * @return {@link BookingDetailsResponse} containing booking information
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDetailsResponse> getBookingDetails(@PathVariable("id") UUID id) {
        log.info("Fetching booking details for bookingId={}", id);
        BookingDetailsResponse resp = bookingService.getBookingDetails(id);
        log.debug("Booking details retrieved: {}", resp);
        return ResponseEntity.ok(resp);
    }
}
