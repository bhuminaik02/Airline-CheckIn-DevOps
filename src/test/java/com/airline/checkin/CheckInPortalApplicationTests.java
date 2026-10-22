package com.airline.checkin;

import com.airline.checkin.booking.BookingStatus;
import com.airline.checkin.booking.FlightSeatReservation;
import com.airline.checkin.booking.FlightSeatReservationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CheckInPortalApplicationTests {

    private static final String CREATE_BODY = "{\"bookingReference\":\"NEW5001\",\"firstName\":\"Sam\",\"surname\":\"Example\",\"flightNumber\":\"AC505\",\"origin\":\"SFO\",\"destination\":\"LAX\",\"departureTime\":\"2030-07-01T10:00:00\",\"seatNumber\":\"8a\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightSeatReservationRepository reservationRepository;

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

    @Test
    void createsListsAndFindsBookingByReference() throws Exception {
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON).content(CREATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingReference").value("NEW5001"))
                .andExpect(jsonPath("$.seatNumber").value("8A"))
                .andExpect(jsonPath("$.status").value("BOOKED"));
        mockMvc.perform(get("/api/bookings").with(httpBasic("demo-staff", "demo-staff-only")))
                .andExpect(status().isOk()).andExpect(jsonPath("$[?(@.bookingReference == 'NEW5001')]").exists());
        mockMvc.perform(get("/api/bookings/NEW5001/detail").with(httpBasic("demo-staff", "demo-staff-only")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.surname").value("Example"));
    }

    @Test
    void rejectsDuplicateReferencesAndInvalidCreationSeats() throws Exception {
        String duplicate = CREATE_BODY.replace("NEW5001", "DEMO1001");
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON).content(duplicate))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message", containsString("reference")));
        String invalidSeat = CREATE_BODY.replace("8a", "40Z");
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON).content(invalidSeat))
                .andExpect(status().isBadRequest());
    }

    @Test
    void normalizesBookingReferencesAndRejectsDifferentCasingDuplicates() throws Exception {
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(CREATE_BODY.replace("NEW5001", "  mixedRef5001  ")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingReference").value("MIXEDREF5001"));
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(CREATE_BODY.replace("NEW5001", "mixedref5001")))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsSeatAlreadyAssignedOnCreation() throws Exception {
        String occupied = CREATE_BODY.replace("AC505", "AC101").replace("8a", "12b");
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON).content(occupied))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message", containsString("already assigned")));
    }

    @Test
    void searchesByReferenceSurnameAndFlight() throws Exception {
        mockMvc.perform(get("/api/bookings/search").with(httpBasic("demo-staff", "demo-staff-only"))
                        .param("surname", "morg").param("flightNumber", "ac10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].bookingReference").value("DEMO1001"));
    }

    @Test
    void updatesDetailsButDoesNotPermitStatusOrSeatChanges() throws Exception {
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                .contentType(APPLICATION_JSON)
                .content(CREATE_BODY.replace("NEW5001", "EDIT5001")));
        mockMvc.perform(put("/api/bookings/EDIT5001").with(httpBasic("demo-staff", "demo-staff-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"Sam\",\"surname\":\"Example\",\"flightNumber\":\"AC505\",\"origin\":\"SFO\",\"destination\":\"PDX\",\"departureTime\":\"2030-07-01T10:00:00\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.destination").value("PDX"))
                .andExpect(jsonPath("$.seatNumber").value("8A")).andExpect(jsonPath("$.status").value("BOOKED"));
        mockMvc.perform(put("/api/bookings/DEMO3001").with(httpBasic("demo-staff", "demo-staff-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"Avery\",\"surname\":\"Patel\",\"flightNumber\":\"AC303\",\"origin\":\"DEN\",\"destination\":\"SFO\",\"departureTime\":\"2030-06-16T17:45:00\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message", containsString("cancelled")));
    }

    @Test
    void rejectsUpdateForCheckedInBookingAndInvalidUpdateFields() throws Exception {
        mockMvc.perform(post("/api/bookings/DEMO2001/check-in").contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Rivera\"}"));
        mockMvc.perform(put("/api/bookings/DEMO2001").with(httpBasic("demo-staff", "demo-staff-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"Casey\",\"surname\":\"Rivera\",\"flightNumber\":\"AC202\",\"origin\":\"SEA\",\"destination\":\"DEN\",\"departureTime\":\"2030-06-15T13:15:00\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/bookings/DEMO1001").with(httpBasic("demo-staff", "demo-staff-only")).with(csrf())
                        .contentType(APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsFlightUpdateWhenItsExistingSeatIsOccupiedOnThatFlight() throws Exception {
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                .contentType(APPLICATION_JSON)
                .content(CREATE_BODY.replace("NEW5001", "MOVE5001").replace("AC505", "AC606").replace("8a", "12b")))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/bookings/MOVE5001").with(httpBasic("demo-staff", "demo-staff-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"Sam\",\"surname\":\"Example\",\"flightNumber\":\"AC101\",\"origin\":\"SFO\",\"destination\":\"LAX\",\"departureTime\":\"2030-07-01T10:00:00\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message", containsString("already assigned")));
    }

    @Test
    void databaseConstraintRejectsConcurrentSeatReservationDuplicates() {
        reservationRepository.saveAndFlush(new FlightSeatReservation("DBSEAT1", "RACE101", "7A"));
        assertThrows(DataIntegrityViolationException.class,
                () -> reservationRepository.saveAndFlush(new FlightSeatReservation("DBSEAT2", "race101", "7a")));
    }

    @Test
    void cancelledBookingSeatCanBeReused() throws Exception {
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(CREATE_BODY.replace("NEW5001", "REUSE5001").replace("AC505", "AC303").replace("8a", "5c")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatNumber").value("5C"));
    }

    @Test
    void cancellingBookingReleasesItsSeatForReuse() throws Exception {
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(CREATE_BODY.replace("NEW5001", "CANCEL5001").replace("AC505", "AC606").replace("8a", "7d")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/bookings/CANCEL5001/cancel")
                        .with(httpBasic("demo-admin", "demo-admin-only")).with(csrf()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED"));
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(CREATE_BODY.replace("NEW5001", "REUSE5002").replace("AC505", "AC606").replace("8a", "7d")))
                .andExpect(status().isCreated());
    }

    @Test
    void managementEndpointsRequireRolesAndCustomerCheckInRemainsPublic() throws Exception {
        mockMvc.perform(get("/api/bookings")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/bookings/search").param("surname", "Morgan"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/bookings/DEMO1001/detail")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/bookings").with(csrf()).contentType(APPLICATION_JSON).content(CREATE_BODY))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/bookings").with(httpBasic("demo-staff", "demo-staff-only")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/bookings/search").with(httpBasic("demo-admin", "demo-admin-only")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/bookings/DEMO1001/detail").with(httpBasic("demo-staff", "demo-staff-only")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-staff", "demo-staff-only")).with(csrf())
                        .contentType(APPLICATION_JSON).content(CREATE_BODY))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/bookings/DEMO1001/cancel")
                        .with(httpBasic("demo-staff", "demo-staff-only")).with(csrf()))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/bookings").with(httpBasic("demo-admin", "demo-admin-only")).with(csrf())
                        .contentType(APPLICATION_JSON).content(CREATE_BODY))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/bookings/DEMO1001").with(httpBasic("demo-staff", "demo-staff-only")).with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{\"firstName\":\"Taylor\",\"surname\":\"Morgan\",\"flightNumber\":\"AC101\",\"origin\":\"SFO\",\"destination\":\"SEA\",\"departureTime\":\"2030-06-14T09:30:00\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/bookings/DEMO1001").param("surname", "Morgan"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/bookings/DEMO2001/check-in").contentType(APPLICATION_JSON)
                        .content("{\"surname\":\"Rivera\"}"))
                .andExpect(status().isOk());
    }
}
