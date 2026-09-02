package com.nbai.automation.api.booker.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
public class BookingDatesResponse {

    @JsonProperty("checkin")
    private final LocalDate checkIn;

    @JsonProperty("checkout")
    private final LocalDate checkOut;
}
