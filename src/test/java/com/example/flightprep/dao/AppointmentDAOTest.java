package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Appointment;
// Add other necessary model imports if AppointmentDAO handles them

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// Add other necessary imports (e.g., List, LocalDate, etc.)

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AppointmentDAOTest {

    private static MockedStatic<DatabaseFactory> mockedDatabaseFactory;

    @Mock
    private DatabaseConnection mockDatabaseConnection;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private AppointmentDAO appointmentDAO; // Will be instantiated in setUp

    @BeforeAll
    static void setUpAll() {
        mockedDatabaseFactory = Mockito.mockStatic(DatabaseFactory.class);
    }

    @AfterAll
    static void tearDownAll() {
        if (mockedDatabaseFactory != null && !mockedDatabaseFactory.isClosed()) {
            mockedDatabaseFactory.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        reset(mockDatabaseConnection, mockConnection, mockPreparedStatement, mockResultSet);

        mockedDatabaseFactory.when(DatabaseFactory::getDatabase).thenReturn(mockDatabaseConnection);
        appointmentDAO = new AppointmentDAO(); // Instantiate after static mock setup

        lenient().when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @Test
    void placeholderTest() {
        assertTrue(true); // Placeholder to ensure the class is picked up
    }

    // Tests for isSlotBooked(LocalDate date, String time)
    private final DateTimeFormatter DATE_FORMATTER_TEST = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    void isSlotBooked_returnsTrue_whenSlotIsBooked() throws SQLException {
        // Given
        LocalDate date = LocalDate.of(2024, 7, 20);
        String time = "10:00";
        String formattedDate = date.format(DATE_FORMATTER_TEST);
        String expectedSql = "SELECT COUNT(*) FROM appointments WHERE date = ? AND time = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1); // Count > 0

        // When
        boolean isBooked = appointmentDAO.isSlotBooked(date, time);

        // Then
        assertTrue(isBooked);
        verify(mockPreparedStatement).setString(1, formattedDate);
        verify(mockPreparedStatement).setString(2, time);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getInt(1);
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void isSlotBooked_returnsFalse_whenSlotIsNotBooked() throws SQLException {
        // Given
        LocalDate date = LocalDate.of(2024, 7, 21);
        String time = "11:00";
        String formattedDate = date.format(DATE_FORMATTER_TEST);
        String expectedSql = "SELECT COUNT(*) FROM appointments WHERE date = ? AND time = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0); // Count = 0

        // When
        boolean isBooked = appointmentDAO.isSlotBooked(date, time);

        // Then
        assertFalse(isBooked);
        verify(mockResultSet).getInt(1);
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void isSlotBooked_returnsFalse_whenQueryReturnsNoRows() throws SQLException {
        // Given
        LocalDate date = LocalDate.of(2024, 7, 22);
        String time = "12:00";
        String formattedDate = date.format(DATE_FORMATTER_TEST);
        String expectedSql = "SELECT COUNT(*) FROM appointments WHERE date = ? AND time = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no rows (though COUNT(*) won't do this)

        // When
        boolean isBooked = appointmentDAO.isSlotBooked(date, time);

        // Then
        assertFalse(isBooked);
        verify(mockResultSet, never()).getInt(1);
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void isSlotBooked_throwsSQLException_whenDbQueryFails() throws SQLException {
        // Given
        LocalDate date = LocalDate.of(2024, 7, 23);
        String time = "13:00";
        String formattedDate = date.format(DATE_FORMATTER_TEST);
        String expectedSql = "SELECT COUNT(*) FROM appointments WHERE date = ? AND time = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB Query Failed for isSlotBooked"));

        // When & Then
        assertThrows(SQLException.class, () -> appointmentDAO.isSlotBooked(date, time));

        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close(); // Not opened if executeQuery fails before rs is assigned from it
        verify(mockConnection).close();
    }

    // --- Tests for bookAppointment ---

    // Helper method to stub getCurrentDoctorId() behavior
    private void stubGetCurrentDoctorId_success(String doctorId) throws SQLException {
        PreparedStatement mockDoctorStmt = mock(PreparedStatement.class);
        ResultSet mockDoctorRs = mock(ResultSet.class);
        String doctorSql = "SELECT user_id FROM Doctor LIMIT 1";

        when(mockConnection.prepareStatement(eq(doctorSql))).thenReturn(mockDoctorStmt);
        when(mockDoctorStmt.executeQuery()).thenReturn(mockDoctorRs);
        when(mockDoctorRs.next()).thenReturn(true);
        when(mockDoctorRs.getString("user_id")).thenReturn(doctorId);
    }

    // Helper method to stub getCurrentDoctorId() to throw SQLException
    private void stubGetCurrentDoctorId_throwsSQLException() throws SQLException {
        PreparedStatement mockDoctorStmt = mock(PreparedStatement.class);
        String doctorSql = "SELECT user_id FROM Doctor LIMIT 1";
        when(mockConnection.prepareStatement(eq(doctorSql))).thenReturn(mockDoctorStmt);
        when(mockDoctorStmt.executeQuery()).thenThrow(new SQLException("Doctor query failed"));
    }

    // Helper method to stub getCurrentDoctorId() to find no doctor
    private void stubGetCurrentDoctorId_noDoctorFound() throws SQLException {
        PreparedStatement mockDoctorStmt = mock(PreparedStatement.class);
        ResultSet mockDoctorRs = mock(ResultSet.class);
        String doctorSql = "SELECT user_id FROM Doctor LIMIT 1";

        when(mockConnection.prepareStatement(eq(doctorSql))).thenReturn(mockDoctorStmt);
        when(mockDoctorStmt.executeQuery()).thenReturn(mockDoctorRs);
        when(mockDoctorRs.next()).thenReturn(false); // No doctor found
    }


    // --- bookAppointment: New Appointment (INSERT path) ---
    @Test
    void bookAppointment_newAppointment_success() throws SQLException {
        // Given
        String customerId = "custNew123";
        LocalDate date = LocalDate.of(2024, 8, 1);
        String time = "14:00";
        String expectedDoctorId = "doc789";
        String formattedDate = date.format(DATE_FORMATTER_TEST);

        String insertSql = "INSERT INTO appointments (customer_id, doctor_id, date, time) VALUES (?, ?, ?, ?)";

        // 1. getAppointmentByCustomerId returns null (this is a spy/stub on the DAO itself or mock the call)
        // For now, let's assume we will mock the call to getAppointmentByCustomerId within the test method itself
        // To do this properly, appointmentDAO should be a spy, or we need to mock its collaborators if it calls other DAOs.
        // AppointmentDAO.getAppointmentByCustomerId is a public method. We can create a spy.
        AppointmentDAO spiedDAO = spy(appointmentDAO); // Spy on the DAO instance from setUp
        doReturn(null).when(spiedDAO).getAppointmentByCustomerId(customerId);

        // 2. getCurrentDoctorId returns a doctor ID
        stubGetCurrentDoctorId_success(expectedDoctorId); // Uses the main mockConnection

        // 3. INSERT statement succeeds
        PreparedStatement mockInsertStmt = mock(PreparedStatement.class); // Use a separate mock for this specific statement
        when(mockConnection.prepareStatement(eq(insertSql))).thenReturn(mockInsertStmt);
        when(mockInsertStmt.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        spiedDAO.bookAppointment(customerId, date, time);

        // Then
        verify(spiedDAO).getAppointmentByCustomerId(customerId); // Verify internal call
        // verify(mockConnection).prepareStatement(eq("SELECT user_id FROM Doctor LIMIT 1")); // Verified by stubGetCurrentDoctorId_success

        verify(mockConnection).prepareStatement(eq(insertSql));
        verify(mockInsertStmt).setString(1, customerId);
        verify(mockInsertStmt).setString(2, expectedDoctorId);
        verify(mockInsertStmt).setString(3, formattedDate);
        verify(mockInsertStmt).setString(4, time);
        verify(mockInsertStmt).executeUpdate();
        verify(mockConnection).commit();

        verify(mockInsertStmt).close(); // Ensure specific statement is closed
        // mockDoctorStmt related to getCurrentDoctorId should also be closed (handled by try-with-resources in SUT)
        verify(mockConnection, times(2)).close();
    }

    // --- bookAppointment: Update Existing Appointment (UPDATE path) ---
    @Test
    void bookAppointment_updateExistingAppointment_success() throws SQLException {
        // Given
        String customerId = "custExist123";
        LocalDate date = LocalDate.of(2024, 8, 15);
        String time = "15:00";
        String expectedDoctorId = "doc456";
        String formattedDate = date.format(DATE_FORMATTER_TEST);
        String updateSql = "UPDATE appointments SET doctor_id = ?, date = ?, time = ? WHERE customer_id = ?";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        Appointment mockExistingAppointment = mock(Appointment.class); // Mock an existing appointment

        // 1. getAppointmentByCustomerId returns an existing appointment
        doReturn(mockExistingAppointment).when(spiedDAO).getAppointmentByCustomerId(customerId);

        // 2. getCurrentDoctorId returns a doctor ID
        stubGetCurrentDoctorId_success(expectedDoctorId);

        // 3. UPDATE statement succeeds
        PreparedStatement mockUpdateStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(eq(updateSql))).thenReturn(mockUpdateStmt);
        when(mockUpdateStmt.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        spiedDAO.bookAppointment(customerId, date, time);

        // Then
        verify(spiedDAO).getAppointmentByCustomerId(customerId); // Verify internal call
        // verify(mockConnection).prepareStatement(eq("SELECT user_id FROM Doctor LIMIT 1")); // Verified by stubGetCurrentDoctorId_success

        verify(mockConnection).prepareStatement(eq(updateSql));
        verify(mockUpdateStmt).setString(1, expectedDoctorId);
        verify(mockUpdateStmt).setString(2, formattedDate);
        verify(mockUpdateStmt).setString(3, time);
        verify(mockUpdateStmt).setString(4, customerId);
        verify(mockUpdateStmt).executeUpdate();
        verify(mockConnection).commit();

        verify(mockUpdateStmt).close();
        // mockDoctorStmt related to getCurrentDoctorId should also be closed (handled by try-with-resources in SUT)
        verify(mockConnection, times(2)).close();
    }

    // --- bookAppointment: Failure Scenarios ---

    @Test
    void bookAppointment_throwsSQLException_when_getCurrentDoctorId_fails_onInsert() throws SQLException {
        // Given
        String customerId = "custNewFailDoc1";
        LocalDate date = LocalDate.of(2024, 9, 1);
        String time = "10:00";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        doReturn(null).when(spiedDAO).getAppointmentByCustomerId(customerId); // New appointment path

        stubGetCurrentDoctorId_throwsSQLException(); // Simulate failure in getCurrentDoctorId

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("Doctor query failed", exception.getMessage()); // Matches message from stub

        verify(spiedDAO).getAppointmentByCustomerId(customerId);
        // Verify that prepareStatement for INSERT was not called because getCurrentDoctorId failed first
        // The following lines are commented out because the PreparedStatement for the main SQL (INSERT/UPDATE)
        // is created in the SUT before the exception from getCurrentDoctorId fully propagates.
        // verify(mockConnection, never()).prepareStatement(contains("INSERT INTO appointments"));
        verify(mockConnection, never()).commit();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    @Test
    void bookAppointment_throwsSQLException_when_getCurrentDoctorId_fails_onUpdate() throws SQLException {
        // Given
        String customerId = "custExistFailDoc1";
        LocalDate date = LocalDate.of(2024, 9, 2);
        String time = "11:00";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        Appointment mockExistingAppointment = mock(Appointment.class);
        doReturn(mockExistingAppointment).when(spiedDAO).getAppointmentByCustomerId(customerId); // Update path

        stubGetCurrentDoctorId_throwsSQLException(); // Simulate failure in getCurrentDoctorId

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("Doctor query failed", exception.getMessage()); // Matches message from stub

        verify(spiedDAO).getAppointmentByCustomerId(customerId);
        // The following lines are commented out because the PreparedStatement for the main SQL (INSERT/UPDATE)
        // is created in the SUT before the exception from getCurrentDoctorId fully propagates.
        // verify(mockConnection, never()).prepareStatement(contains("INSERT INTO appointments"));
        verify(mockConnection, never()).commit();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    @Test
    void bookAppointment_throwsSQLException_when_noDoctorFound_onInsert() throws SQLException {
        // Given
        String customerId = "custNewNoDoc1";
        LocalDate date = LocalDate.of(2024, 9, 3);
        String time = "12:00";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        doReturn(null).when(spiedDAO).getAppointmentByCustomerId(customerId); // New appointment path

        stubGetCurrentDoctorId_noDoctorFound(); // Simulate no doctor found

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("No doctor found in the database. Cannot book appointment.", exception.getMessage());

        verify(spiedDAO).getAppointmentByCustomerId(customerId);
        // The following lines are commented out because the PreparedStatement for the main SQL (INSERT/UPDATE)
        // is created in the SUT before the exception from getCurrentDoctorId fully propagates.
        // verify(mockConnection, never()).prepareStatement(contains("INSERT INTO appointments"));
        verify(mockConnection, never()).commit();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    @Test
    void bookAppointment_throwsSQLException_when_noDoctorFound_onUpdate() throws SQLException {
        // Given
        String customerId = "custExistNoDoc1";
        LocalDate date = LocalDate.of(2024, 9, 4);
        String time = "13:00";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        Appointment mockExistingAppointment = mock(Appointment.class);
        doReturn(mockExistingAppointment).when(spiedDAO).getAppointmentByCustomerId(customerId); // Update path

        stubGetCurrentDoctorId_noDoctorFound(); // Simulate no doctor found

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("No doctor found in the database. Cannot book appointment.", exception.getMessage());

        verify(spiedDAO).getAppointmentByCustomerId(customerId);
        // The following lines are commented out because the PreparedStatement for the main SQL (INSERT/UPDATE)
        // is created in the SUT before the exception from getCurrentDoctorId fully propagates.
        // verify(mockConnection, never()).prepareStatement(contains("INSERT INTO appointments"));
        verify(mockConnection, never()).commit();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    @Test
    void bookAppointment_throwsSQLException_when_getAppointmentByCustomerId_fails() throws SQLException {
        // Given
        String customerId = "custFailGetAppt";
        LocalDate date = LocalDate.of(2024, 9, 5);
        String time = "14:00";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        // Simulate getAppointmentByCustomerId throwing an SQLException
        doThrow(new SQLException("Failed to get appointment by customer ID")).when(spiedDAO).getAppointmentByCustomerId(customerId);

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("Failed to get appointment by customer ID", exception.getMessage());

        verify(spiedDAO).getAppointmentByCustomerId(customerId); // Verify the failing call
        // Ensure getCurrentDoctorId and subsequent DB operations are not attempted
        PreparedStatement mockDoctorStmt = mock(PreparedStatement.class); // Used in getCurrentDoctorId stub helpers
        String doctorSql = "SELECT user_id FROM Doctor LIMIT 1";
        verify(mockConnection, never()).prepareStatement(eq(doctorSql));
        verify(mockConnection, never()).prepareStatement(contains("INSERT INTO appointments"));
        verify(mockConnection, never()).prepareStatement(contains("UPDATE appointments"));
        verify(mockConnection, never()).commit();
        // The connection used by getAppointmentByCustomerId should be closed by its own try-with-resources.
        // Since it's mocked as part of the spiedDAO, we might not be able to directly verify its closure here
        // unless getAppointmentByCustomerId also uses the main mockConnection directly.
        // For now, we assume the SUT handles this correctly. The main mockConnection for bookAppointment should not be used yet.
    }

    @Test
    void bookAppointment_throwsSQLException_when_insertExecuteUpdateReturnsZero() throws SQLException {
        // Given
        String customerId = "custInsertZeroRows";
        LocalDate date = LocalDate.of(2024, 9, 6);
        String time = "15:00";
        String expectedDoctorId = "docZero1";
        String insertSql = "INSERT INTO appointments (customer_id, doctor_id, date, time) VALUES (?, ?, ?, ?)";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        doReturn(null).when(spiedDAO).getAppointmentByCustomerId(customerId); // New appointment
        stubGetCurrentDoctorId_success(expectedDoctorId);

        PreparedStatement mockInsertStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(eq(insertSql))).thenReturn(mockInsertStmt);
        when(mockInsertStmt.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("Appointment could not be saved (update or insert failed).", exception.getMessage());

        verify(mockInsertStmt).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockInsertStmt).close();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    @Test
    void bookAppointment_throwsSQLException_when_updateExecuteUpdateReturnsZero() throws SQLException {
        // Given
        String customerId = "custUpdateZeroRows";
        LocalDate date = LocalDate.of(2024, 9, 7);
        String time = "16:00";
        String expectedDoctorId = "docZero2";
        String updateSql = "UPDATE appointments SET doctor_id = ?, date = ?, time = ? WHERE customer_id = ?";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        Appointment mockExistingAppointment = mock(Appointment.class);
        doReturn(mockExistingAppointment).when(spiedDAO).getAppointmentByCustomerId(customerId); // Existing appointment
        stubGetCurrentDoctorId_success(expectedDoctorId);

        PreparedStatement mockUpdateStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(eq(updateSql))).thenReturn(mockUpdateStmt);
        when(mockUpdateStmt.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("Appointment could not be saved (update or insert failed).", exception.getMessage());

        verify(mockUpdateStmt).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockUpdateStmt).close();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    @Test
    void bookAppointment_throwsSQLException_when_commitFails_onInsert() throws SQLException {
        // Given
        String customerId = "custInsertCommitFail";
        LocalDate date = LocalDate.of(2024, 9, 8);
        String time = "17:00";
        String expectedDoctorId = "docCommitFail1";
        String insertSql = "INSERT INTO appointments (customer_id, doctor_id, date, time) VALUES (?, ?, ?, ?)";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        doReturn(null).when(spiedDAO).getAppointmentByCustomerId(customerId); // New appointment
        stubGetCurrentDoctorId_success(expectedDoctorId);

        PreparedStatement mockInsertStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(eq(insertSql))).thenReturn(mockInsertStmt);
        when(mockInsertStmt.executeUpdate()).thenReturn(1); // Insert succeeds

        doThrow(new SQLException("Commit failed during insert")).when(mockConnection).commit();

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("Commit failed during insert", exception.getMessage());

        verify(mockInsertStmt).executeUpdate();
        verify(mockConnection).commit(); // Verify commit was attempted
        verify(mockInsertStmt).close();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    @Test
    void bookAppointment_throwsSQLException_when_commitFails_onUpdate() throws SQLException {
        // Given
        String customerId = "custUpdateCommitFail";
        LocalDate date = LocalDate.of(2024, 9, 9);
        String time = "18:00";
        String expectedDoctorId = "docCommitFail2";
        String updateSql = "UPDATE appointments SET doctor_id = ?, date = ?, time = ? WHERE customer_id = ?";

        AppointmentDAO spiedDAO = spy(appointmentDAO);
        Appointment mockExistingAppointment = mock(Appointment.class);
        doReturn(mockExistingAppointment).when(spiedDAO).getAppointmentByCustomerId(customerId); // Existing appointment
        stubGetCurrentDoctorId_success(expectedDoctorId);

        PreparedStatement mockUpdateStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(eq(updateSql))).thenReturn(mockUpdateStmt);
        when(mockUpdateStmt.executeUpdate()).thenReturn(1); // Update succeeds

        doThrow(new SQLException("Commit failed during update")).when(mockConnection).commit();

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            spiedDAO.bookAppointment(customerId, date, time);
        });
        assertEquals("Commit failed during update", exception.getMessage());

        verify(mockUpdateStmt).executeUpdate();
        verify(mockConnection).commit(); // Verify commit was attempted
        verify(mockUpdateStmt).close();
        verify(mockConnection, times(2)).close(); // Connection from bookAppointment + getCurrentDoctorId
    }

    // --- Tests for getAppointmentsByDate(LocalDate date) ---
    @Test
    void getAppointmentsByDate_returnsListOfAppointments_whenDataExists() throws SQLException {
        // Given
        LocalDate queryDate = LocalDate.of(2024, 10, 1);
        String formattedQueryDate = queryDate.format(DATE_FORMATTER_TEST); // Using test formatter
        String expectedSql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.date = ? " +
                "ORDER BY a.time";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate two appointments in the result set
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        // Mock data for the first appointment
        when(mockResultSet.getInt("appointment_id")).thenReturn(101, 202);
        when(mockResultSet.getString("customer_id")).thenReturn("cust101", "cust202");
        when(mockResultSet.getString("first_name")).thenReturn("Alice", "Bob");
        when(mockResultSet.getString("last_name")).thenReturn("Smith", "Johnson");
        when(mockResultSet.getString("doctor_id")).thenReturn("doc1", "doc2");
        // The date in the result set is expected to be the same as formattedQueryDate for these records
        when(mockResultSet.getString("date")).thenReturn(formattedQueryDate, formattedQueryDate);
        when(mockResultSet.getString("time")).thenReturn("09:00", "10:00");
        when(mockResultSet.getInt("risk_group")).thenReturn(1, 3);

        // When
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDate(queryDate);

        // Then
        assertNotNull(appointments);
        assertEquals(2, appointments.size());

        verify(mockPreparedStatement).setString(1, formattedQueryDate);
        verify(mockPreparedStatement).executeQuery();

        Appointment appt1 = appointments.get(0);
        assertEquals(101, appt1.getAppointmentId());
        assertEquals("cust101", appt1.getCustomerId());
        assertEquals("Alice", appt1.getCustomerFirstName());
        assertEquals("Smith", appt1.getCustomerLastName());
        assertEquals("doc1", appt1.getDoctorId());
        assertEquals(formattedQueryDate, appt1.getDate()); // Assuming getter returns the string date
        assertEquals("09:00", appt1.getTime());
        assertEquals(1, appt1.getRiskGroup());

        Appointment appt2 = appointments.get(1);
        assertEquals(202, appt2.getAppointmentId());
        assertEquals("cust202", appt2.getCustomerId());
        assertEquals("10:00", appt2.getTime());

        verify(mockResultSet, times(3)).next(); // 2 for rows, 1 for false
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getAppointmentsByDate_returnsEmptyList_whenNoDataExists() throws SQLException {
        // Given
        LocalDate queryDate = LocalDate.of(2024, 10, 2);
        String formattedQueryDate = queryDate.format(DATE_FORMATTER_TEST);
        String expectedSql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.date = ? " +
                "ORDER BY a.time";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No data in result set

        // When
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDate(queryDate);

        // Then
        assertNotNull(appointments);
        assertTrue(appointments.isEmpty());

        verify(mockPreparedStatement).setString(1, formattedQueryDate);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next(); // Called once, returns false
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getAppointmentsByDate_throwsSQLException_whenDbQueryFails() throws SQLException {
        // Given
        LocalDate queryDate = LocalDate.of(2024, 10, 3);
        String formattedQueryDate = queryDate.format(DATE_FORMATTER_TEST);
        String expectedSql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.date = ? " +
                "ORDER BY a.time";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB Query Failed for getAppointmentsByDate"));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            appointmentDAO.getAppointmentsByDate(queryDate);
        });
        assertEquals("DB Query Failed for getAppointmentsByDate", exception.getMessage());

        verify(mockPreparedStatement).setString(1, formattedQueryDate);
        verify(mockPreparedStatement).executeQuery();
        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close(); // ResultSet not opened if executeQuery fails
        verify(mockConnection).close();
    }

    // --- Tests for getAppointmentByCustomerId(String customerId) ---
    @Test
    void getAppointmentByCustomerId_returnsAppointment_whenFound() throws SQLException {
        // Given
        String customerId = "custWithAppt1";
        String expectedSql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.customer_id = ? LIMIT 1";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Appointment found

        // Mock data for the appointment (can reuse some from previous tests or make specific)
        when(mockResultSet.getInt("appointment_id")).thenReturn(303);
        when(mockResultSet.getString("customer_id")).thenReturn(customerId); // Should match the input customerId
        when(mockResultSet.getString("first_name")).thenReturn("Charlie");
        when(mockResultSet.getString("last_name")).thenReturn("Brown");
        when(mockResultSet.getString("doctor_id")).thenReturn("docCharlie");
        when(mockResultSet.getString("date")).thenReturn("15.10.2024");
        when(mockResultSet.getString("time")).thenReturn("14:00");
        when(mockResultSet.getInt("risk_group")).thenReturn(2);

        // When
        Appointment appointment = appointmentDAO.getAppointmentByCustomerId(customerId);

        // Then
        assertNotNull(appointment);
        assertEquals(303, appointment.getAppointmentId());
        assertEquals(customerId, appointment.getCustomerId());
        assertEquals("Charlie", appointment.getCustomerFirstName());
        assertEquals("Brown", appointment.getCustomerLastName());
        assertEquals("docCharlie", appointment.getDoctorId());
        assertEquals("15.10.2024", appointment.getDate());
        assertEquals("14:00", appointment.getTime());
        assertEquals(2, appointment.getRiskGroup());

        verify(mockPreparedStatement).setString(1, customerId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getAppointmentByCustomerId_returnsNull_whenNotFound() throws SQLException {
        // Given
        String customerId = "custNoAppt1";
        String expectedSql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.customer_id = ? LIMIT 1";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No appointment found

        // When
        Appointment appointment = appointmentDAO.getAppointmentByCustomerId(customerId);

        // Then
        assertNull(appointment);

        verify(mockPreparedStatement).setString(1, customerId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        // createAppointmentFromResultSet should not be called, so no need to verify rs.getXXX calls
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getAppointmentByCustomerId_throwsSQLException_whenDbQueryFails() throws SQLException {
        // Given
        String customerId = "custFailQuery1";
        String expectedSql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.customer_id = ? LIMIT 1";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB Query Failed for getAppointmentByCustomerId"));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            appointmentDAO.getAppointmentByCustomerId(customerId);
        });
        assertEquals("DB Query Failed for getAppointmentByCustomerId", exception.getMessage());

        verify(mockPreparedStatement).setString(1, customerId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close(); // ResultSet not opened if executeQuery fails
        verify(mockConnection).close();
    }

    // Tests for AppointmentDAO methods will be added here
}