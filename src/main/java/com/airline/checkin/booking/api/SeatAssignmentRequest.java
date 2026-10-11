package com.airline.checkin.booking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SeatAssignmentRequest(
        @NotBlank(message = "Surname is required.") String surname,
        @NotBlank(message = "Seat number is required.")
        @Pattern(regexp = "(?i)([1-9]|[1-3][0-9])[A-F]", message = "Choose a seat from rows 1–39, seats A–F.")
        String seatNumber) {
}
