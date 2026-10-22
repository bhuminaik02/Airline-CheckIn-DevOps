package com.airline.checkin.booking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record BookingCreateRequest(
        @NotBlank String bookingReference,
        @NotBlank String firstName,
        @NotBlank String surname,
        @NotBlank String flightNumber,
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull LocalDateTime departureTime,
        @NotBlank @Pattern(regexp = "(?i)([1-9]|[1-3][0-9])[A-F]", message = "Choose a seat from rows 1–39, seats A–F.") String seatNumber) {
}
