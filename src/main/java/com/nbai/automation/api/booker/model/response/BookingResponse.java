package com.nbai.automation.api.booker.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
public class BookingResponse {

    @JsonProperty("firstname")
    private final String firstName;

    @JsonProperty("lastname")
    private final String lastName;

    @JsonProperty("totalprice")
    private final int totalPrice;

    @JsonProperty("depositpaid")
    private final boolean depositPaid;

    @JsonProperty("bookingdates")
    private final BookingDatesResponse bookingDates;

    @JsonProperty("additionalneeds")
    private final String additionalNeeds;
}
