package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.model.request.BookingPatchRequest;
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
@Feature("Booking updates")
public final class BookingUpdateTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "regression"}, description = "Replace booking using Cookie token")
    public void replaceBookingUsingCookieToken() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("Original-" + id)
                .lastName("OriginalLast-" + id)
                .totalPrice(401)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-01-10"))
                        .checkOut(LocalDate.parse("2033-01-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        BookingRequest updated = BookingRequest.builder()
                .firstName("Updated-" + id)
                .lastName("UpdatedLast-" + id)
                .totalPrice(402)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-02-10"))
                        .checkOut(LocalDate.parse("2033-02-14"))
                        .build())
                .additionalNeeds("Dinner")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            assertBookingResponse(services.bookings().update(bookingId, updated, token), updated);
            assertBookingResponse(services.bookings().get(bookingId), updated);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Replace booking using Basic authentication")
    public void replaceBookingUsingBasicAuthentication() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("BasicOriginal-" + id)
                .lastName("BasicLast-" + id)
                .totalPrice(403)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-03-10"))
                        .checkOut(LocalDate.parse("2033-03-12"))
                        .build())
                .additionalNeeds("Lunch")
                .build();
        BookingRequest updated = BookingRequest.builder()
                .firstName("BasicUpdated-" + id)
                .lastName("BasicUpdatedLast-" + id)
                .totalPrice(404)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-04-10"))
                        .checkOut(LocalDate.parse("2033-04-14"))
                        .build())
                .additionalNeeds("Dinner")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            assertBookingResponse(services.bookings().updateWithBasicAuthentication(
                    bookingId, updated, config.requireCredentials()), updated);
            assertBookingResponse(services.bookings().get(bookingId), updated);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "negative"}, description = "Reject PUT without authentication")
    public void rejectPutWithoutAuthentication() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("Protected-" + id)
                .lastName("Original-" + id)
                .totalPrice(405)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-05-10"))
                        .checkOut(LocalDate.parse("2033-05-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        BookingRequest attempted = BookingRequest.builder()
                .firstName("Blocked-" + id)
                .lastName("Changed-" + id)
                .totalPrice(999)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-06-10"))
                        .checkOut(LocalDate.parse("2033-06-12"))
                        .build())
                .additionalNeeds("Dinner")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            Response response = services.bookings().updateWithoutAuthentication(bookingId, attempted);
            Assert.assertEquals(response.statusCode(), HttpStatus.FORBIDDEN);
            assertBookingResponse(services.bookings().get(bookingId), original);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "negative"}, description = "Reject PUT with invalid token")
    public void rejectPutWithInvalidToken() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("InvalidToken-" + id)
                .lastName("Original-" + id)
                .totalPrice(406)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-07-10"))
                        .checkOut(LocalDate.parse("2033-07-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        BookingRequest attempted = BookingRequest.builder()
                .firstName("Blocked-" + id)
                .lastName("Changed-" + id)
                .totalPrice(999)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2033-08-10"))
                        .checkOut(LocalDate.parse("2033-08-12"))
                        .build())
                .additionalNeeds("Dinner")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            Response response = services.bookings().update(bookingId, attempted, "invalid-token-" + id);
            Assert.assertEquals(response.statusCode(), HttpStatus.FORBIDDEN);
            assertBookingResponse(services.bookings().get(bookingId), original);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Patch scalar and date fields using Cookie token")
    public void patchScalarAndDateFieldsUsingCookieToken() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("PatchOriginal-" + id)
                .lastName("PatchLast-" + id)
                .totalPrice(501)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2034-01-10"))
                        .checkOut(LocalDate.parse("2034-01-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        BookingPatchRequest patch = BookingPatchRequest.builder()
                .firstName("Patched-" + id).totalPrice(599)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2034-02-10"))
                        .checkOut(LocalDate.parse("2034-02-15"))
                        .build())
                .build();
        BookingRequest expected = BookingRequest.builder()
                .firstName("Patched-" + id)
                .lastName("PatchLast-" + id)
                .totalPrice(599)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2034-02-10"))
                        .checkOut(LocalDate.parse("2034-02-15"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            assertBookingResponse(services.bookings().partialUpdate(bookingId, patch, token), expected);
            assertBookingResponse(services.bookings().get(bookingId), expected);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Patch booking using Basic authentication")
    public void patchBookingUsingBasicAuthentication() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("BasicPatch-" + id)
                .lastName("BasicPatchLast-" + id)
                .totalPrice(502)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2034-03-10"))
                        .checkOut(LocalDate.parse("2034-03-12"))
                        .build())
                .additionalNeeds("Lunch")
                .build();
        BookingRequest expected = BookingRequest.builder()
                .firstName("BasicPatch-" + id)
                .lastName("BasicPatchLast-" + id)
                .totalPrice(502)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2034-03-10"))
                        .checkOut(LocalDate.parse("2034-03-12"))
                        .build())
                .additionalNeeds("BasicPatchNeed-" + id)
                .build();
        BookingPatchRequest patch = BookingPatchRequest.builder()
                .additionalNeeds(expected.getAdditionalNeeds()).build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            assertBookingResponse(services.bookings().partialUpdateWithBasicAuthentication(
                    bookingId, patch, config.requireCredentials()), expected);
            assertBookingResponse(services.bookings().get(bookingId), expected);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "negative"}, description = "Reject PATCH without authentication")
    public void rejectPatchWithoutAuthentication() {
        String id = runId();
        BookingRequest original = BookingRequest.builder()
                .firstName("NoAuthPatch-" + id)
                .lastName("Original-" + id)
                .totalPrice(503)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2034-04-10"))
                        .checkOut(LocalDate.parse("2034-04-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(original), original).getBookingId();
            Response rejected = services.bookings().partialUpdateWithoutAuthentication(bookingId,
                    BookingPatchRequest.builder().firstName("BlockedPatch-" + id).build());
            Assert.assertEquals(rejected.statusCode(), HttpStatus.FORBIDDEN);
            assertBookingResponse(services.bookings().get(bookingId), original);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

}
