package com.xyz.carrental.booking.stub.model;

import java.math.BigDecimal;

public record RateResponse(
        String category,
        BigDecimal ratePerDay
) {}
