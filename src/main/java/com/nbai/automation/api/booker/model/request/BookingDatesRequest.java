package com.nbai.automation.api.booker.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Getter
@Builder
@EqualsAndHashCode
@Jacksonized
public class BookingDatesRequest {

    @JsonProperty("checkin")
    private final LocalDate checkIn;

    @JsonProperty("checkout")
    private final LocalDate checkOut;
}
