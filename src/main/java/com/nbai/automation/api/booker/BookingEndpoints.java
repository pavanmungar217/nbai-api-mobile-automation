package com.nbai.automation.api.booker;

public final class BookingEndpoints {

    public static final String AUTH = "/auth";
    public static final String BOOKING = "/booking";
    public static final String PING = "/ping";

    private BookingEndpoints() {
    }

    public static String bookingById(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Booking ID must be positive");
        }
        return BOOKING + "/" + bookingId;
    }
}

