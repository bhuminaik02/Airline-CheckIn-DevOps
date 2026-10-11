package com.airline.checkin.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReferenceIgnoreCaseAndSurnameIgnoreCase(
            String bookingReference, String surname);

    boolean existsByFlightNumberIgnoreCaseAndSeatNumberIgnoreCaseAndStatusNotAndIdNot(
            String flightNumber, String seatNumber, BookingStatus excludedStatus, Long id);
}
