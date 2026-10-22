package com.airline.checkin.booking.api;

import com.airline.checkin.booking.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> listBookings() {
        return bookingService.listBookings();
    }

    @GetMapping("/search")
    public List<BookingResponse> searchBookings(@RequestParam(required = false) String reference,
                                                @RequestParam(required = false) String surname,
                                                @RequestParam(required = false) String flightNumber) {
        return bookingService.searchBookings(reference, surname, flightNumber);
    }

    @GetMapping("/{reference}/detail")
    public BookingResponse getBooking(@PathVariable String reference) {
        return bookingService.getBooking(reference);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingCreateRequest request) {
        return bookingService.createBooking(request);
    }

    @PutMapping("/{reference}")
    public BookingResponse updateBooking(@PathVariable String reference,
                                         @Valid @RequestBody BookingUpdateRequest request) {
        return bookingService.updateBooking(reference, request);
    }

    @PostMapping("/{reference}/cancel")
    public BookingResponse cancelBooking(@PathVariable String reference) {
        return bookingService.cancelBooking(reference);
    }

    @GetMapping("/{reference}")
    public BookingResponse findBooking(@PathVariable String reference, @RequestParam String surname) {
        return bookingService.findBooking(reference, surname);
    }

    @PutMapping("/{reference}/seat")
    public BookingResponse assignSeat(@PathVariable String reference,
                                      @Valid @RequestBody SeatAssignmentRequest request) {
        return bookingService.assignSeat(reference, request);
    }

    @PostMapping("/{reference}/check-in")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse checkIn(@PathVariable String reference,
                                   @Valid @RequestBody CheckInRequest request) {
        return bookingService.checkIn(reference, request);
    }
}
