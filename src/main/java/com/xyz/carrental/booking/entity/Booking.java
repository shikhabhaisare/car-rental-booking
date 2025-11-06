package com.xyz.carrental.booking.entity;

import com.xyz.carrental.booking.domain.CarSegment;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a car booking record in the system.
 * <p>
 * Contains customer, booking, and pricing details, mapped to the {@code bookings} table.
 */
@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "driving_license_number", nullable = false)
    private String drivingLicenseNumber;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(nullable = false)
    private int age;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_segment", nullable = false)
    private CarSegment carSegment;

    @Column(name = "rental_price", precision = 10, scale = 2)
    private BigDecimal rentalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}