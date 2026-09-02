package com.nbai.automation.api.booker.service;

import java.util.Objects;

public final class BookerServices {

    private final AuthService authService;
    private final BookingService bookingService;
    private final HealthService healthService;

    BookerServices(
            AuthService authService,
            BookingService bookingService,
            HealthService healthService) {
        this.authService = Objects.requireNonNull(authService);
        this.bookingService = Objects.requireNonNull(bookingService);
        this.healthService = Objects.requireNonNull(healthService);
    }

    public AuthService auth() {
        return authService;
    }

    public BookingService bookings() {
        return bookingService;
    }

    public HealthService health() {
        return healthService;
    }
}
