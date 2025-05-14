package com.example.flightprep.service;

import com.example.flightprep.BaseServiceTest;
import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.dao.CustomerDAO;
import com.example.flightprep.model.User;
import com.example.flightprep.util.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest extends BaseServiceTest {
    @Mock
    private CustomerDAO customerDAO;

    @Mock
    private AppointmentDAO appointmentDAO;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentService = new AppointmentService(appointmentDAO, customerDAO);
    }

    @AfterEach
    public void cleanupBaseTest() {
        SessionManager.setCurrentUser(null);
    }

    @Test
    void testIsValidSlot_ValidFutureSlot() throws SQLException {
        // Test with valid future appointment
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "09:00";
        String userId = "testUser";

        when(SessionManager.getCurrentUserId()).thenReturn(userId);
        when(customerDAO.getFlightDate(eq(userId))).thenReturn(futureDate.plusDays(31));
        when(appointmentDAO.isSlotBooked(eq(futureDate), eq(validTime))).thenReturn(false);

        assertTrue(appointmentService.isValidSlot(futureDate, validTime));
    }

    @Test
    void testIsValidSlot_PastDate() throws SQLException {
        // Test with date in the past
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String validTime = "09:00";
        String userId = "testUser";

        when(SessionManager.getCurrentUserId()).thenReturn(userId);
        when(customerDAO.getFlightDate(userId)).thenReturn(pastDate.plusDays(31));

        assertFalse(appointmentService.isValidSlot(pastDate, validTime));
    }

    @Test
    void testIsValidSlot_InvalidTimeSlot() throws SQLException {
        // Test with invalid time slot
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String invalidTime = "10:00";

        assertFalse(appointmentService.isValidSlot(futureDate, invalidTime));
    }

    @Test
    void testIsValidSlot_BookedSlot() throws SQLException {
        // Test with already booked slot
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "09:00";
        String userId = "testUser";

        when(SessionManager.getCurrentUserId()).thenReturn(userId);
        when(customerDAO.getFlightDate(userId)).thenReturn(futureDate.plusDays(31));
        when(appointmentDAO.isSlotBooked(futureDate, validTime)).thenReturn(true);

        assertFalse(appointmentService.isValidSlot(futureDate, validTime));
    }

    @Test
    void testIsValidSlot_AfterMaxDate() throws SQLException {
        // Test with date after max allowed date
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String validTime = "09:00";
        String userId = "testUser";

        when(SessionManager.getCurrentUserId()).thenReturn(userId);
        when(customerDAO.getFlightDate(userId)).thenReturn(futureDate);

        assertFalse(appointmentService.isValidSlot(futureDate, validTime));
    }

    @Test
    void testGetRiskGroupColor_Group1() {
        // Test color for risk group 1 (green)
        assertEquals("#90EE90", appointmentService.getRiskGroupColor(1));
    }

    @Test
    void testGetRiskGroupColor_Group2() {
        // Test color for risk group 2 (yellow)
        assertEquals("#FFD700", appointmentService.getRiskGroupColor(2));
    }

    @Test
    void testGetRiskGroupColor_Group3() {
        // Test color for risk group 3 (red)
        assertEquals("#FFB6C6", appointmentService.getRiskGroupColor(3));
    }

    @Test
    void testGetRiskGroupColor_InvalidGroup() {
        // Test color for invalid risk group (white)
        assertEquals("white", appointmentService.getRiskGroupColor(0));
    }
}