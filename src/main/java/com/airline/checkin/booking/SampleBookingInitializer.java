package com.airline.checkin.booking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SampleBookingInitializer implements CommandLineRunner {

    private final BookingRepository bookingRepository;

    public SampleBookingInitializer(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void run(String... args) {
        if (bookingRepository.count() > 0) {
            return;
        }

        bookingRepository.saveAll(List.of(
                new Booking("DEMO1001", "Taylor", "Morgan", "AC101", "SFO", "SEA",
                        LocalDateTime.of(2030, 6, 14, 9, 30), "12A", BookingStatus.BOOKED),
                new Booking("DEMO1002", "Jordan", "Lee", "AC101", "SFO", "SEA",
                        LocalDateTime.of(2030, 6, 14, 9, 30), "12B", BookingStatus.BOOKED),
                new Booking("DEMO2001", "Casey", "Rivera", "AC202", "SEA", "DEN",
                        LocalDateTime.of(2030, 6, 15, 13, 15), "10A", BookingStatus.BOOKED),
                new Booking("DEMO3001", "Avery", "Patel", "AC303", "DEN", "SFO",
                        LocalDateTime.of(2030, 6, 16, 17, 45), "5C", BookingStatus.CANCELLED)));
    }
}
