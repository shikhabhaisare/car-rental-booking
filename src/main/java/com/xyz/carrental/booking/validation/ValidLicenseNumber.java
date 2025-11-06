package com.xyz.carrental.booking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation to validate the format of a driving license number.
 * <p>
 * Applied on fields or method parameters containing a driving license number.
 */
@Documented
@Constraint(validatedBy = {LicenseNumberValidator.class})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLicenseNumber {
    String message() default "Invalid driving license number format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
