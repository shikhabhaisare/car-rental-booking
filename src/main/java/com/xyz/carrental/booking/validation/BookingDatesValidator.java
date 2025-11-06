package com.xyz.carrental.booking.validation;

import com.xyz.carrental.booking.dto.ConfirmBookingRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.temporal.ChronoUnit;

/**
 * Validates that booking dates in {@link ConfirmBookingRequest} are valid.
 * <p>
 * Rules:
 * - Start and end dates must not be null
 * - Start date must be before or equal to end date
 * - Booking duration cannot exceed 30 days
 */
public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, ConfirmBookingRequest> {

    @Override
    public boolean isValid(ConfirmBookingRequest req, ConstraintValidatorContext context) {
        if (req.startDate() == null || req.endDate() == null) return false;
        if (req.startDate().isAfter(req.endDate())) return false;
        long days = ChronoUnit.DAYS.between(req.startDate(), req.endDate()) + 1;
        return days <= 30;
    }
}
