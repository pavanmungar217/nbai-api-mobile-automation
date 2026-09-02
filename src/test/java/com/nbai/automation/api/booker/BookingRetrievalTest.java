package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.model.request.BookingDatesRequest;
import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.core.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.time.LocalDate;

@Epic("Restful Booker API")
@Feature("Booking retrieval")
public final class BookingRetrievalTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "regression"}, description = "Retrieve owned booking as JSON without authentication")
    public void retrieveOwnedBookingAsJsonWithoutAuthentication() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("JsonFirst-" + id)
                .lastName("JsonLast-" + id)
                .totalPrice(201)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2031-01-10"))
                        .checkOut(LocalDate.parse("2031-01-12"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            assertBookingResponse(services.bookings().get(bookingId), booking);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Retrieve owned booking as XML")
    public void retrieveOwnedBookingAsXml() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("XmlFirst-" + id)
                .lastName("XmlLast-" + id)
                .totalPrice(202)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2031-02-10"))
                        .checkOut(LocalDate.parse("2031-02-13"))
                        .build())
                .additionalNeeds("Lunch")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().create(booking), booking).getBookingId();
            Response response = services.bookings().get(bookingId, "application/xml");
            Assert.assertEquals(response.statusCode(), HttpStatus.OK);
            assertContentType(response, "text/html; charset=utf-8");
            Document xml = parseXml(response.asByteArray());
            Assert.assertEquals(xml.getDocumentElement().getNodeName(), "booking");
            assertXmlBooking(xml, booking, "");
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "negative"}, description = "Return 404 for unknown booking ID")
    public void return404ForUnknownBookingId() {
        Assert.assertEquals(services.bookings().get(Integer.MAX_VALUE).statusCode(), HttpStatus.NOT_FOUND);
    }
}
