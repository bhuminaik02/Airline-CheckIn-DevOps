package com.airline.checkin.booking.api;

import jakarta.validation.constraints.NotBlank;

public record CheckInRequest(@NotBlank(message = "Surname is required.") String surname) {
}
