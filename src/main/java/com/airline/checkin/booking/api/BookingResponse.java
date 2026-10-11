package com.airline.checkin.booking.api;

import com.airline.checkin.booking.Booking;
import com.airline.checkin.booking.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        String bookingReference,
        String firstName,
        String surname,
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime departureTime,
        String seatNumber,
        BookingStatus status) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getBookingReference(),
                booking.getFirstName(),
                booking.getSurname(),
                booking.getFlightNumber(),
                booking.getOrigin(),
                booking.getDestination(),
                booking.getDepartureTime(),
                booking.getSeatNumber(),
                booking.getStatus());
    }
}
