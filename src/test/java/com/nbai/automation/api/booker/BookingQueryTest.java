package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.model.request.BookingDatesRequest;
import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.api.booker.model.response.BookingIdResponse;
import com.nbai.automation.core.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("Restful Booker API")
@Feature("Booking queries")
public final class BookingQueryTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "regression"}, description = "List all booking IDs without authentication")
    public void listAllBookingIdsWithoutAuthentication() {
        Response response = services.bookings().find(Map.of());
        assertJson(response, HttpStatus.OK);
        response.then().body(matchesJsonSchemaInClasspath("schemas/booking-ids.schema.json"));
        Arrays.stream(response.as(BookingIdResponse[].class))
                .forEach(item -> Assert.assertTrue(item.getBookingId() > 0, "bookingid must be positive"));
    }

    @Test(groups = {"api", "regression"}, description = "Filter booking IDs by exact firstname and lastname")
    public void filterBookingIdsByExactFirstnameAndLastname() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("FilterFirst-" + id)
                .lastName("FilterLast-" + id)
                .totalPrice(111)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2030-01-10"))
                        .checkOut(LocalDate.parse("2030-01-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            Response response = services.bookings().find(Map.of(
                    "firstname", booking.getFirstName(), "lastname", booking.getLastName()));
            assertIdListContains(response, bookingId);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Filter booking IDs at exact checkout boundary")
    public void filterBookingIdsAtExactCheckoutBoundary() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("DateFilter-" + id)
                .lastName("Boundary-" + id)
                .totalPrice(112)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2030-06-10"))
                        .checkOut(LocalDate.parse("2030-06-15"))
                        .build())
                .additionalNeeds("Lunch")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            assertIdListContains(services.bookings().find(Map.of("checkout", "2030-06-15")), bookingId);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "negative"}, description = "Return empty IDs for a unique nonexistent firstname")
    public void returnEmptyIdsForUniqueNonexistentFirstname() {
        Response response = services.bookings().find(Map.of("firstname", "NoMatch-" + UUID.randomUUID()));
        assertJson(response, HttpStatus.OK);
        Assert.assertEquals(response.asString().trim(), "[]");
    }
}
