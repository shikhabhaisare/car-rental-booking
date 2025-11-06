package com.xyz.carrental.booking.service;

import com.xyz.carrental.booking.client.CarPricingClient;
import com.xyz.carrental.booking.client.DrivingLicenseClient;
import com.xyz.carrental.booking.dto.BookingDetailsResponse;
import com.xyz.carrental.booking.dto.ConfirmBookingRequest;
import com.xyz.carrental.booking.entity.Booking;
import com.xyz.carrental.booking.exception.BookingException;
import com.xyz.carrental.booking.mapper.BookingMapper;
import com.xyz.carrental.booking.repository.BookingRepository;
import com.xyz.carrental.booking.validation.LicenseValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service implementation for managing car bookings.
 * <p>
 * Handles booking confirmation, pricing calculation, license validation, and retrieval of booking details.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final DrivingLicenseClient drivingLicenseClient;
    private final CarPricingClient carPricingClient;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final LicenseValidator licenseValidator;


    /**
     * Confirms a new car booking.
     * <p>
     * Validates the driving license, calculates the total rental price, and saves the booking record.
     *
     * @param req the booking confirmation request
     * @return the unique ID of the confirmed booking
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public UUID confirmBooking(ConfirmBookingRequest req) {
        log.info("Confirming booking for drivingLicenseNumber={} and carSegment={}", req.drivingLicenseNumber(), req.carSegment());

        var license = drivingLicenseClient.getLicenseDetails(req.drivingLicenseNumber());
        log.debug("License details retrieved");
        licenseValidator.validateLicense(license);
        log.info("License validated successfully");

        var rateResponse = carPricingClient.getRateForCategory(req.carSegment().name());
        log.debug("Rate retrieved: {}", rateResponse);

        long days = ChronoUnit.DAYS.between(req.startDate(), req.endDate()) + 1;
        BigDecimal total = rateResponse.ratePerDay().multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);
        log.debug("Total price calculated: {}", total);

        Booking booking = bookingRepository.save(bookingMapper.toBooking(req, license.ownerName(), total));
        log.info("Booking saved successfully with bookingId={}", booking.getId());

        return booking.getId();
    }

    /**
     * Retrieves booking details for the given booking ID.
     *
     * @param id the unique booking identifier
     * @return the booking details response
     * @throws BookingException if the booking ID is not found
     */
    @Override
    public BookingDetailsResponse getBookingDetails(UUID id) {
        log.info("Fetching booking details for bookingId={}", id);

        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BookingException("Car Rental Booking details not found: " + id));
        log.debug("Booking details retrieved: {}", booking);

        return bookingMapper.toResponse(booking);
    }
}
