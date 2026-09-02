package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.assertion.BookingAssertions;
import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.api.booker.model.response.AuthResponse;
import com.nbai.automation.api.booker.model.response.BookingIdResponse;
import com.nbai.automation.api.booker.model.response.BookingResponse;
import com.nbai.automation.api.booker.model.response.CreateBookingResponse;
import com.nbai.automation.core.http.HttpStatus;
import io.restassured.response.Response;
import org.testng.Assert;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.UUID;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

abstract class RestfulBookerTestSupport extends BaseApiTest {

    protected void deleteAndRequireCreated(int bookingId, String token) {
        if (bookingId > 0) {
            Assert.assertEquals(services.bookings().delete(bookingId, token).statusCode(), HttpStatus.CREATED,
                    "Owned booking cleanup must return HTTP 201");
        }
    }

    protected CreateBookingResponse assertCreated(Response response, BookingRequest expected) {
        assertJson(response, HttpStatus.OK);
        response.then().body(matchesJsonSchemaInClasspath("schemas/create-booking-response.schema.json"));
        CreateBookingResponse created = response.as(CreateBookingResponse.class);
        Assert.assertTrue(created.getBookingId() > 0, "bookingid must be positive");
        BookingAssertions.assertMatches(created.getBooking(), expected);
        return created;
    }

    protected void assertBookingResponse(Response response, BookingRequest expected) {
        assertJson(response, HttpStatus.OK);
        response.then().body(matchesJsonSchemaInClasspath("schemas/booking.schema.json"));
        BookingAssertions.assertMatches(response.as(BookingResponse.class), expected);
    }

    protected void assertIdListContains(Response response, int bookingId) {
        assertJson(response, HttpStatus.OK);
        response.then().body(matchesJsonSchemaInClasspath("schemas/booking-ids.schema.json"));
        BookingIdResponse[] ids = response.as(BookingIdResponse[].class);
        Arrays.stream(ids).forEach(item -> Assert.assertTrue(item.getBookingId() > 0));
        Assert.assertTrue(Arrays.stream(ids).anyMatch(item -> item.getBookingId() == bookingId),
                "response did not contain owned bookingid " + bookingId);
    }

    protected void assertBadCredentials(Response response) {
        assertJson(response, HttpStatus.OK);
        AuthResponse body = response.as(AuthResponse.class);
        Assert.assertEquals(body.getReason(), "Bad credentials");
        Assert.assertNull(body.getToken(), "token must be absent");
        Assert.assertEquals(response.jsonPath().getMap("$").size(), 1, "body must contain only reason");
    }

    protected void assertJson(Response response, int status) {
        Assert.assertEquals(response.statusCode(), status);
        String contentType = response.getHeader("Content-Type");
        Assert.assertNotNull(contentType, "Content-Type must exist");
        Assert.assertTrue(contentType.toLowerCase().startsWith("application/json"),
                "Expected JSON media type but got " + contentType);
    }

    protected static void assertContentType(Response response, String expected) {
        Assert.assertEquals(response.getHeader("Content-Type").toLowerCase(), expected.toLowerCase());
    }

    protected static String runId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected static String toXml(BookingRequest booking) {
        return "<booking><firstname>" + booking.getFirstName() + "</firstname><lastname>"
                + booking.getLastName() + "</lastname><totalprice>" + booking.getTotalPrice()
                + "</totalprice><depositpaid>" + booking.isDepositPaid()
                + "</depositpaid><bookingdates><checkin>" + booking.getBookingDates().getCheckIn()
                + "</checkin><checkout>" + booking.getBookingDates().getCheckOut()
                + "</checkout></bookingdates><additionalneeds>" + booking.getAdditionalNeeds()
                + "</additionalneeds></booking>";
    }

    protected static Document parseXml(byte[] bytes) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(bytes));
        } catch (Exception exception) {
            throw new AssertionError("Response body was not well-formed XML", exception);
        }
    }

    protected static void assertXmlBooking(Document xml, BookingRequest expected, String prefix) {
        Assert.assertEquals(text(xml, prefix + "firstname"), expected.getFirstName());
        Assert.assertEquals(text(xml, prefix + "lastname"), expected.getLastName());
        Assert.assertEquals(text(xml, prefix + "totalprice"), Integer.toString(expected.getTotalPrice()));
        Assert.assertEquals(text(xml, prefix + "depositpaid"), Boolean.toString(expected.isDepositPaid()));
        Assert.assertEquals(text(xml, prefix + "bookingdates/checkin"), expected.getBookingDates().getCheckIn().toString());
        Assert.assertEquals(text(xml, prefix + "bookingdates/checkout"), expected.getBookingDates().getCheckOut().toString());
        Assert.assertEquals(text(xml, prefix + "additionalneeds"), expected.getAdditionalNeeds());
    }

    protected static String text(Document document, String path) {
        String[] elements = path.split("/");
        org.w3c.dom.Node node = document.getDocumentElement();
        for (String element : elements) {
            org.w3c.dom.Node found = null;
            for (int index = 0; index < node.getChildNodes().getLength(); index++) {
                org.w3c.dom.Node child = node.getChildNodes().item(index);
                if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && child.getNodeName().equals(element)) {
                    found = child;
                    break;
                }
            }
            Assert.assertNotNull(found, "Missing XML element " + path);
            node = found;
        }
        return node.getTextContent();
    }
}
