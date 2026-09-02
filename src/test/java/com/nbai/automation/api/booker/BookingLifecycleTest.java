package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.model.request.BookingPatchRequest;
import com.nbai.automation.api.booker.model.request.BookingDatesRequest;
import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.core.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;

@Epic("Restful Booker API")
@Feature("Booking lifecycle")
public final class BookingLifecycleTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "regression"}, description = "Reuse one token through PUT PATCH and DELETE lifecycle")
    public void reuseOneTokenThroughPutPatchAndDeleteLifecycle() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("LifeOriginal-" + id)
                .lastName("LifeLast-" + id)
                .totalPrice(701)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2036-01-10"))
                        .checkOut(LocalDate.parse("2036-01-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        BookingRequest updated = BookingRequest.builder()
                .firstName("LifeUpdated-" + id)
                .lastName("LifeUpdatedLast-" + id)
                .totalPrice(702)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2036-02-10"))
                        .checkOut(LocalDate.parse("2036-02-14"))
                        .build())
                .additionalNeeds("Dinner")
                .build();
        BookingRequest expected = BookingRequest.builder()
                .firstName("LifeUpdated-" + id)
                .lastName("LifeUpdatedLast-" + id)
                .totalPrice(702)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2036-02-10"))
                        .checkOut(LocalDate.parse("2036-02-14"))
                        .build())
                .additionalNeeds("Late checkout-" + id)
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        boolean deletionAttempted = false;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            assertBookingResponse(services.bookings().update(bookingId, updated, token), updated);
            BookingPatchRequest patch = BookingPatchRequest.builder()
                    .additionalNeeds(expected.getAdditionalNeeds()).build();
            assertBookingResponse(services.bookings().partialUpdate(bookingId, patch, token), expected);
            assertBookingResponse(services.bookings().get(bookingId), expected);
            deletionAttempted = true;
            Assert.assertEquals(services.bookings().delete(bookingId, token).statusCode(), HttpStatus.CREATED);
            Assert.assertEquals(services.bookings().get(bookingId).statusCode(), HttpStatus.NOT_FOUND);
        } finally {
            if (!deletionAttempted) {
                deleteQuietly(bookingId, token);
            }
        }
    }
}
