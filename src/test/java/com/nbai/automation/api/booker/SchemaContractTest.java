package com.nbai.automation.api.booker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbai.automation.api.booker.model.response.BookingDatesResponse;
import com.nbai.automation.api.booker.model.response.BookingResponse;
import com.nbai.automation.api.booker.model.response.CreateBookingResponse;
import com.nbai.automation.core.json.JsonMapperFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

public final class SchemaContractTest {

    @Test(groups = "framework")
    public void createBookingFixtureMatchesTheResponseSchema() throws Exception {
        LocalDate checkIn = LocalDate.of(2030, 1, 10);
        BookingResponse booking = BookingResponse.builder()
                .firstName("Schema")
                .lastName("Check")
                .totalPrice(250)
                .depositPaid(true)
                .bookingDates(BookingDatesResponse.builder()
                        .checkIn(checkIn)
                        .checkOut(checkIn.plusDays(2))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
        ObjectMapper objectMapper = JsonMapperFactory.create();
        String fixture = objectMapper.writeValueAsString(CreateBookingResponse.builder()
                .bookingId(123)
                .booking(booking)
                .build());

        assertThat(fixture, matchesJsonSchemaInClasspath("schemas/create-booking-response.schema.json"));

        CreateBookingResponse roundTrip = objectMapper.readValue(fixture, CreateBookingResponse.class);
        Assert.assertEquals(roundTrip.getBookingId(), 123);
        Assert.assertEquals(roundTrip.getBooking(), booking);
    }
}
