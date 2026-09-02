package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.model.request.BookingDatesRequest;
import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.api.booker.model.response.CreateBookingResponse;
import com.nbai.automation.core.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.time.LocalDate;

@Epic("Restful Booker API")
@Feature("Booking creation")
public final class BookingCreationTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "regression"}, description = "Create JSON booking without authentication")
    public void createJsonBookingWithoutAuthentication() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("CreateFirst-" + id)
                .lastName("CreateLast-" + id)
                .totalPrice(245)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2032-03-10"))
                        .checkOut(LocalDate.parse("2032-03-13"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            CreateBookingResponse created = assertCreated(services.bookings().create(booking), booking);
            bookingId = created.getBookingId();
            assertBookingResponse(services.bookings().get(bookingId), booking);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Create booking using XML input and JSON output")
    public void createBookingUsingXmlInputAndJsonOutput() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("XmlCreate-" + id)
                .lastName("XmlLast-" + id)
                .totalPrice(310)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2032-04-01"))
                        .checkOut(LocalDate.parse("2032-04-04"))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            bookingId = assertCreated(services.bookings().createXml(toXml(booking)), booking).getBookingId();
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Create JSON booking and request XML output")
    public void createJsonBookingAndRequestXmlOutput() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("XmlResponse-" + id)
                .lastName("XmlResponseLast-" + id)
                .totalPrice(311)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2032-05-01"))
                        .checkOut(LocalDate.parse("2032-05-04"))
                        .build())
                .additionalNeeds("Dinner")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            Response response = services.bookings().createJson(booking, "application/xml", "application/json");
            Assert.assertEquals(response.statusCode(), HttpStatus.OK);
            assertContentType(response, "text/html; charset=utf-8");
            Document xml = parseXml(response.asByteArray());
            Assert.assertEquals(xml.getDocumentElement().getNodeName(), "created-booking");
            bookingId = Integer.parseInt(text(xml, "bookingid"));
            Assert.assertTrue(bookingId > 0, "bookingid must be positive");
            assertXmlBooking(xml, booking, "booking/");
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }

    @Test(groups = {"api", "regression"}, description = "Preserve Unicode and punctuation in string fields")
    public void preserveUnicodeAndPunctuationInStringFields() {
        String id = runId();
        BookingRequest booking = BookingRequest.builder()
                .firstName("Ana-María-" + id)
                .lastName("O'Neil-" + id)
                .totalPrice(312)
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(LocalDate.parse("2032-06-01"))
                        .checkOut(LocalDate.parse("2032-06-04"))
                        .build())
                .additionalNeeds("Quiet room & breakfast")
                .build();
        String token = freshAuthenticationToken();
        int bookingId = 0;
        try {
            CreateBookingResponse created = assertCreated(services.bookings().createJson(
                    booking, "application/json", "application/json; charset=UTF-8"), booking);
            bookingId = created.getBookingId();
            assertBookingResponse(services.bookings().get(bookingId), booking);
        } finally {
            deleteAndRequireCreated(bookingId, token);
        }
    }
}
