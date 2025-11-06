package com.xyz.carrental.booking.client;

import com.xyz.carrental.booking.exception.BookingException;
import com.xyz.carrental.booking.stub.model.RateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

/**
 * Calls the Car Rental Pricing API as per OpenAPI spec.
 * POST /rental/rate
 */
@Slf4j
@Component
public class CarPricingClient {

    private final WebClient webClient;

    public CarPricingClient(@Value("${external.pricing.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Retrieves the daily rental rate for the provided car category.
     *
     * @param category The car category (SMALL, MEDIUM, LARGE, EXTRA_LARGE)
     * @return RateResponse containing category and ratePerDay
     */
    public RateResponse getRateForCategory(String category) {
        try {
            return webClient.post()
                    .uri("/rental/rate")
                    .bodyValue(Map.of("category", category))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> response.createException())
                    .onStatus(status -> status.is5xxServerError(), response -> response.createException())
                    .bodyToMono(RateResponse.class)
                    .block();

        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BookingException(parseErrorMessage(ex));
            } else {
                throw new BookingException("Car Pricing API error: " + parseErrorMessage(ex), ex);
            }

        } catch (Exception ex) {
            throw new BookingException("Failed to call Car Pricing API", ex);
        }
    }

    /**
     * Helper to extract readable error message from API error JSON body.
     */
    private String parseErrorMessage(WebClientResponseException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            if (body != null && body.contains("error")) {
                int idx = body.indexOf(":");
                if (idx > 0) {
                    return body.substring(idx + 1).replaceAll("[\"{}]", "").trim();
                }
            }
            return ex.getMessage();
        } catch (Exception e) {
            return "Unknown error";
        }
    }
}