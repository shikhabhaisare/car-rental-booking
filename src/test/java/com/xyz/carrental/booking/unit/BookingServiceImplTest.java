package com.xyz.carrental.booking.unit;

import com.xyz.carrental.booking.booking.client.CarPricingClient;
import com.xyz.carrental.booking.booking.client.DrivingLicenseClient;
import com.xyz.carrental.booking.booking.domain.CarSegment;
import com.xyz.carrental.booking.booking.dto.BookingDetailsResponse;
import com.xyz.carrental.booking.booking.dto.ConfirmBookingRequest;
import com.xyz.carrental.booking.booking.entity.Booking;
import com.xyz.carrental.booking.booking.exception.BookingException;
import com.xyz.carrental.booking.booking.mapper.BookingMapper;
import com.xyz.carrental.booking.booking.repository.BookingRepository;
import com.xyz.carrental.booking.booking.service.BookingServiceImpl;
import com.xyz.carrental.booking.booking.stub.model.LicenseResponse;
import com.xyz.carrental.booking.booking.stub.model.RateResponse;
import com.xyz.carrental.booking.booking.validation.LicenseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private DrivingLicenseClient drivingLicenseClient;

    @Mock
    private CarPricingClient carPricingClient;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private LicenseValidator licenseValidator;

    private ConfirmBookingRequest bookingRequest;
    private LicenseResponse licenseResponse;
    private RateResponse rateResponse;
    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bookingRequest = new ConfirmBookingRequest(
                "DL123456789",
                25,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                CarSegment.MEDIUM // carSegment for simplicity
        );

        licenseResponse = new LicenseResponse("DL123456789","John Doe", LocalDate.now().minusYears(3), LocalDate.now().plusYears(7));
        rateResponse = new RateResponse("MEDIUM", BigDecimal.valueOf(50.0));
        booking = Booking.builder()
                .id(UUID.randomUUID())
                .drivingLicenseNumber(bookingRequest.drivingLicenseNumber())
                .customerName("John Doe")
                .age(bookingRequest.age())
                .startDate(bookingRequest.startDate())
                .endDate(bookingRequest.endDate())
                .rentalPrice(BigDecimal.valueOf(250.0))
                .build();
    }

    @Test
    void confirmBooking_success() {
        when(drivingLicenseClient.getLicenseDetails(bookingRequest.drivingLicenseNumber())).thenReturn(licenseResponse);
        doNothing().when(licenseValidator).validateLicense(licenseResponse);
        when(carPricingClient.getRateForCategory(anyString())).thenReturn(rateResponse);
        when(bookingMapper.toBooking(any(), anyString(), any())).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);

        UUID bookingId = bookingService.confirmBooking(bookingRequest);
        assertNotNull(bookingId);
        assertEquals(booking.getId(), bookingId);

        verify(licenseValidator).validateLicense(licenseResponse);
        verify(bookingRepository).save(booking);
    }

    @Test
    void confirmBooking_licenseInvalid_throwsBookingException() {
        when(drivingLicenseClient.getLicenseDetails(anyString())).thenReturn(licenseResponse);
        doThrow(new BookingException("Invalid license")).when(licenseValidator).validateLicense(licenseResponse);

        BookingException ex = assertThrows(BookingException.class, () -> bookingService.confirmBooking(bookingRequest));
        assertEquals("Invalid license", ex.getMessage());
    }

    @Test
    void getBookingDetails_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDetailsResponse response = mock(BookingDetailsResponse.class);
        when(bookingMapper.toResponse(booking)).thenReturn(response);

        BookingDetailsResponse result = bookingService.getBookingDetails(booking.getId());
        assertNotNull(result);
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void getBookingDetails_notFound_throwsBookingException() {
        UUID id = UUID.randomUUID();
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        BookingException ex = assertThrows(BookingException.class, () -> bookingService.getBookingDetails(id));
        assertTrue(ex.getMessage().contains("Car Rental Booking details not found"));
    }
}
