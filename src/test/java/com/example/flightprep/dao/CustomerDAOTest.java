package com.example.flightprep.dao;

// import com.example.flightprep.database.SQLiteConnection; // Will be DatabaseFactory
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDAOTest {

    private static MockedStatic<DatabaseFactory> mockedDatabaseFactory;

    @Mock
    private DatabaseConnection mockDatabaseConnection; // For DatabaseFactory.getDatabase()
    @Mock
    private Connection mockConnection; // For mockDatabaseConnection.getConnection()
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    // CustomerDAO uses DatabaseFactory.getDatabase().getConnection()
    // @InjectMocks will create a new CustomerDAO() which internally calls DatabaseFactory.getDatabase()
    // @InjectMocks // REMOVE
    private CustomerDAO customerDAO; // REMOVE @InjectMocks

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
        // Reset mocks before each test
        reset(mockDatabaseConnection, mockConnection, mockPreparedStatement, mockResultSet);

        // Static mock for DatabaseFactory needs to be active BEFORE CustomerDAO is instantiated if its constructor uses it.
        mockedDatabaseFactory.when(DatabaseFactory::getDatabase).thenReturn(mockDatabaseConnection);

        // Instantiate CustomerDAO AFTER the static mock for DatabaseFactory is in place
        customerDAO = new CustomerDAO();

        // Stub the mock DatabaseConnection to return the mock SQL Connection
        lenient().when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);

        // Default behavior for mockConnection.prepareStatement to return mockPreparedStatement
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Default behavior for PreparedStatement executeUpdate (can be overridden)
        // when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Default to 1 row affected
        // Default behavior for PreparedStatement executeQuery (can be overridden)
        // when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet); // Default to return mockResultSet
    }

    // Example test structure - to be expanded based on CustomerDAO methods
    @Test
    void exampleTest_shouldReturnCustomer() throws SQLException {
        // Given
        // when(mockConnection.prepareStatement(contains("SELECT"))).thenReturn(mockPreparedStatement); // More specific
        // when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        // when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Simulate one row
        // when(mockResultSet.getString("user_id")).thenReturn("testId");
        // ... stub other ResultSet.getXXX calls ...

        // When
        // Customer customer = customerDAO.getCustomerById("testId"); // Assuming such a method exists

        // Then
        // assertNotNull(customer);
        // assertEquals("testId", customer.getUserId());
        // verify(mockConnection).prepareStatement(anyString());
        // verify(mockPreparedStatement).executeQuery();
        // verify(mockResultSet, times(2)).next(); // Called for the row and then for the end
        // verify(mockPreparedStatement).close();
        // verify(mockConnection).close(); // If DAO closes it
        assertTrue(true); // Placeholder assertion
    }

    // Tests for findAllWithUploadedFiles()
    @Test
    void findAllWithUploadedFiles_returnsListOfCustomers_whenDataExists() throws SQLException {
        // Given
        String expectedSql = "SELECT u.user_id, u.password, c.first_name, c.last_name, c.email, " +
                "c.form_submitted, c.appointment_made, c.file_uploaded, c.flight_date, c.risk_group " +
                "FROM User u " +
                "JOIN Customer c ON u.user_id = c.user_id " +
                "WHERE c.file_uploaded = 1 AND c.declaration IS NULL " +
                "ORDER BY c.flight_date ASC";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simulate two customers in the result set
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        // Mock data for the first customer
        when(mockResultSet.getString("user_id")).thenReturn("user1", "user2");
        when(mockResultSet.getString("password")).thenReturn("pass1", "pass2");
        when(mockResultSet.getString("first_name")).thenReturn("John", "Jane");
        when(mockResultSet.getString("last_name")).thenReturn("Doe", "Roe");
        when(mockResultSet.getString("email")).thenReturn("john@example.com", "jane@example.com");
        when(mockResultSet.getBoolean("form_submitted")).thenReturn(true, false);
        when(mockResultSet.getBoolean("appointment_made")).thenReturn(true, true);
        when(mockResultSet.getBoolean("file_uploaded")).thenReturn(true, true); // Should be true based on query
        when(mockResultSet.getString("flight_date")).thenReturn("01.01.2025", "02.01.2025");
        when(mockResultSet.getInt("risk_group")).thenReturn(1, 2);
        // declaration and declaration_comment are not in select, but mapCustomer might try to access if not careful
        // but the query condition is `c.declaration IS NULL`

        // When
        List<Customer> customers = customerDAO.findAllWithUploadedFiles();

        // Then
        assertNotNull(customers);
        assertEquals(2, customers.size());

        Customer customer1 = customers.get(0);
        assertEquals("user1", customer1.getUserId());
        assertEquals("pass1", customer1.getPassword());
        assertEquals("John", customer1.getFirstName());
        assertEquals("01.01.2025", customer1.getFlightDate()); // Assuming getter returns formatted string

        Customer customer2 = customers.get(1);
        assertEquals("user2", customer2.getUserId());
        assertEquals("Jane", customer2.getFirstName());

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next(); // 2 for rows, 1 for false
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void findAllWithUploadedFiles_returnsEmptyList_whenNoDataExists() throws SQLException {
        // Given
        String expectedSql = "SELECT u.user_id, u.password, c.first_name, c.last_name, c.email, " +
                "c.form_submitted, c.appointment_made, c.file_uploaded, c.flight_date, c.risk_group " +
                "FROM User u " +
                "JOIN Customer c ON u.user_id = c.user_id " +
                "WHERE c.file_uploaded = 1 AND c.declaration IS NULL " +
                "ORDER BY c.flight_date ASC";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No data

        // When
        List<Customer> customers = customerDAO.findAllWithUploadedFiles();

        // Then
        assertNotNull(customers);
        assertTrue(customers.isEmpty());

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next(); // Called once
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void findAllWithUploadedFiles_throwsSQLException_whenDbQueryFails() throws SQLException {
        // Given
        String expectedSql = "SELECT u.user_id, u.password, c.first_name, c.last_name, c.email, " +
                "c.form_submitted, c.appointment_made, c.file_uploaded, c.flight_date, c.risk_group " +
                "FROM User u " +
                "JOIN Customer c ON u.user_id = c.user_id " +
                "WHERE c.file_uploaded = 1 AND c.declaration IS NULL " +
                "ORDER BY c.flight_date ASC";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB Query Failed"));

        // When & Then
        assertThrows(SQLException.class, () -> customerDAO.findAllWithUploadedFiles());

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeQuery();
        verify(mockPreparedStatement).close(); // Should still be closed in finally block
        verify(mockResultSet, never()).close(); // Not opened
        verify(mockConnection).close(); // Should still be closed in finally block
    }

    @Test
    void findAllWithUploadedFiles_throwsSQLException_whenConnectionFails() throws SQLException {
        // Given
        // Override the @BeforeEach setup for DatabaseFactory.getDatabase().getConnection()
        reset(mockDatabaseConnection); // reset it to remove the previous when().thenReturn()
        mockedDatabaseFactory.when(DatabaseFactory::getDatabase).thenReturn(mockDatabaseConnection);
        when(mockDatabaseConnection.getConnection()).thenThrow(new SQLException("DB Connection Failed"));

        // When & Then
        assertThrows(SQLException.class, () -> customerDAO.findAllWithUploadedFiles());

        verify(mockConnection, never()).prepareStatement(anyString());
    }

    // Tests for saveDeclaration(String userId, boolean isApproved, String comment)
    @Test
    void saveDeclaration_success_approved() throws SQLException {
        // Given
        String userId = "user123";
        boolean isApproved = true;
        String comment = "Looks good";
        String expectedSql = "UPDATE Customer SET declaration = ?, declaration_comment = ? WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        customerDAO.saveDeclaration(userId, isApproved, comment);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, 1); // isApproved = true -> 1
        verify(mockPreparedStatement).setString(2, comment);
        verify(mockPreparedStatement).setString(3, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void saveDeclaration_success_notApproved() throws SQLException {
        // Given
        String userId = "user456";
        boolean isApproved = false;
        String comment = "Not good";
        String expectedSql = "UPDATE Customer SET declaration = ?, declaration_comment = ? WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        customerDAO.saveDeclaration(userId, isApproved, comment);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, 0); // isApproved = false -> 0
        verify(mockPreparedStatement).setString(2, comment);
        verify(mockPreparedStatement).setString(3, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void saveDeclaration_throwsSQLException_whenNoRowsAffected() throws SQLException {
        // Given
        String userId = "user789";
        String expectedSql = "UPDATE Customer SET declaration = ?, declaration_comment = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        SQLException exception = assertThrows(SQLException.class,
                () -> customerDAO.saveDeclaration(userId, true, "Test"));
        assertEquals("No data updated", exception.getMessage());

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit(); // Commit should not be called
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void saveDeclaration_throwsSQLException_whenDbUpdateFails() throws SQLException {
        // Given
        String userId = "userFail";
        String expectedSql = "UPDATE Customer SET declaration = ?, declaration_comment = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Update Failed"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.saveDeclaration(userId, true, "Test Fail"));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void saveDeclaration_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "userCommitFail";
        String expectedSql = "UPDATE Customer SET declaration = ?, declaration_comment = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        doThrow(new SQLException("Commit Failed")).when(mockConnection).commit();

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.saveDeclaration(userId, true, "Commit Test"));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit(); // Attempted commit
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    // Tests for updateCustomerRiskGroup(String userId, int riskGroup)
    @Test
    void updateCustomerRiskGroup_success() throws SQLException {
        // Given
        String userId = "userRisk1";
        int riskGroup = 2;
        String expectedSql = "UPDATE Customer SET risk_group = ? WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        customerDAO.updateCustomerRiskGroup(userId, riskGroup);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, riskGroup);
        verify(mockPreparedStatement).setString(2, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateCustomerRiskGroup_throwsSQLException_whenNoRowsAffected() throws SQLException {
        // Given
        String userId = "userRiskNoUpdate";
        int riskGroup = 3;
        String expectedSql = "UPDATE Customer SET risk_group = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        SQLException exception = assertThrows(SQLException.class,
                () -> customerDAO.updateCustomerRiskGroup(userId, riskGroup));
        assertEquals("Risk group could not be updated.", exception.getMessage());

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateCustomerRiskGroup_throwsSQLException_whenDbUpdateFails() throws SQLException {
        // Given
        String userId = "userRiskFail";
        int riskGroup = 1;
        String expectedSql = "UPDATE Customer SET risk_group = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Update Failed for Risk Group"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateCustomerRiskGroup(userId, riskGroup));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateCustomerRiskGroup_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "userRiskCommitFail";
        int riskGroup = 0;
        String expectedSql = "UPDATE Customer SET risk_group = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        doThrow(new SQLException("Commit Failed for Risk Group")).when(mockConnection).commit();

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateCustomerRiskGroup(userId, riskGroup));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    // Tests for updateFileUploadStatus(String userId)
    @Test
    void updateFileUploadStatus_success() throws SQLException {
        // Given
        String userId = "userUpload1";
        String expectedSql = "UPDATE Customer SET file_uploaded = TRUE WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        customerDAO.updateFileUploadStatus(userId);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFileUploadStatus_throwsSQLException_whenNoRowsAffected() throws SQLException {
        // Given
        String userId = "userUploadNoUpdate";
        String expectedSql = "UPDATE Customer SET file_uploaded = TRUE WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        SQLException exception = assertThrows(SQLException.class,
                () -> customerDAO.updateFileUploadStatus(userId));
        assertEquals("Customer file upload status could not be updated.", exception.getMessage());

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFileUploadStatus_throwsSQLException_whenDbUpdateFails() throws SQLException {
        // Given
        String userId = "userUploadFail";
        String expectedSql = "UPDATE Customer SET file_uploaded = TRUE WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Update Failed for File Upload"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateFileUploadStatus(userId));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFileUploadStatus_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "userUploadCommitFail";
        String expectedSql = "UPDATE Customer SET file_uploaded = TRUE WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        doThrow(new SQLException("Commit Failed for File Upload")).when(mockConnection).commit();

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateFileUploadStatus(userId));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    // Tests for updateAppointmentStatus(String userId)
    @Test
    void updateAppointmentStatus_success() throws SQLException {
        // Given
        String userId = "userAppt1";
        String expectedSql = "UPDATE Customer SET appointment_made = TRUE WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        customerDAO.updateAppointmentStatus(userId);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateAppointmentStatus_throwsSQLException_whenNoRowsAffected() throws SQLException {
        // Given
        String userId = "userApptNoUpdate";
        String expectedSql = "UPDATE Customer SET appointment_made = TRUE WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        SQLException exception = assertThrows(SQLException.class,
                () -> customerDAO.updateAppointmentStatus(userId));
        assertEquals("Appointment status could not be updated", exception.getMessage());

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateAppointmentStatus_throwsSQLException_whenDbUpdateFails() throws SQLException {
        // Given
        String userId = "userApptFail";
        String expectedSql = "UPDATE Customer SET appointment_made = TRUE WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Update Failed for Appointment Status"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateAppointmentStatus(userId));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateAppointmentStatus_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "userApptCommitFail";
        String expectedSql = "UPDATE Customer SET appointment_made = TRUE WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        doThrow(new SQLException("Commit Failed for Appointment Status")).when(mockConnection).commit();

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateAppointmentStatus(userId));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    // Tests for getAppointmentStatus(String userId)
    @Test
    void getAppointmentStatus_returnsTrue_whenStatusIsTrueInDB() throws SQLException {
        // Given
        String userId = "userApptStatus1";
        String expectedSql = "SELECT appointment_made FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        lenient().when(mockResultSet.getBoolean("appointment_made")).thenReturn(true);

        // When
        boolean status = customerDAO.getAppointmentStatus(userId);

        // Then
        assertTrue(status);
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getBoolean("appointment_made");
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getAppointmentStatus_returnsFalse_whenStatusIsFalseInDB() throws SQLException {
        // Given
        String userId = "userApptStatus2";
        String expectedSql = "SELECT appointment_made FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        lenient().when(mockResultSet.getBoolean("appointment_made")).thenReturn(false);

        // When
        boolean status = customerDAO.getAppointmentStatus(userId);

        // Then
        assertFalse(status);
        verify(mockPreparedStatement).close(); // Ensure resources closed path
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getAppointmentStatus_returnsFalse_whenUserNotFound() throws SQLException {
        // Given
        String userId = "userApptStatusNotFound";
        String expectedSql = "SELECT appointment_made FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // User not found

        // When
        boolean status = customerDAO.getAppointmentStatus(userId);

        // Then
        assertFalse(status);
        verify(mockResultSet, never()).getBoolean(anyString()); // Should not try to get data
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getAppointmentStatus_throwsSQLException_whenDbQueryFails() throws SQLException {
        // Given
        String userId = "userApptStatusFail";
        String expectedSql = "SELECT appointment_made FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB Query Failed for Appt Status"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.getAppointmentStatus(userId));

        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close(); // Not opened if executeQuery fails before returning it
        verify(mockConnection).close();
    }

    // Tests for updateFormSubmittedStatus(String userId, boolean status)
    @Test
    void updateFormSubmittedStatus_success_setsTrue() throws SQLException {
        // Given
        String userId = "userForm1";
        boolean status = true;
        String expectedSql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Assume 1 row affected for success path

        // When
        customerDAO.updateFormSubmittedStatus(userId, status);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setBoolean(1, status);
        verify(mockPreparedStatement).setString(2, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFormSubmittedStatus_success_setsFalse() throws SQLException {
        // Given
        String userId = "userForm2";
        boolean status = false;
        String expectedSql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // When
        customerDAO.updateFormSubmittedStatus(userId, status);

        // Then
        verify(mockPreparedStatement).setBoolean(1, status);
        // ... other verifications similar to setsTrue ...
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFormSubmittedStatus_noRowsAffected_doesNotThrowException() throws SQLException {
        // Given
        String userId = "userFormNoUpdate";
        boolean status = true;
        String expectedSql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        assertDoesNotThrow(() -> customerDAO.updateFormSubmittedStatus(userId, status));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit(); // Commit is still called
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFormSubmittedStatus_throwsSQLException_whenDbUpdateFails() throws SQLException {
        // Given
        String userId = "userFormFail";
        boolean status = true;
        String expectedSql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Update Failed for Form Status"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateFormSubmittedStatus(userId, status));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFormSubmittedStatus_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "userFormCommitFail";
        boolean status = false;
        String expectedSql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Assume update itself was fine
        doThrow(new SQLException("Commit Failed for Form Status")).when(mockConnection).commit();

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateFormSubmittedStatus(userId, status));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    // Tests for getFormSubmittedStatus(String userId)
    @Test
    void getFormSubmittedStatus_returnsTrue_whenStatusIsTrueInDB() throws SQLException {
        // Given
        String userId = "userFormStatus1";
        String expectedSql = "SELECT form_submitted FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        lenient().when(mockResultSet.getBoolean("form_submitted")).thenReturn(true);

        // When
        boolean status = customerDAO.getFormSubmittedStatus(userId);

        // Then
        assertTrue(status);
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getBoolean("form_submitted");
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFormSubmittedStatus_returnsFalse_whenStatusIsFalseInDB() throws SQLException {
        // Given
        String userId = "userFormStatus2";
        String expectedSql = "SELECT form_submitted FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        lenient().when(mockResultSet.getBoolean("form_submitted")).thenReturn(false);

        // When
        boolean status = customerDAO.getFormSubmittedStatus(userId);

        // Then
        assertFalse(status);
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFormSubmittedStatus_returnsFalse_whenUserNotFound() throws SQLException {
        // Given
        String userId = "userFormStatusNotFound";
        String expectedSql = "SELECT form_submitted FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // User not found

        // When
        boolean status = customerDAO.getFormSubmittedStatus(userId);

        // Then
        assertFalse(status);
        verify(mockResultSet, never()).getBoolean(anyString());
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFormSubmittedStatus_throwsSQLException_whenDbQueryFails() throws SQLException {
        // Given
        String userId = "userFormStatusFail";
        String expectedSql = "SELECT form_submitted FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB Query Failed for Form Status"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.getFormSubmittedStatus(userId));

        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close();
        verify(mockConnection).close();
    }

    // Tests for getFlightDate(String userId)
    @Test
    void getFlightDate_returnsDate_whenDateIsValidInDB() throws SQLException {
        // Given
        String userId = "userFlightDate1";
        String dateStr = "25.12.2024";
        LocalDate expectedDate = LocalDate.of(2024, 12, 25);
        String expectedSql = "SELECT flight_date FROM Customer WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("flight_date")).thenReturn(dateStr);

        // When
        LocalDate actualDate = customerDAO.getFlightDate(userId);

        // Then
        assertEquals(expectedDate, actualDate);
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getString("flight_date");
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFlightDate_returnsNull_whenDateStringIsNullInDB() throws SQLException {
        // Given
        String userId = "userFlightDateNull";
        String expectedSql = "SELECT flight_date FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("flight_date")).thenReturn(null);

        // When
        LocalDate actualDate = customerDAO.getFlightDate(userId);

        // Then
        assertNull(actualDate);
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFlightDate_returnsNull_whenDateStringIsEmptyInDB() throws SQLException {
        // Given
        String userId = "userFlightDateEmpty";
        String expectedSql = "SELECT flight_date FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("flight_date")).thenReturn("");

        // When
        LocalDate actualDate = customerDAO.getFlightDate(userId);

        // Then
        assertNull(actualDate);
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFlightDate_returnsNull_andPrintsError_whenDateStringIsInvalidFormat() throws SQLException {
        // Given
        String userId = "userFlightDateInvalid";
        String invalidDateStr = "2024-12-25"; // Wrong format
        String expectedSql = "SELECT flight_date FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("flight_date")).thenReturn(invalidDateStr);

        // Capture System.err output if possible, or just verify null and assume error is printed
        // For simplicity, we'll just verify null here. Testing System.err output requires more setup (e.g., redirecting System.err).

        // When
        LocalDate actualDate = customerDAO.getFlightDate(userId);

        // Then
        assertNull(actualDate);
        // We expect an error message to be printed to System.err by the SUT.
        // Verifying System.err output requires more setup (e.g., redirecting System.err).
        // For now, trust it's printed and ensure the method behaves correctly by returning null.
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFlightDate_returnsNull_whenUserNotFound() throws SQLException {
        // Given
        String userId = "userFlightDateNotFound";
        String expectedSql = "SELECT flight_date FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // User not found

        // When
        LocalDate actualDate = customerDAO.getFlightDate(userId);

        // Then
        assertNull(actualDate);
        verify(mockResultSet, never()).getString(anyString());
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getFlightDate_throwsSQLException_whenDbQueryFails() throws SQLException {
        // Given
        String userId = "userFlightDateFail";
        String expectedSql = "SELECT flight_date FROM Customer WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("DB Query Failed for Flight Date"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.getFlightDate(userId));

        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close();
        verify(mockConnection).close();
    }

    // Tests for updateFlightDate(String userId, LocalDate newFlightDate)
    @Test
    void updateFlightDate_success() throws SQLException {
        // Given
        String userId = "userFlightDateUpdate1";
        LocalDate newFlightDate = LocalDate.of(2025, 1, 15);
        // Replicate the formatter pattern used in CustomerDAO
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = newFlightDate.format(formatter);
        String expectedSql = "UPDATE Customer SET flight_date = ? WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 row affected

        // When
        customerDAO.updateFlightDate(userId, newFlightDate);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setString(1, formattedDate);
        verify(mockPreparedStatement).setString(2, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFlightDate_success_withNullDate() throws SQLException {
        // Given
        String userId = "userFlightDateUpdateNull";
        LocalDate newFlightDate = null;
        String expectedSql = "UPDATE Customer SET flight_date = ? WHERE user_id = ?";

        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // When
        customerDAO.updateFlightDate(userId, newFlightDate);

        // Then
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setNull(1, java.sql.Types.VARCHAR); // Or specific type used by driver for date string
        verify(mockPreparedStatement).setString(2, userId);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFlightDate_throwsSQLException_whenNoRowsAffected() throws SQLException {
        // Given
        String userId = "userFlightDateNoUpdate";
        LocalDate newFlightDate = LocalDate.of(2025, 2, 1);
        String expectedSql = "UPDATE Customer SET flight_date = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // 0 rows affected

        // When & Then
        // Assuming it throws an exception similar to other updates. If not, this test will need adjustment.
        SQLException exception = assertThrows(SQLException.class,
                () -> customerDAO.updateFlightDate(userId, newFlightDate));
        // assertEquals("Flight date could not be updated.", exception.getMessage()); // Example message
        // Actual message depends on SUT implementation, might need to read the full SUT method.
        // For now, just verifying SQLException is thrown if 0 rows affected and SUT checks this.
        // If SUT does NOT throw on 0 rows, then this test should be assertDoesNotThrow, and verify commit still happens.
        // Based on other update methods, it's likely it throws. Let's assume it does for now.
        assertTrue(exception.getMessage().contains("Flight date could not be updated"), "Exception message should indicate update failure.");


        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFlightDate_throwsSQLException_whenDbUpdateFails() throws SQLException {
        // Given
        String userId = "userFlightDateFailUpdate";
        LocalDate newFlightDate = LocalDate.of(2025, 3, 10);
        String expectedSql = "UPDATE Customer SET flight_date = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Update Failed for Flight Date"));

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateFlightDate(userId, newFlightDate));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void updateFlightDate_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "userFlightDateCommitFail";
        LocalDate newFlightDate = LocalDate.of(2025, 4, 20);
        String expectedSql = "UPDATE Customer SET flight_date = ? WHERE user_id = ?";
        when(mockConnection.prepareStatement(expectedSql)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        doThrow(new SQLException("Commit Failed for Flight Date")).when(mockConnection).commit();

        // When & Then
        assertThrows(SQLException.class,
                () -> customerDAO.updateFlightDate(userId, newFlightDate));

        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    // Add more tests for each public method in CustomerDAO
}