package com.nbai.automation.api.booker.data;

import com.nbai.automation.api.booker.model.request.BookingDatesRequest;
import com.nbai.automation.api.booker.model.request.BookingPatchRequest;
import com.nbai.automation.api.booker.model.request.BookingRequest;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class BookingDataFactory {

    public BookingRequest validBooking() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        LocalDate checkIn = LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(20, 60));
        return BookingRequest.builder()
                .firstName("Auto" + suffix)
                .lastName("User" + suffix)
                .totalPrice(ThreadLocalRandom.current().nextInt(100, 501))
                .depositPaid(true)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(checkIn)
                        .checkOut(checkIn.plusDays(3))
                        .build())
                .additionalNeeds("Breakfast")
                .build();
    }

    public BookingRequest updatedBooking(BookingRequest original) {
        return BookingRequest.builder()
                .firstName(original.getFirstName() + "Updated")
                .lastName(original.getLastName())
                .totalPrice(original.getTotalPrice() + 50)
                .depositPaid(false)
                .bookingDates(BookingDatesRequest.builder()
                        .checkIn(original.getBookingDates().getCheckIn().plusDays(1))
                        .checkOut(original.getBookingDates().getCheckOut().plusDays(2))
                        .build())
                .additionalNeeds("Late checkout")
                .build();
    }

    public BookingPatchRequest patch() {
        return BookingPatchRequest.builder()
                .firstName("Patched" + uniqueSuffix())
                .additionalNeeds("Dinner")
                .build();
    }

    public BookingRequest applyPatch(BookingRequest original, BookingPatchRequest patch) {
        return BookingRequest.builder()
                .firstName(patch.getFirstName() == null ? original.getFirstName() : patch.getFirstName())
                .lastName(patch.getLastName() == null ? original.getLastName() : patch.getLastName())
                .totalPrice(patch.getTotalPrice() == null ? original.getTotalPrice() : patch.getTotalPrice())
                .depositPaid(patch.getDepositPaid() == null ? original.isDepositPaid() : patch.getDepositPaid())
                .bookingDates(patch.getBookingDates() == null ? original.getBookingDates() : patch.getBookingDates())
                .additionalNeeds(patch.getAdditionalNeeds() == null
                        ? original.getAdditionalNeeds()
                        : patch.getAdditionalNeeds())
                .build();
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
