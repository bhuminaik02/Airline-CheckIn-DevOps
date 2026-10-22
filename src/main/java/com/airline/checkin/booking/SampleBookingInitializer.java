package com.airline.checkin.booking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SampleBookingInitializer implements CommandLineRunner {

    private final BookingRepository bookingRepository;
    private final FlightSeatReservationRepository reservationRepository;

    public SampleBookingInitializer(BookingRepository bookingRepository,
                                    FlightSeatReservationRepository reservationRepository) {
        this.bookingRepository = bookingRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        if (bookingRepository.count() > 0) {
            synchronizeActiveReservations();
            return;
        }

        bookingRepository.saveAllAndFlush(List.of(
                new Booking("DEMO1001", "Taylor", "Morgan", "AC101", "SFO", "SEA",
                        LocalDateTime.of(2030, 6, 14, 9, 30), "12A", BookingStatus.BOOKED),
                new Booking("DEMO1002", "Jordan", "Lee", "AC101", "SFO", "SEA",
                        LocalDateTime.of(2030, 6, 14, 9, 30), "12B", BookingStatus.BOOKED),
                new Booking("DEMO2001", "Casey", "Rivera", "AC202", "SEA", "DEN",
                        LocalDateTime.of(2030, 6, 15, 13, 15), "10A", BookingStatus.BOOKED),
                new Booking("DEMO3001", "Avery", "Patel", "AC303", "DEN", "SFO",
                        LocalDateTime.of(2030, 6, 16, 17, 45), "5C", BookingStatus.CANCELLED)));
        synchronizeActiveReservations();
    }

    private void synchronizeActiveReservations() {
        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED).toList();
        if (activeBookings.isEmpty()) {
            reservationRepository.deleteAllInBatch();
        } else {
            reservationRepository.deleteAllByBookingReferenceNotIn(activeBookings.stream()
                    .map(Booking::getBookingReference).toList());
        }
        activeBookings.forEach(booking -> {
                    FlightSeatReservation reservation = reservationRepository.findById(booking.getBookingReference())
                            .orElseGet(() -> new FlightSeatReservation(booking.getBookingReference(),
                                    booking.getFlightNumber(), booking.getSeatNumber()));
                    reservation.assign(booking.getFlightNumber(), booking.getSeatNumber());
                    reservationRepository.save(reservation);
                });
        reservationRepository.flush();
    }
}
