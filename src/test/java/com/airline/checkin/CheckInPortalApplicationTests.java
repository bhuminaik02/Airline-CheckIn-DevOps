package com.airline.checkin;

import com.airline.checkin.booking.BookingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CheckInPortalApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void looksUpBookingWithReferenceAndSurname() throws Exception {
        mockMvc.perform(get("/api/bookings/DEMO1001").param("surname", "Morgan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingReference").value("DEMO1001"))
                .andExpect(jsonPath("$.firstName").value("Taylor"))
                .andExpect(jsonPath("$.status").value(BookingStatus.BOOKED.name()));
    }

    @Test
    void returnsNotFoundWhenReferenceAndSurnameDoNotMatch() throws Exception {
        mockMvc.perform(get("/api/bookings/DEMO1001").param("surname", "WrongSurname"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("No booking matches")));
    }

    @Test
    void rejectsInvalidSeatFormat() throws Exception {
        mockMvc.perform(put("/api/bookings/DEMO1001/seat")
                        .contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Morgan\",\"seatNumber\":\"0Z\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("rows 1–39")));
    }

    @Test
    void assignsAnAvailableSeat() throws Exception {
        mockMvc.perform(put("/api/bookings/DEMO1001/seat")
                        .contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Morgan\",\"seatNumber\":\"13c\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value("13C"));
    }

    @Test
    void rejectsSeatAlreadyAssignedOnTheSameFlight() throws Exception {
        mockMvc.perform(put("/api/bookings/DEMO1001/seat")
                        .contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Morgan\",\"seatNumber\":\"12b\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already assigned")));
    }

    @Test
    void preventsCheckInForCancelledBooking() throws Exception {
        mockMvc.perform(post("/api/bookings/DEMO3001/check-in")
                        .contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Patel\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("cancelled")));
    }

    @Test
    void preventsDuplicateCheckIn() throws Exception {
        mockMvc.perform(post("/api/bookings/DEMO1002/check-in")
                        .contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Lee\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.CHECKED_IN.name()));

        mockMvc.perform(post("/api/bookings/DEMO1002/check-in")
                        .contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Lee\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already been checked in")));
    }
}
