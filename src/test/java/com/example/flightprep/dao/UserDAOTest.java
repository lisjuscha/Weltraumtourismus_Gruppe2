package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;
import com.example.flightprep.model.Doctor;
import com.example.flightprep.model.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    private static MockedStatic<DatabaseFactory> mockedDatabaseFactory;

    @Mock
    private DatabaseConnection mockDatabaseConnection;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private UserDAO userDAO; // SUT

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
        // Reset mocks before each test to ensure test isolation
        reset(mockDatabaseConnection, mockConnection, mockPreparedStatement, mockResultSet);

        // Static mock for DatabaseFactory must be configured before DAO instantiation
        mockedDatabaseFactory.when(DatabaseFactory::getDatabase).thenReturn(mockDatabaseConnection);

        // Instantiate UserDAO AFTER the static mock for DatabaseFactory is in place
        userDAO = new UserDAO();

        // Default stub for DatabaseConnection to return the mock SQL Connection
        // Made lenient to avoid UnnecessaryStubbingException if not all tests use this directly
        lenient().when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);

        // Default stub for Connection.prepareStatement to return the mock PreparedStatement
        // Made lenient for the same reason
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @Test
    void placeholderTest_ensureClassIsTested() {
        assertTrue(true, "Placeholder test to ensure UserDAOTest is picked up by the test runner.");
    }

    // Tests for getUserByUserID(String userId)

    // Tests for getCustomerByUserId(String userId, String password)
    @Test
    void getCustomerByUserId_returnsCustomer_whenFound() throws SQLException {
        // Given
        String userId = "testCust1";
        String password = "testPass1";
        String expectedSql = "SELECT first_name, last_name, email, form_submitted, appointment_made, " +
                "file_uploaded, flight_date, risk_group FROM Customer WHERE user_id = ?";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Customer found

        // Mock data for the customer
        when(mockResultSet.getString("first_name")).thenReturn("Test");
        when(mockResultSet.getString("last_name")).thenReturn("CustomerOne");
        when(mockResultSet.getString("email")).thenReturn("test.cust1@example.com");
        when(mockResultSet.getBoolean("form_submitted")).thenReturn(true);
        when(mockResultSet.getBoolean("appointment_made")).thenReturn(false);
        when(mockResultSet.getBoolean("file_uploaded")).thenReturn(true);
        when(mockResultSet.getString("flight_date")).thenReturn("01.01.2025");
        when(mockResultSet.getInt("risk_group")).thenReturn(1);

        // When
        Customer customer = userDAO.getCustomerByUserId(userId, password);

        // Then
        assertNotNull(customer);
        assertEquals(userId, customer.getUserId());
        assertEquals(password, customer.getPassword()); // Password is passed to constructor, not from ResultSet for this method
        assertEquals("Test", customer.getFirstName());
        assertEquals("CustomerOne", customer.getLastName());
        assertEquals("test.cust1@example.com", customer.getEmail());
        assertTrue(customer.isFormSubmitted());
        assertFalse(customer.isAppointmentMade());
        assertTrue(customer.isFileUploaded());
        assertEquals("01.01.2025", customer.getFlightDate());
        assertEquals(1, customer.getRiskGroup());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection).commit(); // commit is called in SUT
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getCustomerByUserId_returnsNull_whenNotFound() throws SQLException {
        // Given
        String userId = "nonExistentCust";
        String password = "anyPass";
        String expectedSql = "SELECT first_name, last_name, email, form_submitted, appointment_made, " +
                "file_uploaded, flight_date, risk_group FROM Customer WHERE user_id = ?";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Customer not found

        // When
        Customer customer = userDAO.getCustomerByUserId(userId, password);

        // Then
        assertNull(customer);

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection, never()).commit(); // Commit should not be called if customer not found
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getCustomerByUserId_throwsSQLException_whenQueryFails() throws SQLException {
        // Given
        String userId = "testCustError";
        String password = "anyPassError";
        String expectedSql = "SELECT first_name, last_name, email, form_submitted, appointment_made, " +
                "file_uploaded, flight_date, risk_group FROM Customer WHERE user_id = ?";
        String exceptionMessage = "DB query failed for getCustomerByUserId";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException(exceptionMessage));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            userDAO.getCustomerByUserId(userId, password);
        });
        assertEquals(exceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close(); // ResultSet not opened
        verify(mockConnection).close();
    }

    @Test
    void getCustomerByUserId_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "testCustCommitFail";
        String password = "passCommitFail";
        String expectedSql = "SELECT first_name, last_name, email, form_submitted, appointment_made, " +
                "file_uploaded, flight_date, risk_group FROM Customer WHERE user_id = ?";
        String commitExceptionMessage = "DB commit failed for getCustomerByUserId";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Customer data found

        // Mock data for the customer - necessary for constructor to be called before commit
        when(mockResultSet.getString("first_name")).thenReturn("Commit");
        when(mockResultSet.getString("last_name")).thenReturn("FailCustomer");
        when(mockResultSet.getString("email")).thenReturn("commit.fail@example.com");
        when(mockResultSet.getBoolean("form_submitted")).thenReturn(true);
        when(mockResultSet.getBoolean("appointment_made")).thenReturn(false);
        when(mockResultSet.getBoolean("file_uploaded")).thenReturn(false);
        when(mockResultSet.getString("flight_date")).thenReturn("10.10.2025");
        when(mockResultSet.getInt("risk_group")).thenReturn(0);

        doThrow(new SQLException(commitExceptionMessage)).when(mockConnection).commit();

        // When & Then
        // The SQLException from commit() should propagate out
        SQLException exception = assertThrows(SQLException.class, () -> {
            userDAO.getCustomerByUserId(userId, password);
        });
        assertEquals(commitExceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection).commit(); // Commit was attempted
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close(); // Connection still closed in finally block
    }

    // Tests for getDoctorByUserId(String userId, String password)
    @Test
    void getDoctorByUserId_returnsDoctor_whenFound() throws SQLException {
        // Given
        String userId = "testDoc1";
        String password = "docPass1";
        String expectedSql = "SELECT first_name, last_name, email FROM Doctor WHERE user_id = ?";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Doctor found

        // Mock data for the doctor
        when(mockResultSet.getString("first_name")).thenReturn("Doctor");
        when(mockResultSet.getString("last_name")).thenReturn("TestOne");
        when(mockResultSet.getString("email")).thenReturn("doc.test1@example.com");

        // When
        Doctor doctor = userDAO.getDoctorByUserId(userId, password);

        // Then
        assertNotNull(doctor);
        assertEquals(userId, doctor.getUserId());
        assertEquals(password, doctor.getPassword()); // Password is passed to constructor
        assertEquals("Doctor", doctor.getFirstName());
        assertEquals("TestOne", doctor.getLastName());
        assertEquals("doc.test1@example.com", doctor.getEmail());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection).commit(); // commit is called in SUT
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getDoctorByUserId_returnsNull_whenNotFound() throws SQLException {
        // Given
        String userId = "nonExistentDoc";
        String password = "anyPass";
        String expectedSql = "SELECT first_name, last_name, email FROM Doctor WHERE user_id = ?";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Doctor not found

        // When
        Doctor doctor = userDAO.getDoctorByUserId(userId, password);

        // Then
        assertNull(doctor);

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection, never()).commit(); // Commit should not be called
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getDoctorByUserId_throwsSQLException_whenQueryFails() throws SQLException {
        // Given
        String userId = "testDocError";
        String password = "anyPassError";
        String expectedSql = "SELECT first_name, last_name, email FROM Doctor WHERE user_id = ?";
        String exceptionMessage = "DB query failed for getDoctorByUserId";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException(exceptionMessage));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            userDAO.getDoctorByUserId(userId, password);
        });
        assertEquals(exceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close(); // ResultSet not opened
        verify(mockConnection).close();
    }

    @Test
    void getDoctorByUserId_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "testDocCommitFail";
        String password = "passDocCommitFail";
        String expectedSql = "SELECT first_name, last_name, email FROM Doctor WHERE user_id = ?";
        String commitExceptionMessage = "DB commit failed for getDoctorByUserId";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Doctor data found

        // Mock data for the doctor - necessary for constructor to be called before commit
        when(mockResultSet.getString("first_name")).thenReturn("CommitFail");
        when(mockResultSet.getString("last_name")).thenReturn("Doctor");
        when(mockResultSet.getString("email")).thenReturn("doc.commit.fail@example.com");

        doThrow(new SQLException(commitExceptionMessage)).when(mockConnection).commit();

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            userDAO.getDoctorByUserId(userId, password);
        });
        assertEquals(commitExceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection).commit(); // Commit was attempted
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close(); // Connection still closed in finally block
    }

    // Tests for getUserByUserID(String userId)
    @Test
    void getUserByUserID_returnsCustomer_whenRoleIsCustomer() throws SQLException {
        // Given
        String userId = "custUser1";
        String passwordFromUserTable = "custPass"; // Password fetched from User table
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";

        UserDAO spiedUserDAO = spy(userDAO);
        Customer mockCustomer = mock(Customer.class); // Mocked result from getCustomerByUserId

        // 1. Mock initial query to User table
        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        when(mockResultSet.getString("role")).thenReturn("customer");
        when(mockResultSet.getString("password")).thenReturn(passwordFromUserTable);

        // 2. Stub the call to the spied getCustomerByUserId
        // Note: getCustomerByUserId itself throws SQLException, but getUserByUserID catches and wraps it
        doReturn(mockCustomer).when(spiedUserDAO).getCustomerByUserId(userId, passwordFromUserTable);

        // When
        User user = spiedUserDAO.getUserByUserID(userId);

        // Then
        assertNotNull(user);
        assertSame(mockCustomer, user, "Should return the mocked Customer object");

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getString("role");
        verify(mockResultSet).getString("password");
        verify(spiedUserDAO).getCustomerByUserId(userId, passwordFromUserTable); // Verify internal call
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getUserByUserID_returnsDoctor_whenRoleIsDoctor() throws SQLException {
        // Given
        String userId = "docUser1";
        String passwordFromUserTable = "docPass";
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";

        UserDAO spiedUserDAO = spy(userDAO);
        Doctor mockDoctor = mock(Doctor.class); // Mocked result from getDoctorByUserId

        // 1. Mock initial query to User table
        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        when(mockResultSet.getString("role")).thenReturn("doctor");
        when(mockResultSet.getString("password")).thenReturn(passwordFromUserTable);

        // 2. Stub the call to the spied getDoctorByUserId
        doReturn(mockDoctor).when(spiedUserDAO).getDoctorByUserId(userId, passwordFromUserTable);

        // When
        User user = spiedUserDAO.getUserByUserID(userId);

        // Then
        assertNotNull(user);
        assertSame(mockDoctor, user, "Should return the mocked Doctor object");

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getString("role");
        verify(mockResultSet).getString("password");
        verify(spiedUserDAO).getDoctorByUserId(userId, passwordFromUserTable); // Verify internal call
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getUserByUserID_returnsNull_whenRoleIsUnknown() throws SQLException {
        // Given
        String userId = "unknownRoleUser1";
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";

        // No need to spy for this test as sub-methods won't be called.

        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // User found
        when(mockResultSet.getString("role")).thenReturn("admin"); // Unknown role
        // Password might or might not be read, depending on SUT logic order, let's assume it's read.
        lenient().when(mockResultSet.getString("password")).thenReturn("anyPass");

        // When
        User user = userDAO.getUserByUserID(userId);

        // Then
        assertNull(user, "User should be null for an unknown role");

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getString("role");
        verify(mockResultSet).getString("password"); // Corrected: Password IS read by SUT before role check
        // Verify getCustomerByUserId and getDoctorByUserId were NOT called
        // (userDAO is not a spy in this test's default setup, so no explicit verify needed for non-calls to sub-methods)
        verify(mockConnection).commit(); // Commit is called in the SUT after rs.next() is false or role is unknown
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getUserByUserID_returnsNull_whenUserNotFoundInUserTable() throws SQLException {
        // Given
        String userId = "ghostUser1";
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";

        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // User not found in User table

        // When
        User user = userDAO.getUserByUserID(userId);

        // Then
        assertNull(user, "User should be null if not found in User table");

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet, never()).getString("role"); // Corrected: Role not read if user not found
        verify(mockResultSet, never()).getString("password"); // Corrected: Password not read
        verify(mockConnection).commit(); // Commit is called in SUT when rs.next() is false
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getUserByUserID_throwsRuntimeException_whenInitialUserQueryFails() throws SQLException {
        // Given
        String userId = "userQueryFail1";
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";
        String sqlExceptionMessage = "Initial User query failed";

        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        // Simulate SQLException during executeQuery for the User table
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException(sqlExceptionMessage));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userDAO.getUserByUserID(userId);
        });
        assertEquals("Error getting user by ID", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof SQLException);
        assertEquals(sqlExceptionMessage, exception.getCause().getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockConnection, never()).commit(); // Commit not reached
        verify(mockPreparedStatement).close();
        verify(mockResultSet, never()).close(); // ResultSet not opened
        verify(mockConnection).close(); // Connection closed in finally block of SUT
    }

    @Test
    void getUserByUserID_throwsRuntimeException_whenGetCustomerByUserIdThrowsSQLException() throws SQLException {
        // Given
        String userId = "customerSubQueryFail";
        String passwordFromUserTable = "custPassFail";
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";
        String sqlExceptionMessage = "getCustomerByUserId SQLException";

        UserDAO spiedUserDAO = spy(userDAO);

        // 1. Mock initial query to User table - success
        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("role")).thenReturn("customer");
        when(mockResultSet.getString("password")).thenReturn(passwordFromUserTable);

        // 2. Stub spied getCustomerByUserId to throw SQLException
        doThrow(new SQLException(sqlExceptionMessage)).when(spiedUserDAO).getCustomerByUserId(userId, passwordFromUserTable);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spiedUserDAO.getUserByUserID(userId);
        });
        assertEquals("Error getting user by ID", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof SQLException);
        assertEquals(sqlExceptionMessage, exception.getCause().getMessage());

        verify(spiedUserDAO).getCustomerByUserId(userId, passwordFromUserTable);
        // Commit in the outer method is NOT called if the inner call throws an exception
        // that is caught and re-thrown as RuntimeException.
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close(); // From initial User query
        verify(mockResultSet).close();       // From initial User query
        verify(mockConnection).close();      // From initial User query try-with-resources
    }

    @Test
    void getUserByUserID_throwsRuntimeException_whenGetDoctorByUserIdThrowsSQLException() throws SQLException {
        // Given
        String userId = "doctorSubQueryFail";
        String passwordFromUserTable = "docPassFail";
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";
        String sqlExceptionMessage = "getDoctorByUserId SQLException";

        UserDAO spiedUserDAO = spy(userDAO);

        // 1. Mock initial query to User table - success
        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("role")).thenReturn("doctor");
        when(mockResultSet.getString("password")).thenReturn(passwordFromUserTable);

        // 2. Stub spied getDoctorByUserId to throw SQLException
        doThrow(new SQLException(sqlExceptionMessage)).when(spiedUserDAO).getDoctorByUserId(userId, passwordFromUserTable);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spiedUserDAO.getUserByUserID(userId);
        });
        assertEquals("Error getting user by ID", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof SQLException);
        assertEquals(sqlExceptionMessage, exception.getCause().getMessage());

        verify(spiedUserDAO).getDoctorByUserId(userId, passwordFromUserTable);
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close(); // From initial User query
        verify(mockResultSet).close();       // From initial User query
        verify(mockConnection).close();      // From initial User query try-with-resources
    }

    @Test
    void getUserByUserID_throwsRuntimeException_whenCommitFailsAfterUserNotFound() throws SQLException {
        // Given
        String userId = "userCommitFail1";
        String userTableSql = "SELECT * FROM User WHERE user_id = ?";
        String commitExceptionMessage = "Commit failed in getUserByUserID after user not found";

        when(mockConnection.prepareStatement(eq(userTableSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // User not found in User table

        // Simulate commit failure
        doThrow(new SQLException(commitExceptionMessage)).when(mockConnection).commit();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userDAO.getUserByUserID(userId);
        });
        assertEquals("Error getting user by ID", exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof SQLException);
        assertEquals(commitExceptionMessage, exception.getCause().getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection).commit(); // Commit was attempted
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close(); // Connection closed in finally block of SUT
    }

    // Test for outer commit failing after successful delegation was removed as it's not reachable in SUT.
    // The commit within getUserByUserID is only reached if user not found or role unknown.
}