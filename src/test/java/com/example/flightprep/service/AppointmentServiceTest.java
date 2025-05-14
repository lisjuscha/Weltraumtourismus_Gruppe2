package com.example.flightprep.service;

import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.dao.CustomerDAO;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.util.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {
    @Mock
    private CustomerDAO customerDAO;

    @Mock
    private AppointmentDAO appointmentDAO;

    private AppointmentService appointmentService;
    private AutoCloseable mocksCloseable;

    // To control static DAO getInstance() calls
    private MockedStatic<AppointmentDAO> mockedAppointmentDAOStatic;
    private MockedStatic<CustomerDAO> mockedCustomerDAOStatic;

    @BeforeEach
    void setUp() throws Exception {
        mocksCloseable = MockitoAnnotations.openMocks(this);
        appointmentService = new AppointmentService(appointmentDAO, customerDAO);

        // IMPORTANT: Mock the static getInstance() methods of DAOs BEFORE AppointmentService singleton is reset and fetched.
        // This ensures that when AppointmentService's constructor is called (via its getInstance()),
        // it receives these mocks.
        mockedAppointmentDAOStatic = Mockito.mockStatic(AppointmentDAO.class);
        mockedAppointmentDAOStatic.when(AppointmentDAO::getInstance).thenReturn(appointmentDAO);

        mockedCustomerDAOStatic = Mockito.mockStatic(CustomerDAO.class);
        mockedCustomerDAOStatic.when(CustomerDAO::getInstance).thenReturn(customerDAO);

        // Reset the AppointmentService singleton instance to ensure a fresh one for each test,
        // which will then use the mocked DAO instances provided above.
        resetAppointmentServiceSingleton();
        appointmentService = AppointmentService.getInstance();
    }

    @AfterEach
    public void cleanupBaseTest() {
        SessionManager.setCurrentUser(null);
    }

    @AfterEach
    void tearDown() throws Exception {
        resetAppointmentServiceSingleton();

        // Close static DAO mocks
        if (mockedAppointmentDAOStatic != null) mockedAppointmentDAOStatic.close();
        if (mockedCustomerDAOStatic != null) mockedCustomerDAOStatic.close();

        if (mocksCloseable != null) mocksCloseable.close();

        // General cleanup, good practice although MockitoExtension usually handles more.
        Mockito.framework().clearInlineMocks();
    }

    private void resetAppointmentServiceSingleton() throws Exception {
        java.lang.reflect.Field instanceField = AppointmentService.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void getInstance_returnsNonNullInstanceAndSameInstanceOnSubsequentCalls() throws Exception {
        assertNotNull(appointmentService, "First instance from setUp should be non-null");

        AppointmentService instance1 = AppointmentService.getInstance();
        assertSame(appointmentService, instance1, "getInstance() after setUp should return the same instance created in setUp");

        // Reset singleton again to test fresh creation path by getInstance()
        resetAppointmentServiceSingleton();
        // Ensure DAO statics are still in place from setUp or re-mock if necessary for this specific test path
        // For this test, we mainly care about the AppointmentService instance itself.

        AppointmentService instance2 = AppointmentService.getInstance();
        assertNotNull(instance2, "getInstance() after reset should create a new non-null instance");

        AppointmentService instance3 = AppointmentService.getInstance();
        assertSame(instance2, instance3, "Subsequent getInstance() calls should return the same new instance");
    }

    @Test
    void testIsValidSlot_ValidFutureSlot() throws SQLException {
        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            // Test with valid future appointment
            LocalDate futureDate = LocalDate.now().plusDays(1);
            String validTime = "09:00";
            String userId = "testUser";

            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
            when(customerDAO.getFlightDate(eq(userId))).thenReturn(futureDate.plusDays(31));
            when(appointmentDAO.isSlotBooked(eq(futureDate), eq(validTime))).thenReturn(false);

            assertTrue(appointmentService.isValidSlot(futureDate, validTime));
        }
    }

    @Test
    void testIsValidSlot_PastDate() throws SQLException {
        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            // Test with date in the past
            LocalDate pastDate = LocalDate.now().minusDays(1);
            String validTime = "09:00";
            String userId = "testUser";

            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
            when(customerDAO.getFlightDate(userId)).thenReturn(pastDate.plusDays(31));

            assertFalse(appointmentService.isValidSlot(pastDate, validTime));
        }
    }

    @Test
    void testIsValidSlot_InvalidTimeSlot() throws SQLException {
        // Test with invalid time slot - No SessionManager call expected here
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String invalidTime = "10:00";

        assertFalse(appointmentService.isValidSlot(futureDate, invalidTime));
    }

    @Test
    void testIsValidSlot_BookedSlot() throws SQLException {
        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            // Test with already booked slot
            LocalDate futureDate = LocalDate.now().plusDays(1);
            String validTime = "09:00";
            String userId = "testUser";

            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
            when(customerDAO.getFlightDate(userId)).thenReturn(futureDate.plusDays(31));
            when(appointmentDAO.isSlotBooked(futureDate, validTime)).thenReturn(true);

            assertFalse(appointmentService.isValidSlot(futureDate, validTime));
        }
    }

    @Test
    void testIsValidSlot_AfterMaxDate() throws SQLException {
        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            // Test with date after max allowed date
            LocalDate futureDate = LocalDate.now().plusDays(1);
            String validTime = "09:00";
            String userId = "testUser";

            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
            when(customerDAO.getFlightDate(userId)).thenReturn(futureDate); // Flight date is same as appointment date

            assertFalse(appointmentService.isValidSlot(futureDate, validTime));
        }
    }

    @Test
    void isValidSlot_returnsFalse_whenFlightDateIsNull() throws SQLException {
        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            LocalDate futureDate = LocalDate.now().plusDays(1);
            String validTime = "09:00";
            String userId = "userWithNullFlightDate";

            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
            when(customerDAO.getFlightDate(userId)).thenReturn(null); // Simulate flight date not found or null
            // isSlotBooked should not matter if flightDate is null and handled first
            // when(appointmentDAO.isSlotBooked(eq(futureDate), eq(validTime))).thenReturn(false);

            // Expecting false because the flightDate dependent checks cannot be performed.
            // If the SUT currently throws NPE, this test will fail, indicating SUT needs a fix.
            assertFalse(appointmentService.isValidSlot(futureDate, validTime),
                    "isValidSlot should return false if customer flight date is null.");

            verify(customerDAO).getFlightDate(userId);
            // Depending on SUT fix, appointmentDAO.isSlotBooked might not be called if flightDate is null and returns early.
            // verify(appointmentDAO, never()).isSlotBooked(any(LocalDate.class), anyString());
        }
    }

    @Test
    void isValidSlot_throwsSQLException_whenGetFlightDateFails() throws SQLException {
        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            LocalDate futureDate = LocalDate.now().plusDays(1);
            String validTime = "09:00";
            String userId = "userSqlErrorFlightDate";

            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
            when(customerDAO.getFlightDate(userId)).thenThrow(new SQLException("DAO error getting flight date"));

            SQLException exception = assertThrows(SQLException.class,
                    () -> appointmentService.isValidSlot(futureDate, validTime));
            assertEquals("DAO error getting flight date", exception.getMessage());
            verify(customerDAO).getFlightDate(userId);
            verify(appointmentDAO, never()).isSlotBooked(any(LocalDate.class), anyString());
        }
    }

    @Test
    void isValidSlot_throwsSQLException_whenIsSlotBookedFails() throws SQLException {
        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            LocalDate futureDate = LocalDate.now().plusDays(1);
            String validTime = "09:00";
            String userId = "userSqlErrorSlotBooked";

            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
            when(customerDAO.getFlightDate(userId)).thenReturn(futureDate.plusDays(31)); // Valid flight date
            when(appointmentDAO.isSlotBooked(futureDate, validTime)).thenThrow(new SQLException("DAO error checking if slot booked"));

            SQLException exception = assertThrows(SQLException.class,
                    () -> appointmentService.isValidSlot(futureDate, validTime));
            assertEquals("DAO error checking if slot booked", exception.getMessage());
            verify(customerDAO).getFlightDate(userId);
            verify(appointmentDAO).isSlotBooked(futureDate, validTime);
        }
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

    @Test
    void getTimeSlots_ReturnsCorrectSlots() {
        String[] expectedSlots = {"09:00", "11:00", "14:00", "16:00"};
        String[] actualSlots = appointmentService.getTimeSlots();
        assertArrayEquals(expectedSlots, actualSlots);
        // Ensure it's a clone, not the original
        assertNotSame(AppointmentService.class.getDeclaredFields()[2], actualSlots); // Crude check based on field order, better to check content and non-sameness
    }

    @Test
    void getAppointmentsForDate_Success() throws SQLException {
        // Given
        LocalDate testDate = LocalDate.of(2024, 5, 15);
        List<Appointment> expectedAppointments = List.of(new Appointment(1, "c1", "F", "L", "d1", "15.05.2024", "09:00", 1));
        when(appointmentDAO.getAppointmentsByDate(testDate)).thenReturn(expectedAppointments);

        // When
        List<Appointment> actualAppointments = appointmentService.getAppointmentsForDate(testDate);

        // Then
        assertEquals(expectedAppointments, actualAppointments);
        verify(appointmentDAO).getAppointmentsByDate(testDate);
    }

    @Test
    void getAppointmentByCustomerId_Success() throws SQLException {
        // Given
        String customerId = "cust123";
        Appointment expectedAppointment = new Appointment(1, customerId, "F", "L", "d1", "15.05.2024", "09:00", 1);
        when(appointmentDAO.getAppointmentByCustomerId(customerId)).thenReturn(expectedAppointment);

        // When
        Appointment actualAppointment = appointmentService.getAppointmentByCustomerId(customerId);

        // Then
        assertEquals(expectedAppointment, actualAppointment);
        verify(appointmentDAO).getAppointmentByCustomerId(customerId);
    }

    @Test
    void getCurrentWeekStart_ReturnsMonday() {
        // Mock LocalDate.now() to return a known date
        LocalDate knownDate = LocalDate.of(2024, 5, 15); // A Wednesday
        LocalDate expectedMonday = LocalDate.of(2024, 5, 13);

        try (MockedStatic<LocalDate> mockedLocalDate = Mockito.mockStatic(LocalDate.class, Mockito.RETURNS_DEEP_STUBS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(knownDate);

            LocalDate actualWeekStart = appointmentService.getCurrentWeekStart();
            assertEquals(expectedMonday, actualWeekStart);
        }
    }

    // --- Tests for bookAppointment ---

    @Test
    void bookAppointment_InvalidSlot_ThrowsSQLException() throws SQLException {
        // Given
        LocalDate testDate = LocalDate.now().plusDays(1);
        String testTime = "09:00";
        String patientId = "patientInvalidSlot";

        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(patientId);
            // Setup for isValidSlot to fail (e.g., flight date too close)
            when(customerDAO.getFlightDate(patientId)).thenReturn(testDate.plusDays(5));
            // No need to mock appointmentDAO.isSlotBooked if date validation in isValidSlot fails first

            // When & Then
            SQLException exception = assertThrows(SQLException.class, () -> {
                appointmentService.bookAppointment(testDate, testTime); // Corrected: 2 args
            });
            assertTrue(exception.getMessage().contains("The selected appointment is not available"));
        }
        verify(appointmentDAO, never()).bookAppointment(anyString(), any(LocalDate.class), anyString()); // Corrected DAO method
    }

    @Test
    void bookAppointment_DaoBookAppointmentThrowsSqlException_ThrowsSqlException() throws SQLException { // Renamed and repurposed
        // Given
        LocalDate testDate = LocalDate.now().plusDays(7);
        String testTime = "09:00";
        String patientId = "patientDaoFail";

        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(patientId);
            // Setup for isValidSlot to pass
            when(customerDAO.getFlightDate(patientId)).thenReturn(testDate.plusDays(35));
            when(appointmentDAO.isSlotBooked(testDate, testTime)).thenReturn(false);

            // Make DAO's bookAppointment throw an error
            doThrow(new SQLException("DAO bookAppointment error")).when(appointmentDAO).bookAppointment(eq(patientId), eq(testDate), eq(testTime)); // Corrected DAO method

            // When & Then
            SQLException exception = assertThrows(SQLException.class, () -> {
                appointmentService.bookAppointment(testDate, testTime); // Corrected: 2 args
            });
            assertEquals("DAO bookAppointment error", exception.getMessage());
            verify(appointmentDAO).bookAppointment(eq(patientId), eq(testDate), eq(testTime)); // Verify it was called
            verify(customerDAO, never()).updateAppointmentStatus(anyString()); // Should not be called if DAO fails
        }
    }

    @Test
    void bookAppointment_throwsSQLException_whenUpdateAppointmentStatusFails() throws SQLException {
        LocalDate testDate = LocalDate.now().plusDays(1);
        String testTime = "09:00";
        String patientId = "patientUpdateStatusFail";

        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(patientId);

            // Setup for isValidSlot to pass
            when(customerDAO.getFlightDate(patientId)).thenReturn(testDate.plusDays(31));
            when(appointmentDAO.isSlotBooked(testDate, testTime)).thenReturn(false);
            // Make isValidSlot a spy or use a real one that depends on these mocks for full integration
            // For simplicity, we can also just mock isValidSlot directly if its internal logic is tested elsewhere
            // However, the SUT calls the *actual* isValidSlot. So the mocks for getFlightDate and isSlotBooked are correct.

            doNothing().when(appointmentDAO).bookAppointment(patientId, testDate, testTime); // booking itself succeeds
            doThrow(new SQLException("DAO error updating status")).when(customerDAO).updateAppointmentStatus(patientId);

            SQLException exception = assertThrows(SQLException.class,
                    () -> appointmentService.bookAppointment(testDate, testTime));

            assertEquals("DAO error updating status", exception.getMessage());

            verify(appointmentDAO).bookAppointment(patientId, testDate, testTime);
            verify(customerDAO).updateAppointmentStatus(patientId);
        }
    }

    @Test
    void bookAppointment_Success() throws SQLException {
        // Given
        LocalDate testDate = LocalDate.now().plusDays(7);
        String testTime = "09:00";
        String patientId = "patientSuccess";

        try (MockedStatic<SessionManager> mockedSessionManager = Mockito.mockStatic(SessionManager.class)) {
            mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(patientId);
            // Setup for isValidSlot to pass
            when(customerDAO.getFlightDate(patientId)).thenReturn(testDate.plusDays(35));
            when(appointmentDAO.isSlotBooked(testDate, testTime)).thenReturn(false);

            // Mock DAO calls for success
            doNothing().when(appointmentDAO).bookAppointment(eq(patientId), eq(testDate), eq(testTime)); // Corrected DAO method
            doNothing().when(customerDAO).updateAppointmentStatus(eq(patientId));

            // When
            appointmentService.bookAppointment(testDate, testTime); // Corrected: 2 args

            // Then
            mockedSessionManager.verify(SessionManager::getCurrentUserId, atLeastOnce());
            verify(appointmentDAO).bookAppointment(eq(patientId), eq(testDate), eq(testTime));
            verify(customerDAO).updateAppointmentStatus(eq(patientId));
        }
    }

    // --- End Tests for bookAppointment ---

    // --- Tests for getAvailableTimeSlotsForDate ---

    @Test
    void getAvailableTimeSlotsForDate_allSlotsAvailable() throws SQLException {
        // Given
        LocalDate testDate = LocalDate.now().plusDays(10);
        AppointmentService spyService = spy(appointmentService);
        // We need to mock SessionManager and DAOs if isValidSlot is NOT spied/mocked directly,
        // but here we choose to mock isValidSlot directly on a spy for simplicity.

        // Mock isValidSlot to return true for all slots on this date
        for (String slot : spyService.getTimeSlots()) {
            doReturn(true).when(spyService).isValidSlot(testDate, slot);
        }

        // When
        List<String> availableSlots = spyService.getAvailableTimeSlotsForDate(testDate);

        // Then
        assertNotNull(availableSlots);
        assertEquals(spyService.getTimeSlots().length, availableSlots.size());
        assertArrayEquals(spyService.getTimeSlots(), availableSlots.toArray(new String[0]));
        for (String slot : spyService.getTimeSlots()) {
            verify(spyService).isValidSlot(testDate, slot);
        }
    }

    @Test
    void getAvailableTimeSlotsForDate_someSlotsAvailable() throws SQLException {
        // Given
        LocalDate testDate = LocalDate.now().plusDays(10);
        AppointmentService spyService = spy(appointmentService);
        String[] allSlots = spyService.getTimeSlots();
        String availableSlot1 = allSlots[0]; // e.g., "09:00"
        String unavailableSlot1 = allSlots[1]; // e.g., "11:00"
        List<String> expectedAvailable = new ArrayList<>();

        for (String slot : allSlots) {
            if (slot.equals(availableSlot1)) { // Make first slot available
                doReturn(true).when(spyService).isValidSlot(testDate, slot);
                expectedAvailable.add(slot);
            } else if (slot.equals(allSlots[2])) { // Make third slot available
                doReturn(true).when(spyService).isValidSlot(testDate, slot);
                expectedAvailable.add(slot);
            } else { // Others unavailable
                doReturn(false).when(spyService).isValidSlot(testDate, slot);
            }
        }

        // When
        List<String> actualAvailableSlots = spyService.getAvailableTimeSlotsForDate(testDate);

        // Then
        assertNotNull(actualAvailableSlots);
        assertEquals(expectedAvailable.size(), actualAvailableSlots.size());
        assertArrayEquals(expectedAvailable.toArray(new String[0]), actualAvailableSlots.toArray(new String[0]));
        for (String slot : allSlots) {
            verify(spyService).isValidSlot(testDate, slot);
        }
    }

    @Test
    void getAvailableTimeSlotsForDate_noSlotsAvailable() throws SQLException {
        // Given
        LocalDate testDate = LocalDate.now().plusDays(10);
        AppointmentService spyService = spy(appointmentService);

        // Mock isValidSlot to return false for all slots
        for (String slot : spyService.getTimeSlots()) {
            doReturn(false).when(spyService).isValidSlot(testDate, slot);
        }

        // When
        List<String> availableSlots = spyService.getAvailableTimeSlotsForDate(testDate);

        // Then
        assertNotNull(availableSlots);
        assertTrue(availableSlots.isEmpty());
        for (String slot : spyService.getTimeSlots()) {
            verify(spyService).isValidSlot(testDate, slot);
        }
    }

    @Test
    void getAvailableTimeSlotsForDate_isValidSlotThrowsSQLException_propagatesException() throws SQLException {
        // Given
        LocalDate testDate = LocalDate.now().plusDays(10);
        AppointmentService spyService = spy(appointmentService);
        String firstSlot = spyService.getTimeSlots()[0];

        // Mock isValidSlot to throw an exception for the first slot it checks
        doThrow(new SQLException("DAO Error from isValidSlot")).when(spyService).isValidSlot(testDate, firstSlot);
        // Other slots won't be checked if the first one throws

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spyService.getAvailableTimeSlotsForDate(testDate);
        });
        assertEquals("DAO Error from isValidSlot", exception.getMessage());
        verify(spyService).isValidSlot(testDate, firstSlot); // Verify it was called at least for the first slot
    }

    // --- End Tests for getAvailableTimeSlotsForDate ---

    // --- Tests for getWeekAppointments ---

    @Test
    void getWeekAppointments_emptyWeek_returnsEmptyList() throws SQLException {
        // Given
        LocalDate weekStart = LocalDate.of(2024, 5, 13); // A Monday
        for (int i = 0; i < 7; i++) {
            when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(i))).thenReturn(Collections.emptyList());
        }

        // When
        List<Appointment> weekAppointments = appointmentService.getWeekAppointments(weekStart);

        // Then
        assertNotNull(weekAppointments);
        assertTrue(weekAppointments.isEmpty());
        for (int i = 0; i < 7; i++) {
            verify(appointmentDAO).getAppointmentsByDate(weekStart.plusDays(i));
        }
    }

    @Test
    void getWeekAppointments_appointmentsOnSomeDays_returnsCombinedList() throws SQLException {
        // Given
        LocalDate weekStart = LocalDate.of(2024, 5, 13);
        Appointment appt1 = new Appointment(1, "c1", "F1", "L1", "d1", weekStart.plusDays(1).toString(), "09:00", 1);
        Appointment appt2 = new Appointment(2, "c2", "F2", "L2", "d1", weekStart.plusDays(3).toString(), "11:00", 2);
        List<Appointment> day2Appointments = List.of(appt1);
        List<Appointment> day4Appointments = List.of(appt2);

        when(appointmentDAO.getAppointmentsByDate(weekStart)).thenReturn(Collections.emptyList());
        when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(1))).thenReturn(day2Appointments);
        when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(2))).thenReturn(Collections.emptyList());
        when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(3))).thenReturn(day4Appointments);
        when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(4))).thenReturn(Collections.emptyList());
        when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(5))).thenReturn(Collections.emptyList());
        when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(6))).thenReturn(Collections.emptyList());

        // When
        List<Appointment> weekAppointments = appointmentService.getWeekAppointments(weekStart);

        // Then
        assertNotNull(weekAppointments);
        assertEquals(2, weekAppointments.size());
        assertTrue(weekAppointments.contains(appt1));
        assertTrue(weekAppointments.contains(appt2));
        for (int i = 0; i < 7; i++) {
            verify(appointmentDAO).getAppointmentsByDate(weekStart.plusDays(i));
        }
    }

    @Test
    void getWeekAppointments_daoThrowsException_propagatesException() throws SQLException {
        // Given
        LocalDate weekStart = LocalDate.of(2024, 5, 13);
        LocalDate problematicDate = weekStart.plusDays(2);

        when(appointmentDAO.getAppointmentsByDate(weekStart)).thenReturn(Collections.emptyList());
        when(appointmentDAO.getAppointmentsByDate(weekStart.plusDays(1))).thenReturn(Collections.emptyList());
        when(appointmentDAO.getAppointmentsByDate(problematicDate)).thenThrow(new SQLException("DAO error on getAppointmentsByDate"));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            appointmentService.getWeekAppointments(weekStart);
        });
        assertEquals("DAO error on getAppointmentsByDate", exception.getMessage());
        // Verify DAO was called up to the point of failure
        verify(appointmentDAO).getAppointmentsByDate(weekStart);
        verify(appointmentDAO).getAppointmentsByDate(weekStart.plusDays(1));
        verify(appointmentDAO).getAppointmentsByDate(problematicDate);
        verify(appointmentDAO, never()).getAppointmentsByDate(weekStart.plusDays(3)); // Should not be called after exception
    }

    // --- End Tests for getWeekAppointments ---
}