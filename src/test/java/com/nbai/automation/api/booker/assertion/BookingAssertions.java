package com.nbai.automation.api.booker.assertion;

import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.api.booker.model.response.BookingResponse;
import org.testng.Assert;

public final class BookingAssertions {

    private BookingAssertions() {
    }

    public static void assertMatches(BookingResponse actual, BookingRequest expected) {
        Assert.assertEquals(actual.getFirstName(), expected.getFirstName(), "First name did not match");
        Assert.assertEquals(actual.getLastName(), expected.getLastName(), "Last name did not match");
        Assert.assertEquals(actual.getTotalPrice(), expected.getTotalPrice(), "Total price did not match");
        Assert.assertEquals(actual.isDepositPaid(), expected.isDepositPaid(), "Deposit flag did not match");
        Assert.assertEquals(
                actual.getBookingDates().getCheckIn(),
                expected.getBookingDates().getCheckIn(),
                "Check-in date did not match");
        Assert.assertEquals(
                actual.getBookingDates().getCheckOut(),
                expected.getBookingDates().getCheckOut(),
                "Check-out date did not match");
        Assert.assertEquals(
                actual.getAdditionalNeeds(),
                expected.getAdditionalNeeds(),
                "Additional needs did not match");
    }
}
