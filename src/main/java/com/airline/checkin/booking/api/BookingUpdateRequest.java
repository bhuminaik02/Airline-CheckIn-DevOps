package com.airline.checkin.booking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String surname,
        @NotBlank String flightNumber,
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull LocalDateTime departureTime) {
}
