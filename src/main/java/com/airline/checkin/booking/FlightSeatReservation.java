package com.airline.checkin.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Locale;

@Entity
@Table(name = "flight_seat_reservations", uniqueConstraints = @UniqueConstraint(
        name = "uk_flight_seat_reservation", columnNames = {"flight_number", "seat_number"}))
public class FlightSeatReservation {

    @Id
    @Column(name = "booking_reference", nullable = false)
    private String bookingReference;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    protected FlightSeatReservation() {
    }

    public FlightSeatReservation(String bookingReference, String flightNumber, String seatNumber) {
        this.bookingReference = bookingReference;
        this.flightNumber = flightNumber;
        this.seatNumber = seatNumber;
    }

    public void assign(String flightNumber, String seatNumber) {
        this.flightNumber = flightNumber;
        this.seatNumber = seatNumber;
    }

    @PrePersist
    @PreUpdate
    private void normalizeIdentifiers() {
        bookingReference = normalize(bookingReference);
        flightNumber = normalize(flightNumber);
        seatNumber = normalize(seatNumber);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
