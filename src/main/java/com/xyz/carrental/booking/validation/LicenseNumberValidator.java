package com.xyz.carrental.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates the format of a driving license number.
 * <p>
 * Accepted format: 5-20 alphanumeric characters, including underscores or hyphens.
 */
public class LicenseNumberValidator implements ConstraintValidator<ValidLicenseNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.matches("^[A-Za-z0-9_-]{5,20}$");
    }
}
