package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.SessionManager;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalDataDAOTest {

    private static MockedStatic<DatabaseFactory> mockedDatabaseFactory;
    private static MockedStatic<SessionManager> mockedSessionManager;

    @Mock
    private DatabaseConnection mockDatabaseConnection;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private MedicalData mockMedicalData; // Used as input for save, and for verifying map method

    private MedicalDataDAO medicalDataDAO; // SUT

    @BeforeAll
    static void setUpAll() {
        mockedDatabaseFactory = Mockito.mockStatic(DatabaseFactory.class);
        mockedSessionManager = Mockito.mockStatic(SessionManager.class);
    }

    @AfterAll
    static void tearDownAll() {
        if (mockedDatabaseFactory != null && !mockedDatabaseFactory.isClosed()) {
            mockedDatabaseFactory.close();
        }
        if (mockedSessionManager != null && !mockedSessionManager.isClosed()) {
            mockedSessionManager.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Reset mocks before each test to ensure test isolation
        reset(mockDatabaseConnection, mockConnection, mockPreparedStatement, mockResultSet, mockMedicalData);

        // Static mock for DatabaseFactory must be configured before DAO instantiation
        mockedDatabaseFactory.when(DatabaseFactory::getDatabase).thenReturn(mockDatabaseConnection);

        // Instantiate DAO AFTER the static mock for DatabaseFactory is in place
        medicalDataDAO = new MedicalDataDAO();

        // Default stub for DatabaseConnection to return the mock SQL Connection
        lenient().when(mockDatabaseConnection.getConnection()).thenReturn(mockConnection);

        // Default stub for Connection.prepareStatement to return the mock PreparedStatement
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Default stub for SessionManager.getCurrentUserId() - can be overridden in specific tests
        lenient().when(SessionManager.getCurrentUserId()).thenReturn("defaultTestUser");
    }

    @Test
    void placeholderTest_ensureClassIsTested() {
        assertTrue(true, "Placeholder test to ensure MedicalDataDAOTest is picked up by the test runner.");
    }

    // --- Tests for save(MedicalData data) ---
    @Test
    void save_success() throws SQLException {
        // Given
        String currentUserId = "testUser123";
        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(currentUserId);

        // Mock all getters for MedicalData input
        when(mockMedicalData.getHeight()).thenReturn("180");
        when(mockMedicalData.getWeight()).thenReturn("75");
        when(mockMedicalData.getAlcoholConsumption()).thenReturn("Occasionally");
        when(mockMedicalData.getSmokingStatus()).thenReturn("Never");
        when(mockMedicalData.isTrainingStatus()).thenReturn(true);
        when(mockMedicalData.getDisabilityStatus()).thenReturn(false);
        when(mockMedicalData.getDisabilityDetails()).thenReturn(""); // Important for setString for nulls
        when(mockMedicalData.isHeartDisease()).thenReturn(false);
        when(mockMedicalData.isHighBloodPressure()).thenReturn(false);
        when(mockMedicalData.isIrregularHeartbeat()).thenReturn(false);
        when(mockMedicalData.isStrokeHistory()).thenReturn(false);
        when(mockMedicalData.isAsthma()).thenReturn(false);
        when(mockMedicalData.isLungDisease()).thenReturn(false);
        when(mockMedicalData.isSeizureHistory()).thenReturn(false);
        when(mockMedicalData.isNeurologicalDisorder()).thenReturn(false);
        when(mockMedicalData.isHsp_respiratory_cardio()).thenReturn(false);
        when(mockMedicalData.isHsp_heart_lung()).thenReturn(false);
        when(mockMedicalData.isPersc_med()).thenReturn(false);
        when(mockMedicalData.isAllergies()).thenReturn(false);
        when(mockMedicalData.isSurgery()).thenReturn(false);
        when(mockMedicalData.isSer_injury()).thenReturn(false);

        String expectedSql = "INSERT or REPLACE INTO medical_data (user_id, height, weight, alcohol_consumption, " +
                "smoking_status, training_status, disability_status, disability_details, heart_disease, " +
                "high_blood_pressure, irregular_heartbeat, stroke_history, asthma, lung_disease, " +
                "seizure_history, neurological_disorder, hsp_respiratory_cardio, hsp_heart_lung, " +
                "persc_med, allergies, surgery, ser_injury) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate one row affected

        // When
        assertDoesNotThrow(() -> medicalDataDAO.save(mockMedicalData));

        // Then
        verify(mockPreparedStatement).setString(1, currentUserId);
        verify(mockPreparedStatement).setString(2, "180");
        verify(mockPreparedStatement).setString(3, "75");
        verify(mockPreparedStatement).setString(4, "Occasionally");
        verify(mockPreparedStatement).setString(5, "Never");
        verify(mockPreparedStatement).setBoolean(6, true);
        verify(mockPreparedStatement).setBoolean(7, false);
        verify(mockPreparedStatement).setString(8, "");
        verify(mockPreparedStatement).setBoolean(9, false);
        verify(mockPreparedStatement).setBoolean(10, false);
        verify(mockPreparedStatement).setBoolean(11, false);
        verify(mockPreparedStatement).setBoolean(12, false);
        verify(mockPreparedStatement).setBoolean(13, false);
        verify(mockPreparedStatement).setBoolean(14, false);
        verify(mockPreparedStatement).setBoolean(15, false);
        verify(mockPreparedStatement).setBoolean(16, false);
        verify(mockPreparedStatement).setBoolean(17, false);
        verify(mockPreparedStatement).setBoolean(18, false);
        verify(mockPreparedStatement).setBoolean(19, false);
        verify(mockPreparedStatement).setBoolean(20, false);
        verify(mockPreparedStatement).setBoolean(21, false);
        verify(mockPreparedStatement).setBoolean(22, false);

        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void save_throwsSQLException_whenExecuteUpdateReturnsZero() throws SQLException {
        // Given
        String currentUserId = "testUserFailSave";
        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(currentUserId);

        // Mock getters - values don't strictly matter for this test path but need to be there
        // to avoid NPEs if the SUT tries to access them before executeUpdate.
        when(mockMedicalData.getHeight()).thenReturn("-");
        // ... (minimal mocking for other fields, can be lenient if not strictly needed before executeUpdate)
        lenient().when(mockMedicalData.getWeight()).thenReturn("-");
        lenient().when(mockMedicalData.getAlcoholConsumption()).thenReturn("-");
        lenient().when(mockMedicalData.getSmokingStatus()).thenReturn("-");
        lenient().when(mockMedicalData.getDisabilityDetails()).thenReturn(null);
        // Add more lenient stubs if needed for other String getters if they might return null
        // and cause issues with PreparedStatement.setString if not handled by SUT before setString.

        String expectedSql = "INSERT or REPLACE INTO medical_data (user_id, height, weight, alcohol_consumption, " +
                "smoking_status, training_status, disability_status, disability_details, heart_disease, " +
                "high_blood_pressure, irregular_heartbeat, stroke_history, asthma, lung_disease, " +
                "seizure_history, neurological_disorder, hsp_respiratory_cardio, hsp_heart_lung, " +
                "persc_med, allergies, surgery, ser_injury) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simulate zero rows affected

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.save(mockMedicalData);
        });
        assertEquals("Saving medical data failed", exception.getMessage());

        // Verify setters were called on PreparedStatement up to the point of executeUpdate
        verify(mockPreparedStatement).setString(1, currentUserId);
        // ... (verify other setters if necessary, or ensure SUT robustness to nulls from getters if not mocked)

        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit(); // Commit should not be called
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void save_throwsSQLException_whenPrepareStatementFails() throws SQLException {
        // Given
        // mockMedicalData getters don't need to be called if prepareStatement fails early.
        // SessionManager.getCurrentUserId() also might not be called if prepareStatement is the first DB interaction point that fails.
        // However, SUT calls getCurrentUserId before prepareStatement, so it should be mocked.
        String currentUserId = "userPrepStmtFail";
        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(currentUserId);

        String expectedSql = "INSERT or REPLACE INTO medical_data (user_id, height, weight, alcohol_consumption, " +
                "smoking_status, training_status, disability_status, disability_details, heart_disease, " +
                "high_blood_pressure, irregular_heartbeat, stroke_history, asthma, lung_disease, " +
                "seizure_history, neurological_disorder, hsp_respiratory_cardio, hsp_heart_lung, " +
                "persc_med, allergies, surgery, ser_injury) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String exceptionMessage = "PrepareStatement failed";
        when(mockConnection.prepareStatement(eq(expectedSql))).thenThrow(new SQLException(exceptionMessage));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.save(mockMedicalData);
        });
        assertEquals(exceptionMessage, exception.getMessage());

        verify(mockConnection).prepareStatement(eq(expectedSql));
        verify(mockPreparedStatement, never()).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement, never()).close(); // PreparedStatement not successfully created
        verify(mockConnection).close(); // Connection should still be closed by try-with-resources
    }

    @Test
    void save_throwsSQLException_whenExecuteUpdateFailsGeneric() throws SQLException {
        // Given
        String currentUserId = "testUserExecUpdateFail";
        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(currentUserId);
        // Minimal mocking for mockMedicalData, similar to the 'returns zero' case
        when(mockMedicalData.getHeight()).thenReturn("-");
        lenient().when(mockMedicalData.getDisabilityDetails()).thenReturn(null);

        String expectedSql = "INSERT or REPLACE INTO medical_data (user_id, height, weight, alcohol_consumption, " +
                "smoking_status, training_status, disability_status, disability_details, heart_disease, " +
                "high_blood_pressure, irregular_heartbeat, stroke_history, asthma, lung_disease, " +
                "seizure_history, neurological_disorder, hsp_respiratory_cardio, hsp_heart_lung, " +
                "persc_med, allergies, surgery, ser_injury) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String exceptionMessage = "ExecuteUpdate generic failure";
        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException(exceptionMessage));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.save(mockMedicalData);
        });
        assertEquals(exceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void save_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String currentUserId = "testUserCommitFail";
        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(currentUserId);
        // Mock MedicalData getters as in success case, as all setters will be called before commit
        when(mockMedicalData.getHeight()).thenReturn("170");
        when(mockMedicalData.getWeight()).thenReturn("70");
        when(mockMedicalData.getAlcoholConsumption()).thenReturn("None");
        when(mockMedicalData.getSmokingStatus()).thenReturn("Quit");
        when(mockMedicalData.isTrainingStatus()).thenReturn(false);
        when(mockMedicalData.getDisabilityStatus()).thenReturn(true);
        when(mockMedicalData.getDisabilityDetails()).thenReturn("Minor back issue");
        when(mockMedicalData.isHeartDisease()).thenReturn(true);
        // ... (mock other boolean getters as needed or ensure they default to false in mock if not set)

        String expectedSql = "INSERT or REPLACE INTO medical_data (user_id, height, weight, alcohol_consumption, " +
                "smoking_status, training_status, disability_status, disability_details, heart_disease, " +
                "high_blood_pressure, irregular_heartbeat, stroke_history, asthma, lung_disease, " +
                "seizure_history, neurological_disorder, hsp_respiratory_cardio, hsp_heart_lung, " +
                "persc_med, allergies, surgery, ser_injury) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // executeUpdate succeeds

        String commitExceptionMessage = "Commit failed during save";
        doThrow(new SQLException(commitExceptionMessage)).when(mockConnection).commit();

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.save(mockMedicalData);
        });
        assertEquals(commitExceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).executeUpdate();
        verify(mockConnection).commit(); // Commit was attempted
        verify(mockPreparedStatement).close();
        verify(mockConnection).close(); // Connection still closed in finally block
    }

    // --- Tests for getDataByUserId(String userId) ---
    @Test
    void getDataByUserId_returnsMedicalData_whenFound() throws SQLException {
        // Given
        String userId = "userWithData";
        String expectedSql = "SELECT * FROM medical_data WHERE user_id = ?";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Data found

        // Mock ResultSet getters for all fields in mapResultSetToMedicalData
        when(mockResultSet.getString("height")).thenReturn("190cm");
        when(mockResultSet.getString("weight")).thenReturn("85kg");
        when(mockResultSet.getString("alcohol_consumption")).thenReturn("Rarely");
        when(mockResultSet.getString("smoking_status")).thenReturn("Non-smoker");
        when(mockResultSet.getBoolean("training_status")).thenReturn(true);
        when(mockResultSet.getBoolean("disability_status")).thenReturn(false);
        when(mockResultSet.getString("disability_details")).thenReturn(null); // Test null string handling
        when(mockResultSet.getBoolean("heart_disease")).thenReturn(true);
        when(mockResultSet.getBoolean("high_blood_pressure")).thenReturn(false);
        when(mockResultSet.getBoolean("irregular_heartbeat")).thenReturn(true);
        when(mockResultSet.getBoolean("stroke_history")).thenReturn(false);
        when(mockResultSet.getBoolean("asthma")).thenReturn(true);
        when(mockResultSet.getBoolean("lung_disease")).thenReturn(false);
        when(mockResultSet.getBoolean("seizure_history")).thenReturn(true);
        when(mockResultSet.getBoolean("neurological_disorder")).thenReturn(false);
        when(mockResultSet.getBoolean("hsp_respiratory_cardio")).thenReturn(true);
        when(mockResultSet.getBoolean("hsp_heart_lung")).thenReturn(false);
        when(mockResultSet.getBoolean("persc_med")).thenReturn(true);
        when(mockResultSet.getBoolean("allergies")).thenReturn(false);
        when(mockResultSet.getBoolean("surgery")).thenReturn(true);
        when(mockResultSet.getBoolean("ser_injury")).thenReturn(false);

        // When
        MedicalData resultData = medicalDataDAO.getDataByUserId(userId);

        // Then
        assertNotNull(resultData);
        assertEquals("190cm", resultData.getHeight());
        assertEquals("85kg", resultData.getWeight());
        assertEquals("Rarely", resultData.getAlcoholConsumption());
        assertEquals("Non-smoker", resultData.getSmokingStatus());
        assertTrue(resultData.isTrainingStatus());
        assertFalse(resultData.getDisabilityStatus());
        assertNull(resultData.getDisabilityDetails()); // Check if null was correctly set
        assertTrue(resultData.isHeartDisease());
        assertFalse(resultData.isHighBloodPressure());
        assertTrue(resultData.isIrregularHeartbeat());
        // ... (assert other fields)
        assertFalse(resultData.isSer_injury());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection).commit();
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close();
    }

    @Test
    void getDataByUserId_returnsNull_whenNotFound() throws SQLException {
        // Given
        String userId = "userWithoutData";
        String expectedSql = "SELECT * FROM medical_data WHERE user_id = ?";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Data not found

        // When
        MedicalData resultData = medicalDataDAO.getDataByUserId(userId);

        // Then
        assertNull(resultData);

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockConnection, never()).commit(); // Commit not called if data not found
        // mapResultSetToMedicalData is not called, so no need to verify its interactions with mockResultSet getters
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close(); // Outer connection still closed
    }

    @Test
    void getDataByUserId_throwsSQLException_whenPrepareStatementFails() throws SQLException {
        // Given
        String userId = "userGetDataPrepFail";
        String expectedSql = "SELECT * FROM medical_data WHERE user_id = ?";
        String exceptionMessage = "PrepareStatement failed for getDataByUserId";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenThrow(new SQLException(exceptionMessage));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.getDataByUserId(userId);
        });
        assertEquals(exceptionMessage, exception.getMessage());

        verify(mockConnection).prepareStatement(eq(expectedSql));
        verify(mockPreparedStatement, never()).setString(1, userId);
        verify(mockPreparedStatement, never()).executeQuery();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement, never()).close();
        verify(mockResultSet, never()).close();
        verify(mockConnection).close(); // Connection from outer try-with-resources should be closed
    }

    @Test
    void getDataByUserId_throwsSQLException_whenExecuteQueryFails() throws SQLException {
        // Given
        String userId = "userGetDataExecFail";
        String expectedSql = "SELECT * FROM medical_data WHERE user_id = ?";
        String exceptionMessage = "ExecuteQuery failed for getDataByUserId";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException(exceptionMessage));

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.getDataByUserId(userId);
        });
        assertEquals(exceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, never()).next();
        verify(mockConnection, never()).commit();
        verify(mockPreparedStatement).close(); // This is closed in the inner try-with-resources
        verify(mockResultSet, never()).close(); // ResultSet not opened
        verify(mockConnection).close(); // Outer connection closed
    }

    @Test
    void getDataByUserId_throwsSQLException_whenCommitFails() throws SQLException {
        // Given
        String userId = "userGetDataCommitFail";
        String expectedSql = "SELECT * FROM medical_data WHERE user_id = ?";
        String commitExceptionMessage = "Commit failed for getDataByUserId";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Data found

        // Minimal mocking for ResultSet getters, as mapResultSetToMedicalData will be called
        lenient().when(mockResultSet.getString(anyString())).thenReturn("someValue");
        lenient().when(mockResultSet.getBoolean(anyString())).thenReturn(false);

        doThrow(new SQLException(commitExceptionMessage)).when(mockConnection).commit();

        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.getDataByUserId(userId);
        });
        assertEquals(commitExceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        // mapResultSetToMedicalData would have been called, verify at least one interaction with rs
        verify(mockResultSet, atLeastOnce()).getString(anyString());
        verify(mockConnection).commit(); // Commit was attempted
        verify(mockPreparedStatement).close();
        verify(mockResultSet).close();
        verify(mockConnection).close(); // Outer connection closed
    }

    @Test
    void getDataByUserId_throwsSQLException_whenMappingResultFails() throws SQLException {
        // Given
        String userId = "userMapFail";
        String expectedSql = "SELECT * FROM medical_data WHERE user_id = ?";
        String mappingExceptionMessage = "ResultSet mapping failed";

        when(mockConnection.prepareStatement(eq(expectedSql))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Data found

        // Simulate SQLException during one of the rs.get... calls in mapResultSetToMedicalData
        // For example, when trying to get "height"
        when(mockResultSet.getString("height")).thenThrow(new SQLException(mappingExceptionMessage));
        // Other getters might be leniently stubbed if the SUT calls them before the failing one.
        lenient().when(mockResultSet.getBoolean(anyString())).thenReturn(false);


        // When & Then
        SQLException exception = assertThrows(SQLException.class, () -> {
            medicalDataDAO.getDataByUserId(userId);
        });
        assertEquals(mappingExceptionMessage, exception.getMessage());

        verify(mockPreparedStatement).setString(1, userId);
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
        verify(mockResultSet).getString("height"); // This call caused the exception
        verify(mockConnection, never()).commit(); // Commit not reached if mapping fails
        verify(mockPreparedStatement).close(); // Closed in inner try-with-resources of SUT
        verify(mockResultSet).close();       // Closed in inner try-with-resources of SUT
        verify(mockConnection).close();      // Outer connection closed
    }

}