package com.xyz.carrental.booking.dto;

import com.xyz.carrental.booking.domain.CarSegment;
import com.xyz.carrental.booking.validation.ValidBookingDates;
import com.xyz.carrental.booking.validation.ValidLicenseNumber;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Request payload for confirming a car booking.
 * <p>
 * Includes customer details, booking dates, and selected car segment.
 * Validation annotations ensure proper input values.
 *
 * @param drivingLicenseNumber customer's valid driving license number
 * @param age                  customer's age (must be 18 or older)
 * @param startDate            booking start date
 * @param endDate              booking end date
 * @param carSegment           selected car segment
 */
@ValidBookingDates
public record ConfirmBookingRequest(
        @NotBlank(message = "licenseNumber is required")
        @ValidLicenseNumber
        String drivingLicenseNumber,
        @Min(value = 18, message = "Customer must be at least 18 years old")
        int age,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        LocalDate endDate,
        @NotNull(message = "Car segment is required")
        CarSegment carSegment
) {}
