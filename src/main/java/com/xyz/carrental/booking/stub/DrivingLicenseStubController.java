package com.xyz.carrental.booking.stub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * stub for Driving License API
 * Implements POST /license/details according to OpenAPI spec.
 */
@Slf4j
@RestController
@RequestMapping("/stub/driving")
public class DrivingLicenseStubController {

    private static final Map<String, Map<String, Object>> LICENSE_DB = new HashMap<>();

    static {
        // Valid license (issued 10 years ago)
        LICENSE_DB.put("DL123456789", Map.of(
                "ownerName", "John Doe",
                "expiryDate", LocalDate.now().plusYears(2)  // expiry in future
        ));

        // License too new (< 1 year old)
        LICENSE_DB.put("DL456789123", Map.of(
                "ownerName", "Alice Smith",
                "expiryDate", LocalDate.now().plusYears(10)  //  → issue < 1 year
        ));

        // Expired license
        LICENSE_DB.put("DL999888777", Map.of(
                "ownerName", "Bob Johnson",
                "expiryDate", LocalDate.now().minusMonths(1)  // expired
        ));
    }

    @PostMapping("/license/details")
    public ResponseEntity<?> getLicenseDetails(@RequestBody Map<String, String> request) {
        String licenseNumber = request.get("licenseNumber");
        log.info("License details requested from external api");

        if (licenseNumber == null || licenseNumber.isBlank()) {
            log.warn("License number missing in request");
            return ResponseEntity.badRequest().body(Map.of("error", "licenseNumber is required"));
        }

        Map<String, Object> license = LICENSE_DB.get(licenseNumber);
        if (license == null) {
            log.warn("License not found: {}", licenseNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Driving license not found"));
        }

        LocalDate expiry = (LocalDate) license.get("expiryDate");
        if (expiry.isBefore(LocalDate.now())) {
            log.warn("License has been expired: {}", licenseNumber);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Input/ Driving license expired"));
        }

        log.info("Returning license details ");
        return ResponseEntity.ok(Map.of(
                "ownerName", license.get("ownerName"),
                "expiryDate", expiry.toString()
        ));
    }
}

