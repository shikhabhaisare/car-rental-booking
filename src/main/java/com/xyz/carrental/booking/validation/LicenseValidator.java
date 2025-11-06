package com.xyz.carrental.booking.validation;

import com.xyz.carrental.booking.exception.BookingValidationException;
import com.xyz.carrental.booking.stub.model.LicenseResponse;
import com.xyz.carrental.booking.exception.BookingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Validates a driving license retrieved from external sources.
 * <p>
 * Rules:
 * - License must not be null
 * - Expiry date must be in the future
 * - License must be at least 1 year old
 */
@Slf4j
@Component
public class LicenseValidator {

    /**
     * Validates the provided license response.
     *
     * @param license the license to validate
     * @throws BookingException if license is invalid
     */
    public void validateLicense(LicenseResponse license) {
        log.debug("Validating license");
        LocalDate now = LocalDate.now();

        if (license == null || license.expiryDate() == null ||
            !license.expiryDate().isAfter(now) ||
            license.expiryDate().minusYears(10).isAfter(now.minusYears(1))) {
            log.warn("License validation failed");
            throw new BookingException("Please provide valid License: Driving License must be at" +
                    " least 1 year old or Driving License has been expired");
        }

        log.info("License validation passed for owner={}", license.ownerName());
    }


}
