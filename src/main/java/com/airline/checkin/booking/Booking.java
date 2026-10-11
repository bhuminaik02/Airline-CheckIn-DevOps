package com.airline.checkin.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String bookingReference;

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String surname;

    @NotBlank
    @Column(nullable = false)
    private String flightNumber;

    @NotBlank
    @Column(nullable = false)
    private String origin;

    @NotBlank
    @Column(nullable = false)
    private String destination;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @NotBlank
    @Column(nullable = false)
    private String seatNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    protected Booking() {
    }

    public Booking(String bookingReference, String firstName, String surname, String flightNumber,
                   String origin, String destination, LocalDateTime departureTime,
                   String seatNumber, BookingStatus status) {
        this.bookingReference = bookingReference;
        this.firstName = firstName;
        this.surname = surname;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
