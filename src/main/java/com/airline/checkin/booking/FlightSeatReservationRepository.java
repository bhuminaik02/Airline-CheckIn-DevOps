package com.airline.checkin.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FlightSeatReservationRepository extends JpaRepository<FlightSeatReservation, String> {

    void deleteAllByBookingReferenceNotIn(Collection<String> bookingReferences);
}
