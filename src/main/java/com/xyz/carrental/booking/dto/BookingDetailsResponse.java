package com.xyz.carrental.booking.dto;

import com.xyz.carrental.booking.domain.CarSegment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload containing detailed booking information.
 *
 * @param bookingId            unique identifier of the booking
 * @param drivingLicenseNumber customer's driving license number
 * @param customerName         name of the customer who made the booking
 * @param age                  age of the customer
 * @param startDate            booking start date
 * @param endDate              booking end date
 * @param carSegment           booked car segment
 * @param rentalPrice          total rental price for the booking
 */
public record BookingDetailsResponse(
        UUID bookingId,
        String drivingLicenseNumber,
        String customerName,
        int age,
        LocalDate startDate,
        LocalDate endDate,
        CarSegment carSegment,
        BigDecimal rentalPrice
) {}

