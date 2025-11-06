package com.xyz.carrental.booking.stub;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

/**
 * stub for Car Rental Pricing API
 * Implements POST /rental/rate according to OpenAPI spec.
 */
@RestController
@RequestMapping("/stub/pricing")
public class PricingStubController {

    @PostMapping("/rental/rate")
    public ResponseEntity<?> getRentalRate(@RequestBody Map<String, String> request) {

        // Validate request body
        if (request == null || !request.containsKey("category")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "category is required"));
        }

        String category = request.get("category");
        if (category == null || category.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "category cannot be blank"));
        }

        String normalized = category.trim().toUpperCase(Locale.ROOT);
        BigDecimal rate;

        // Match enum values exactly per OpenAPI
        switch (normalized) {
            case "SMALL" -> rate = BigDecimal.valueOf(25.00);
            case "MEDIUM" -> rate = BigDecimal.valueOf(45.99);
            case "LARGE" -> rate = BigDecimal.valueOf(65.00);
            case "EXTRA_LARGE" -> rate = BigDecimal.valueOf(95.00);
            default -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid car category"));
            }
        }

        // Return valid RateResponse
        return ResponseEntity.ok(Map.of(
                "category", normalized,
                "ratePerDay", rate
        ));
    }
}
