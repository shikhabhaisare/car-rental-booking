package com.xyz.carrental.booking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation to validate booking duration in {@link com.xyz.carrental.booking.dto.ConfirmBookingRequest}.
 * <p>
 * Ensures reservation duration does not exceed 30 days.
 */
@Documented
@Constraint(validatedBy = BookingDatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBookingDates {
    String message() default "Reservation duration cannot exceed 30 days";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
