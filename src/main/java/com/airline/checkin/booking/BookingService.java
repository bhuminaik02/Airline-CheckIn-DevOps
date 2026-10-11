package com.airline.checkin.booking;

import com.airline.checkin.booking.api.BookingResponse;
import com.airline.checkin.booking.api.CheckInRequest;
import com.airline.checkin.booking.api.SeatAssignmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public BookingResponse findBooking(String reference, String surname) {
        requireText(reference, "Booking reference is required.");
        requireText(surname, "Surname is required.");

        return bookingRepository.findByBookingReferenceIgnoreCaseAndSurnameIgnoreCase(
                        reference.trim(), surname.trim())
                .map(BookingResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No booking matches that reference and surname."));
    }

    public BookingResponse assignSeat(String reference, SeatAssignmentRequest request) {
        Booking booking = findEntity(reference, request.surname());
        requireBooked(booking);

        String seatNumber = request.seatNumber().trim().toUpperCase();
        boolean seatTaken = bookingRepository
                .existsByFlightNumberIgnoreCaseAndSeatNumberIgnoreCaseAndStatusNotAndIdNot(
                        booking.getFlightNumber(), seatNumber, BookingStatus.CANCELLED, booking.getId());
        if (seatTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That seat is already assigned on this flight.");
        }

        booking.setSeatNumber(seatNumber);
        return BookingResponse.from(bookingRepository.save(booking));
    }

    public BookingResponse checkIn(String reference, CheckInRequest request) {
        Booking booking = findEntity(reference, request.surname());
        requireBooked(booking);

        if (!StringUtils.hasText(booking.getSeatNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Choose a seat before checking in.");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        return BookingResponse.from(bookingRepository.save(booking));
    }

    private Booking findEntity(String reference, String surname) {
        requireText(reference, "Booking reference is required.");
        requireText(surname, "Surname is required.");
        return bookingRepository.findByBookingReferenceIgnoreCaseAndSurnameIgnoreCase(
                        reference.trim(), surname.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No booking matches that reference and surname."));
    }

    private void requireBooked(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A cancelled booking cannot be changed.");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This booking has already been checked in.");
        }
    }

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
