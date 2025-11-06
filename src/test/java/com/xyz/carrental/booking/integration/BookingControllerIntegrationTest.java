package com.xyz.carrental.booking.integration;

import com.xyz.carrental.booking.booking.client.CarPricingClient;
import com.xyz.carrental.booking.booking.client.DrivingLicenseClient;
import com.xyz.carrental.booking.booking.controller.BookingController;

import com.xyz.carrental.booking.booking.domain.CarSegment;
import com.xyz.carrental.booking.booking.dto.BookingDetailsResponse;
import com.xyz.carrental.booking.booking.dto.ConfirmBookingRequest;
import com.xyz.carrental.booking.booking.entity.Booking;
import com.xyz.carrental.booking.booking.exception.BookingException;
import com.xyz.carrental.booking.booking.service.BookingService;
import com.xyz.carrental.booking.booking.stub.model.LicenseResponse;
import com.xyz.carrental.booking.booking.stub.model.RateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.xyz.carrental.booking.booking.BookingServiceApplication.class)
@AutoConfigureMockMvc
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @MockBean
    private DrivingLicenseClient drivingLicenseClient;
    private LicenseResponse validLicenseResponse;
    private LicenseResponse invalidLicenseResponse;
    @MockBean
    private CarPricingClient carPricingClient;


    @BeforeEach
    void setUp() {
        // Valid license: issued 2 years ago, expires 5 years from now
        validLicenseResponse = new LicenseResponse(
                "DL12345",
                "John Doe",
                LocalDate.now().minusYears(2),
                LocalDate.now().plusYears(5)
        );

        // Invalid license: issued recently, already expired
        invalidLicenseResponse = new LicenseResponse(
                "DL12345",
                "John Doe",
                LocalDate.now().minusMonths(6),   // issued <1 year ago
                LocalDate.now().minusDays(1)      // expired yesterday
        );

        // Mock the CarPricingClient to return a valid rate
        RateResponse mockRateResponse = new RateResponse("MEDIUM", BigDecimal.valueOf(100));

        when(carPricingClient.getRateForCategory(anyString()))
                .thenReturn(mockRateResponse);

        // Default mock: valid license

        when(drivingLicenseClient.getLicenseDetails(anyString()))
                .thenReturn(validLicenseResponse);
    }

    @Test
    void testConfirmBooking_Success() throws Exception {
        UUID bookingId = UUID.randomUUID();
        when(bookingService.confirmBooking(any(ConfirmBookingRequest.class)))
                .thenReturn(bookingId);
        String requestJson = """
            {
                "drivingLicenseNumber":"DL12345",
                "age":25,
                "startDate":"2025-11-07",
                "endDate":"2025-11-11",
                "carSegment":"MEDIUM"
            }
        """;

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", "Basic dXNlcjp1c2VyMTIz"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookingId").value(bookingId.toString())); // only bookingId exists
    }

    @Test
    void testGetBookingDetails_Success() throws Exception {
        UUID bookingId = UUID.randomUUID();

        Booking booking = Booking.builder()
                .id(bookingId)
                .drivingLicenseNumber("DL12345")
                .customerName("John Doe")
                .age(25)
                .startDate(LocalDate.of(2025, 11, 7))
                .endDate(LocalDate.of(2025, 11, 11))
                .carSegment(CarSegment.MEDIUM)
                .rentalPrice(new BigDecimal("100"))
                .build();

        BookingDetailsResponse mockBookingDetailsResponse = new BookingDetailsResponse(
                bookingId,
                "DL12345",
                "John Doe",
                25,
                LocalDate.of(2025, 11, 7),
                LocalDate.of(2025, 11, 11),
                CarSegment.MEDIUM,
                BigDecimal.valueOf(100)
        );

        when(bookingService.getBookingDetails(bookingId))
                .thenReturn(mockBookingDetailsResponse);
        mockMvc.perform(get("/api/v1/bookings/{id}", bookingId)
                        .header("Authorization", "Basic dXNlcjp1c2VyMTIz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(bookingId.toString()))
                .andExpect(jsonPath("$.drivingLicenseNumber").value("DL12345"))
                .andExpect(jsonPath("$.carSegment").value("MEDIUM"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void testConfirmBooking_InvalidLicense_ShouldReturn400() throws Exception {

        // Mock BookingService to throw exception when trying to confirm
        when(bookingService.confirmBooking(any(ConfirmBookingRequest.class)))
                .thenThrow(new com.xyz.carrental.booking.booking.exception.BookingException(
                        "Please provide valid License: Driving License must be at least 1 year old or Driving License has been expired"
                ));

        String requestJson = """
        {
            "drivingLicenseNumber":"DL12345",
            "age":25,
            "startDate":"2025-11-07",
            "endDate":"2025-11-11",
            "carSegment":"MEDIUM"
        }
    """;

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", "Basic dXNlcjp1c2VyMTIz"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Booking Error"))
                .andExpect(jsonPath("$.message").value(
                        "Please provide valid License: Driving License must be at least 1 year old or Driving License has been expired")); // exact message depends on controller logic
    }

}
