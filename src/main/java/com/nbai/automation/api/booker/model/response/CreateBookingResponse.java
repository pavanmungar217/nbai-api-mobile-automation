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
public class CreateBookingResponse {

    @JsonProperty("bookingid")
    private final int bookingId;

    @JsonProperty("booking")
    private final BookingResponse booking;
}
