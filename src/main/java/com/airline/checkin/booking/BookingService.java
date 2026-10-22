package com.airline.checkin.booking;

import com.airline.checkin.booking.api.BookingCreateRequest;
import com.airline.checkin.booking.api.BookingResponse;
import com.airline.checkin.booking.api.BookingUpdateRequest;
import com.airline.checkin.booking.api.CheckInRequest;
import com.airline.checkin.booking.api.SeatAssignmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightSeatReservationRepository reservationRepository;

    public BookingService(BookingRepository bookingRepository,
                          FlightSeatReservationRepository reservationRepository) {
        this.bookingRepository = bookingRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listBookings() {
        return bookingRepository.findAll().stream().map(BookingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> searchBookings(String reference, String surname, String flightNumber) {
        return bookingRepository.findByBookingReferenceContainingIgnoreCaseAndSurnameContainingIgnoreCaseAndFlightNumberContainingIgnoreCase(
                valueOrEmpty(reference), valueOrEmpty(surname), valueOrEmpty(flightNumber))
                .stream().map(BookingResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(String reference) {
        return BookingResponse.from(findByReference(reference));
    }

    public BookingResponse createBooking(BookingCreateRequest request) {
        String reference = normalize(request.bookingReference());
        if (bookingRepository.existsByBookingReferenceIgnoreCase(reference)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That booking reference is already in use.");
        }
        String flight = normalize(request.flightNumber());
        String seat = normalize(request.seatNumber());
        Booking booking = new Booking(reference, request.firstName().trim(), request.surname().trim(), flight,
                request.origin().trim().toUpperCase(java.util.Locale.ROOT),
                request.destination().trim().toUpperCase(java.util.Locale.ROOT),
                request.departureTime(), seat, BookingStatus.BOOKED);
        booking = bookingRepository.saveAndFlush(booking);
        reservationRepository.saveAndFlush(new FlightSeatReservation(reference, flight, seat));
        return BookingResponse.from(booking);
    }

    public BookingResponse updateBooking(String reference, BookingUpdateRequest request) {
        Booking booking = findByReference(reference);
        requireBooked(booking);
        String flightNumber = request.flightNumber().trim();
        if (!booking.getFlightNumber().equalsIgnoreCase(flightNumber)
                && bookingRepository.existsByFlightNumberIgnoreCaseAndSeatNumberIgnoreCaseAndStatusNotAndIdNot(
                flightNumber, booking.getSeatNumber(), BookingStatus.CANCELLED, booking.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "That seat is already assigned on the requested flight.");
        }
        booking.updateDetails(request.firstName().trim(), request.surname().trim(), request.flightNumber().trim(),
                request.origin().trim().toUpperCase(java.util.Locale.ROOT),
                request.destination().trim().toUpperCase(java.util.Locale.ROOT), request.departureTime());
        Booking savedBooking = bookingRepository.saveAndFlush(booking);
        reservationRepository.findById(savedBooking.getBookingReference()).ifPresentOrElse(
                reservation -> {
                    reservation.assign(savedBooking.getFlightNumber(), savedBooking.getSeatNumber());
                    reservationRepository.saveAndFlush(reservation);
                },
                () -> reservationRepository.saveAndFlush(new FlightSeatReservation(
                        savedBooking.getBookingReference(), savedBooking.getFlightNumber(), savedBooking.getSeatNumber())));
        return BookingResponse.from(savedBooking);
    }

    public BookingResponse cancelBooking(String reference) {
        Booking booking = findByReference(reference);
        requireBooked(booking);
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelled = bookingRepository.saveAndFlush(booking);
        reservationRepository.deleteById(cancelled.getBookingReference());
        return BookingResponse.from(cancelled);
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

        String seatNumber = normalize(request.seatNumber());

        booking.setSeatNumber(seatNumber);
        Booking savedBooking = bookingRepository.saveAndFlush(booking);
        reservationRepository.findById(savedBooking.getBookingReference()).ifPresentOrElse(
                reservation -> {
                    reservation.assign(savedBooking.getFlightNumber(), seatNumber);
                    reservationRepository.saveAndFlush(reservation);
                },
                () -> reservationRepository.saveAndFlush(new FlightSeatReservation(
                        savedBooking.getBookingReference(), savedBooking.getFlightNumber(), seatNumber)));
        return BookingResponse.from(savedBooking);
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
                        normalize(reference), surname.trim())
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

    private Booking findByReference(String reference) {
        requireText(reference, "Booking reference is required.");
        return bookingRepository.findByBookingReferenceIgnoreCase(normalize(reference))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No booking matches that reference."));
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(java.util.Locale.ROOT);
    }
}
