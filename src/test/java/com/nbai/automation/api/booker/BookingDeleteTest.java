package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.model.request.BookingDatesRequest;
import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.core.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;

@Epic("Restful Booker API")
@Feature("Booking deletion")
public final class BookingDeleteTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "regression"}, description = "Delete booking using Cookie token")
    public void deleteBookingUsingCookieToken() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("DeleteCookie-" + id)
                .lastName("Owned-" + id)
                .totalPrice(601)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2035-01-10"))
                        .checkOut(LocalDate.parse("2035-01-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        boolean deletionAttempted = false;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            deletionAttempted = true;
            Response deleted = services.bookings().delete(bookingId, token);
            Assert.assertEquals(deleted.statusCode(), HttpStatus.CREATED);
            Assert.assertEquals(services.bookings().get(bookingId).statusCode(), HttpStatus.NOT_FOUND);
        } finally {
            if (!deletionAttempted) {
                deleteQuietly(bookingId, token);
            }
        }
    }

    @Test(groups = {"api", "regression"}, description = "Delete booking using Basic authentication")
    public void deleteBookingUsingBasicAuthentication() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("DeleteBasic-" + id)
                .lastName("Owned-" + id)
                .totalPrice(602)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2035-02-10"))
                        .checkOut(LocalDate.parse("2035-02-12"))
                        .build())
                .additionalNeeds("Lunch")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        boolean deletionAttempted = false;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            deletionAttempted = true;
            Response deleted = services.bookings().deleteWithBasicAuthentication(
                    bookingId, config.requireCredentials());
            Assert.assertEquals(deleted.statusCode(), HttpStatus.CREATED);
            Assert.assertEquals(services.bookings().get(bookingId).statusCode(), HttpStatus.NOT_FOUND);
        } finally {
            if (!deletionAttempted) {
                deleteQuietly(bookingId, token);
            }
        }
    }

    @Test(groups = {"api", "negative"}, description = "Reject DELETE without authentication")
    public void rejectDeleteWithoutAuthentication() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("NoAuthDelete-" + id)
                .lastName("Owned-" + id)
                .totalPrice(603)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2035-03-10"))
                        .checkOut(LocalDate.parse("2035-03-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            Response rejected = services.bookings().deleteWithoutAuthentication(bookingId);
            Assert.assertEquals(rejected.statusCode(), HttpStatus.FORBIDDEN);
            assertBookingResponse(services.bookings().get(bookingId), booking);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "negative"}, description = "Reject DELETE with invalid token")
    public void rejectDeleteWithInvalidToken() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("InvalidDelete-" + id)
                .lastName("Owned-" + id)
                .totalPrice(604)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2035-04-10"))
                        .checkOut(LocalDate.parse("2035-04-12"))
                        .build())
                .additionalNeeds("Dinner")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            Response rejected = services.bookings().delete(bookingId, "invalid-token-" + id);
            Assert.assertEquals(rejected.statusCode(), HttpStatus.FORBIDDEN);
            assertBookingResponse(services.bookings().get(bookingId), booking);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }
}
