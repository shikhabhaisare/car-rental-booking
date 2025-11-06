package com.xyz.carrental.booking.client;

import com.xyz.carrental.booking.stub.model.LicenseResponse;
import com.xyz.carrental.booking.exception.BookingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Slf4j
@Component
public class DrivingLicenseClient {

    private final WebClient webClient;

    public DrivingLicenseClient(@Value("${external.driving-license.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public LicenseResponse getLicenseDetails(String licenseNumber) {
        String maskedLicense = maskLicense(licenseNumber);
        log.info("Calling Driving License API for license lookup: {}", maskedLicense);
        try {
            return webClient.post()
                    .uri("/license/details")
                    .bodyValue(Map.of("licenseNumber", licenseNumber))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> response.createException())
                    .onStatus(status -> status.is5xxServerError(), response -> response.createException())
                    .bodyToMono(LicenseResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {
            String errorMsg = parseErrorMessage(ex);
            // Handle different HTTP codes explicitly
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Driving license not found: {}", maskedLicense);
                throw new BookingException("Driving license not found: " + licenseNumber);
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.warn("Bad request to Driving License API for {}: {}", maskedLicense, errorMsg);
                throw new BookingException(parseErrorMessage(ex));
            } else {
                log.error("Error from Driving License API: {}", errorMsg);
                throw new BookingException("Driving License API error: " + parseErrorMessage(ex));
            }

        } catch (Exception ex) {
            log.error("Unexpected exception calling Driving License API for {}", maskedLicense, ex);
            throw new BookingException("Failed to call Driving License API");
        }
    }

    private String parseErrorMessage(WebClientResponseException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            if (body != null && body.contains("error")) {
                // crude parsing to extract error message
                int idx = body.indexOf(":");
                if (idx > 0) {
                    return body.substring(idx + 1).replaceAll("[\"{}]", "").trim();
                }
            }
            return ex.getMessage();
        } catch (Exception e) {
            log.warn("Failed to parse error message from Driving License API", e);
            return "Unknown error";
        }
    }

    /**
     * Masks license number to avoid logging sensitive data.
     * Example: DL123456789 -> DL*******89
     */
    private String maskLicense(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.length() < 4) return "****";
        int len = licenseNumber.length();
        return licenseNumber.substring(0, 2) + "*".repeat(len - 4) + licenseNumber.substring(len - 2);
    }
}
