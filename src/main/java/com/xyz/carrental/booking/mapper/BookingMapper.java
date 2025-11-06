package com.xyz.carrental.booking.mapper;

import com.xyz.carrental.booking.dto.BookingDetailsResponse;
import com.xyz.carrental.booking.dto.ConfirmBookingRequest;
import com.xyz.carrental.booking.entity.Booking;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Maps a ConfirmBookingRequest + extra fields to a Booking entity.
     */
    @Mapping(target = "customerName", source = "ownerName")
    @Mapping(target = "rentalPrice", source = "rentalPrice")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Booking toBooking(ConfirmBookingRequest request,
                      String ownerName,
                      java.math.BigDecimal rentalPrice);

    /**
     * Maps Booking entity to BookingDetailsResponse DTO.
     */
    @Mapping(target = "bookingId", source = "id")
    BookingDetailsResponse toResponse(Booking booking);
}
