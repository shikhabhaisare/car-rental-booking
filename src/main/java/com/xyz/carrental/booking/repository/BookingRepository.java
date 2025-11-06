package com.xyz.carrental.booking.repository;

import com.xyz.carrental.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for performing CRUD operations on {@link Booking} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard database operations.
 */
public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
