package com.nbai.automation.api.booker.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingPatchRequest {

    @JsonProperty("firstname")
    private final String firstName;

    @JsonProperty("lastname")
    private final String lastName;

    @JsonProperty("totalprice")
    private final Integer totalPrice;

    @JsonProperty("depositpaid")
    private final Boolean depositPaid;

    @JsonProperty("bookingdates")
    private final BookingDatesRequest bookingDates;

    @JsonProperty("additionalneeds")
    private final String additionalNeeds;
}
