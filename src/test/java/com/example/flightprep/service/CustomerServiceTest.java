package com.example.flightprep.service;

import com.example.flightprep.dao.CustomerDAO;
import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.model.Customer;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.RiskClassifierAI;
import com.example.flightprep.util.SessionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Apply lenient stubbing globally
class CustomerServiceTest {

    private static MockedStatic<SessionManager> mockedSessionManager;
    private static MockedStatic<RiskClassifierAI> mockedRiskClassifierAI;
    private static MockedStatic<AppointmentService> mockedAppointmentServiceStatic;

    // These will be the mocks injected into CustomerService instance
    @Mock private CustomerDAO mockCustomerDAO;
    @Mock private UserDAO mockUserDAO;
    @Mock private MedicalDataDAO mockMedicalDataDAO;
    @Mock private FileUploadService mockFileUploadService;
    @Mock private AppointmentService mockAppointmentServiceInstance; // Returned by AppointmentService.getInstance()

    private CustomerService customerService; // SUT

    // To hold mocked constructions of DAOs if needed, per test
    private MockedConstruction<CustomerDAO> customerDAOMockedConstruction;
    private MockedConstruction<UserDAO> userDAOMockedConstruction;
    private MockedConstruction<MedicalDataDAO> medicalDataDAOMockedConstruction;
    private MockedConstruction<FileUploadService> fileUploadServiceMockedConstruction;

    @BeforeAll
    static void setUpAllStatics() {
        mockedSessionManager = Mockito.mockStatic(SessionManager.class);
        mockedRiskClassifierAI = Mockito.mockStatic(RiskClassifierAI.class);
        mockedAppointmentServiceStatic = Mockito.mockStatic(AppointmentService.class);

        // Default static stubs
        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn("defaultStaticUser");
        mockedRiskClassifierAI.when(() -> RiskClassifierAI.classifyRisk(any(MedicalData.class))).thenReturn(1);
    }

    @AfterAll
    static void tearDownAllStatics() {
        mockedSessionManager.close();
        mockedRiskClassifierAI.close();
        mockedAppointmentServiceStatic.close();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Resetting the CustomerService Singleton instance to null to ensure a fresh one for each test
        Field instanceField = CustomerService.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Clear invocations for static mocks before each test
        if (mockedSessionManager != null) mockedSessionManager.clearInvocations();
        if (mockedRiskClassifierAI != null) mockedRiskClassifierAI.clearInvocations();
        // mockedAppointmentServiceStatic is typically verified for getInstance(), not direct calls in these SUT methods

        // Configure static mock for AppointmentService.getInstance() to return our mock
        // This is crucial because CustomerService calls AppointmentService.getInstance() internally.
        mockedAppointmentServiceStatic.when(AppointmentService::getInstance).thenReturn(mockAppointmentServiceInstance);

        // --- Mock DAO and FileUploadService constructions ---
        // When CustomerService is instantiated (via getInstance), its constructor will create DAO instances.
        // We intercept these constructions to provide our mocks instead.
        customerDAOMockedConstruction = Mockito.mockConstruction(CustomerDAO.class,
                (mock, context) -> this.mockCustomerDAO = mock); // Assign the created mock to our field
        userDAOMockedConstruction = Mockito.mockConstruction(UserDAO.class,
                (mock, context) -> this.mockUserDAO = mock);
        medicalDataDAOMockedConstruction = Mockito.mockConstruction(MedicalDataDAO.class,
                (mock, context) -> this.mockMedicalDataDAO = mock);
        fileUploadServiceMockedConstruction = Mockito.mockConstruction(FileUploadService.class,
                (mock, context) -> this.mockFileUploadService = mock);

        // Instantiate SUT: this will now use the mocked constructions for its DAOs
        customerService = CustomerService.getInstance();
    }

    @AfterEach
    void tearDown() {
        // Close mocked constructions after each test
        if (customerDAOMockedConstruction != null) customerDAOMockedConstruction.close();
        if (userDAOMockedConstruction != null) userDAOMockedConstruction.close();
        if (medicalDataDAOMockedConstruction != null) medicalDataDAOMockedConstruction.close();
        if (fileUploadServiceMockedConstruction != null) fileUploadServiceMockedConstruction.close();
    }

    @Test
    void getInstance_returnsNonNullInstance() {
        assertNotNull(customerService);
        CustomerService instance1 = CustomerService.getInstance();
        CustomerService instance2 = CustomerService.getInstance();
        assertSame(instance1, instance2, "getInstance should return the same instance (Singleton)");
    }

    @Test
    void getCustomerWithUploadedFiles_returnsListOfCustomers_whenDaoReturnsCustomers() throws SQLException {
        List<Customer> expectedCustomers = new ArrayList<>();
        expectedCustomers.add(new Customer("dummyUser", "pwd", "Dummy", "User", "dummy@mail.com", false, false, false, null, 0));
        when(mockCustomerDAO.findAllWithUploadedFiles()).thenReturn(expectedCustomers);

        List<Customer> actualCustomers = customerService.getCustomerWithUploadedFiles();

        assertSame(expectedCustomers, actualCustomers);
        verify(mockCustomerDAO).findAllWithUploadedFiles();
    }

    @Test
    void getCustomerWithUploadedFiles_returnsEmptyList_whenDaoReturnsEmptyList() throws SQLException {
        List<Customer> expectedCustomers = Collections.emptyList();
        when(mockCustomerDAO.findAllWithUploadedFiles()).thenReturn(expectedCustomers);

        List<Customer> actualCustomers = customerService.getCustomerWithUploadedFiles();

        assertTrue(actualCustomers.isEmpty());
        assertSame(expectedCustomers, actualCustomers); // Ensure it's the same empty list instance if DAO provides it
        verify(mockCustomerDAO).findAllWithUploadedFiles();
    }

    @Test
    void getCustomerWithUploadedFiles_throwsSQLException_whenDaoThrowsException() throws SQLException {
        when(mockCustomerDAO.findAllWithUploadedFiles()).thenThrow(new SQLException("DAO error"));

        assertThrows(SQLException.class, () -> customerService.getCustomerWithUploadedFiles());
        verify(mockCustomerDAO).findAllWithUploadedFiles();
    }

    // Tests for getCustomerStatus
    @Test
    void getCustomerStatus_returnsCustomer_whenDaoReturnsCustomer() throws SQLException {
        String userId = "testUser";
        Customer expectedCustomer = new Customer(userId, "pwd", "Test", "User", "test@mail.com", false, false, false, null, 0);
        when(mockUserDAO.getCustomerByUserId(userId, null)).thenReturn(expectedCustomer);

        Customer actualCustomer = customerService.getCustomerStatus(userId);

        assertSame(expectedCustomer, actualCustomer);
        verify(mockUserDAO).getCustomerByUserId(userId, null);
    }

    @Test
    void getCustomerStatus_throwsSQLException_whenDaoReturnsNull() throws SQLException {
        String userId = "nonExistentUser";
        when(mockUserDAO.getCustomerByUserId(userId, null)).thenReturn(null);

        SQLException exception = assertThrows(SQLException.class, () -> customerService.getCustomerStatus(userId));
        assertEquals("Customer not found", exception.getMessage());
        verify(mockUserDAO).getCustomerByUserId(userId, null);
    }

    @Test
    void getCustomerStatus_throwsSQLException_whenDaoThrowsException() throws SQLException {
        String userId = "testUser";
        when(mockUserDAO.getCustomerByUserId(userId, null)).thenThrow(new SQLException("DAO error"));

        SQLException exception = assertThrows(SQLException.class, () -> customerService.getCustomerStatus(userId));
        assertEquals("DAO error", exception.getMessage()); // Ensure it's the original DAO exception
        verify(mockUserDAO).getCustomerByUserId(userId, null);
    }

    // Tests for submitMedicalData
    @Test
    void submitMedicalData_successfullySavesDataAndUpdatesStatus() throws SQLException {
        String userId = "testUser123";
        MedicalData medicalData = new MedicalData(); // Populate if needed for RiskClassifierAI
        int expectedRiskGroup = 2;

        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
        mockedRiskClassifierAI.when(() -> RiskClassifierAI.classifyRisk(medicalData)).thenReturn(expectedRiskGroup);

        customerService.submitMedicalData(medicalData);

        verify(mockMedicalDataDAO).save(medicalData);
        verify(mockCustomerDAO).updateCustomerRiskGroup(userId, expectedRiskGroup);
        verify(mockCustomerDAO).updateFormSubmittedStatus(userId, true);
        mockedSessionManager.verify(SessionManager::getCurrentUserId, times(1));
        mockedRiskClassifierAI.verify(() -> RiskClassifierAI.classifyRisk(medicalData), times(1));
    }

    @Test
    void submitMedicalData_throwsSQLException_whenMedicalDataDaoSaveFails() throws SQLException {
        String userId = "testUser";
        MedicalData medicalData = new MedicalData();
        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
        doThrow(new SQLException("MedicalDAO save error")).when(mockMedicalDataDAO).save(medicalData);

        SQLException exception = assertThrows(SQLException.class, () -> customerService.submitMedicalData(medicalData));
        assertEquals("MedicalDAO save error", exception.getMessage());

        verify(mockMedicalDataDAO).save(medicalData);
        verify(mockCustomerDAO, never()).updateCustomerRiskGroup(anyString(), anyInt());
        verify(mockCustomerDAO, never()).updateFormSubmittedStatus(anyString(), anyBoolean());
    }

    @Test
    void submitMedicalData_throwsSQLException_whenUpdateCustomerRiskGroupFails() throws SQLException {
        String userId = "testUser";
        MedicalData medicalData = new MedicalData();
        int expectedRiskGroup = 1;

        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
        mockedRiskClassifierAI.when(() -> RiskClassifierAI.classifyRisk(medicalData)).thenReturn(expectedRiskGroup);
        doThrow(new SQLException("Update risk group error")).when(mockCustomerDAO).updateCustomerRiskGroup(userId, expectedRiskGroup);

        SQLException exception = assertThrows(SQLException.class, () -> customerService.submitMedicalData(medicalData));
        assertEquals("Update risk group error", exception.getMessage());

        verify(mockMedicalDataDAO).save(medicalData);
        verify(mockCustomerDAO).updateCustomerRiskGroup(userId, expectedRiskGroup);
        verify(mockCustomerDAO, never()).updateFormSubmittedStatus(anyString(), anyBoolean());
    }

    @Test
    void submitMedicalData_throwsSQLException_whenUpdateFormSubmittedStatusFails() throws SQLException {
        String userId = "testUser";
        MedicalData medicalData = new MedicalData();
        int expectedRiskGroup = 3;

        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(userId);
        mockedRiskClassifierAI.when(() -> RiskClassifierAI.classifyRisk(medicalData)).thenReturn(expectedRiskGroup);
        doThrow(new SQLException("Update form status error")).when(mockCustomerDAO).updateFormSubmittedStatus(userId, true);

        SQLException exception = assertThrows(SQLException.class, () -> customerService.submitMedicalData(medicalData));
        assertEquals("Update form status error", exception.getMessage());

        verify(mockMedicalDataDAO).save(medicalData);
        verify(mockCustomerDAO).updateCustomerRiskGroup(userId, expectedRiskGroup);
        verify(mockCustomerDAO).updateFormSubmittedStatus(userId, true);
    }

    @Test
    void submitMedicalData_usesCorrectUserIdFromSessionManager() throws SQLException {
        String specificUserId = "userFromSession";
        MedicalData medicalData = new MedicalData();
        int riskGroup = 1;

        mockedSessionManager.when(SessionManager::getCurrentUserId).thenReturn(specificUserId);
        mockedRiskClassifierAI.when(() -> RiskClassifierAI.classifyRisk(medicalData)).thenReturn(riskGroup);

        customerService.submitMedicalData(medicalData);

        mockedSessionManager.verify(SessionManager::getCurrentUserId, times(1));
        verify(mockMedicalDataDAO).save(medicalData); // MedicalData doesn't use userId directly here
        verify(mockCustomerDAO).updateCustomerRiskGroup(eq(specificUserId), eq(riskGroup));
        verify(mockCustomerDAO).updateFormSubmittedStatus(eq(specificUserId), eq(true));
        mockedRiskClassifierAI.verify(() -> RiskClassifierAI.classifyRisk(medicalData), times(1));
    }

    // Tests for getMedicalData
    @Test
    void getMedicalData_returnsMedicalData_whenDaoReturnsData() throws SQLException {
        String patientId = "patient1";
        MedicalData expectedData = new MedicalData();
        when(mockMedicalDataDAO.getDataByUserId(patientId)).thenReturn(expectedData);

        MedicalData actualData = customerService.getMedicalData(patientId);

        assertSame(expectedData, actualData);
        verify(mockMedicalDataDAO).getDataByUserId(patientId);
    }

    @Test
    void getMedicalData_returnsNull_whenDaoReturnsNull() throws SQLException {
        String patientId = "patientNonExistent";
        when(mockMedicalDataDAO.getDataByUserId(patientId)).thenReturn(null);

        MedicalData actualData = customerService.getMedicalData(patientId);

        assertNull(actualData);
        verify(mockMedicalDataDAO).getDataByUserId(patientId);
    }

    @Test
    void getMedicalData_throwsSQLException_whenDaoThrowsException() throws SQLException {
        String patientId = "patientError";
        when(mockMedicalDataDAO.getDataByUserId(patientId)).thenThrow(new SQLException("DAO error fetching medical data"));

        SQLException exception = assertThrows(SQLException.class, () -> customerService.getMedicalData(patientId));
        assertEquals("DAO error fetching medical data", exception.getMessage());
        verify(mockMedicalDataDAO).getDataByUserId(patientId);
    }

    // Tests for saveDeclaration
    @Test
    void saveDeclaration_callsDaoWithCorrectParameters() throws SQLException {
        String userId = "userToDeclare";
        boolean isApproved = true;
        String comment = "Declaration approved.";

        customerService.saveDeclaration(userId, isApproved, comment);

        verify(mockCustomerDAO).saveDeclaration(userId, isApproved, comment);
    }

    @Test
    void saveDeclaration_throwsSQLException_whenDaoThrowsException() throws SQLException {
        String userId = "userToDeclareError";
        boolean isApproved = false;
        String comment = "Error during declaration.";
        doThrow(new SQLException("DAO error saving declaration")).when(mockCustomerDAO).saveDeclaration(userId, isApproved, comment);

        SQLException exception = assertThrows(SQLException.class, () -> customerService.saveDeclaration(userId, isApproved, comment));
        assertEquals("DAO error saving declaration", exception.getMessage());
        verify(mockCustomerDAO).saveDeclaration(userId, isApproved, comment);
    }

    // Tests for getPatientDocuments
    // Note: Direct testing of java.io.File operations like listFiles() within this unit test is complex
    // without PowerMock or refactoring. These tests focus on the interaction with FileUploadService
    // and the service's error handling.

    @Test
    void getPatientDocuments_callsCreateDirectoriesAndReturnsEmptyListByDefault() throws IOException {
        // Assuming UPLOAD_DIR is initially empty or listFiles returns null/empty array
        // This test primarily verifies createDirectories is called and basic flow.
        doNothing().when(mockFileUploadService).createDirectories();

        List<String> documents = customerService.getPatientDocuments();

        assertNotNull(documents);
        verify(mockFileUploadService).createDirectories();
    }

    @Test
    void getPatientDocuments_throwsRuntimeException_whenCreateDirectoriesThrowsIOException() throws IOException {
        IOException ioException = new IOException("Failed to create directories");
        doThrow(ioException).when(mockFileUploadService).createDirectories();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> customerService.getPatientDocuments());

        assertEquals("Error accessing documents", exception.getMessage());
        assertSame(ioException, exception.getCause());
        verify(mockFileUploadService).createDirectories();
    }

    // Tests for updateFileUploadStatus
    @Test
    void updateFileUploadStatus_callsDaoWithCorrectUserId() throws SQLException {
        String userId = "userToUpdateStatus";

        customerService.updateFileUploadStatus(userId);

        verify(mockCustomerDAO).updateFileUploadStatus(userId);
    }

    @Test
    void updateFileUploadStatus_throwsSQLException_whenDaoThrowsException() throws SQLException {
        String userId = "userUpdateStatusError";
        doThrow(new SQLException("DAO error updating file upload status")).when(mockCustomerDAO).updateFileUploadStatus(userId);

        SQLException exception = assertThrows(SQLException.class, () -> customerService.updateFileUploadStatus(userId));
        assertEquals("DAO error updating file upload status", exception.getMessage());
        verify(mockCustomerDAO).updateFileUploadStatus(userId);
    }

    // Tests for getFlightDate
    @Test
    void getFlightDate_returnsDate_whenDaoReturnsDate() throws SQLException {
        String userId = "userWithFlightDate";
        LocalDate expectedDate = LocalDate.of(2024, 12, 25);
        when(mockCustomerDAO.getFlightDate(userId)).thenReturn(expectedDate);

        LocalDate actualDate = customerService.getFlightDate(userId);

        assertSame(expectedDate, actualDate);
        verify(mockCustomerDAO).getFlightDate(userId);
    }

    @Test
    void getFlightDate_returnsNull_whenDaoReturnsNull() throws SQLException {
        String userId = "userWithoutFlightDate";
        when(mockCustomerDAO.getFlightDate(userId)).thenReturn(null);

        LocalDate actualDate = customerService.getFlightDate(userId);

        assertNull(actualDate);
        verify(mockCustomerDAO).getFlightDate(userId);
    }

    @Test
    void getFlightDate_throwsSQLException_whenDaoThrowsException() throws SQLException {
        String userId = "userFlightDateError";
        when(mockCustomerDAO.getFlightDate(userId)).thenThrow(new SQLException("DAO error fetching flight date"));

        SQLException exception = assertThrows(SQLException.class, () -> customerService.getFlightDate(userId));
        assertEquals("DAO error fetching flight date", exception.getMessage());
        verify(mockCustomerDAO).getFlightDate(userId);
    }

    // Tests for updateFlightDateWithValidation
    private final String testUserId = "flightUser1";

    @Test
    void updateFlightDateWithValidation_throwsIllegalArgumentException_whenNewFlightDateIsNull() throws SQLException {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updateFlightDateWithValidation(testUserId, null));
        assertEquals("New flight date cannot be null.", exception.getMessage());
        verify(mockCustomerDAO, never()).updateFlightDate(anyString(), any(LocalDate.class));
    }

    @Test
    void updateFlightDateWithValidation_throwsIllegalArgumentException_whenNewFlightDateIsInPast() throws SQLException {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updateFlightDateWithValidation(testUserId, pastDate));
        assertEquals("Flight date cannot be in the past.", exception.getMessage());
        verify(mockCustomerDAO, never()).updateFlightDate(anyString(), any(LocalDate.class));
    }

    @Test
    void updateFlightDateWithValidation_updatesDate_whenNoExistingAppointment() throws SQLException {
        LocalDate validFutureDate = LocalDate.now().plusDays(40);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(null);

        customerService.updateFlightDateWithValidation(testUserId, validFutureDate);

        verify(mockAppointmentServiceInstance).getAppointmentByCustomerId(testUserId);
        verify(mockCustomerDAO).updateFlightDate(testUserId, validFutureDate);
    }

    @Test
    void updateFlightDateWithValidation_updatesDate_whenExistingAppointmentIsNullDate() throws SQLException {
        LocalDate validFutureDate = LocalDate.now().plusDays(40);
        Appointment appointmentWithNullDate = new Appointment(0, testUserId, "CustFirst", "CustLast", "doc1", null, null, 0);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(appointmentWithNullDate);

        customerService.updateFlightDateWithValidation(testUserId, validFutureDate);

        verify(mockAppointmentServiceInstance).getAppointmentByCustomerId(testUserId);
        verify(mockCustomerDAO).updateFlightDate(testUserId, validFutureDate);
    }

    @Test
    void updateFlightDateWithValidation_updatesDate_whenExistingAppointmentIsEmptyDate() throws SQLException {
        LocalDate validFutureDate = LocalDate.now().plusDays(40);
        Appointment appointmentWithEmptyDate = new Appointment(0, testUserId, "CustFirst", "CustLast", "doc1", "", null, 0);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(appointmentWithEmptyDate);

        customerService.updateFlightDateWithValidation(testUserId, validFutureDate);

        verify(mockAppointmentServiceInstance).getAppointmentByCustomerId(testUserId);
        verify(mockCustomerDAO).updateFlightDate(testUserId, validFutureDate);
    }

    @Test
    void updateFlightDateWithValidation_updatesDate_whenNewFlightDateIsMoreThan29DaysAfterAppointment() throws SQLException {
        LocalDate appointmentDate = LocalDate.now().plusDays(10);
        LocalDate newFlightDate = appointmentDate.plusDays(30);

        Appointment appointment = new Appointment(1, testUserId, "CustFirst", "CustLast", "doc1", appointmentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")), "10:00", 0);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(appointment);

        customerService.updateFlightDateWithValidation(testUserId, newFlightDate);

        verify(mockAppointmentServiceInstance).getAppointmentByCustomerId(testUserId);
        verify(mockCustomerDAO).updateFlightDate(testUserId, newFlightDate);
    }

    @Test
    void updateFlightDateWithValidation_throwsIllegalArgumentException_whenNewFlightDateIsExactly29DaysAfterAppointment() throws SQLException {
        LocalDate appointmentDate = LocalDate.now().plusDays(10);
        LocalDate newFlightDate = appointmentDate.plusDays(29);
        String appointmentDateStr = appointmentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        Appointment appointment = new Appointment(2, testUserId, "CustFirst", "CustLast", "doc1", appointmentDateStr, "11:00", 0);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(appointment);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updateFlightDateWithValidation(testUserId, newFlightDate));
        assertEquals("Flight date must be at least 30 days after your medical appointment date (" + appointmentDateStr + ").", exception.getMessage());
        verify(mockAppointmentServiceInstance).getAppointmentByCustomerId(testUserId);
        verify(mockCustomerDAO, never()).updateFlightDate(anyString(), any(LocalDate.class));
    }

    @Test
    void updateFlightDateWithValidation_throwsIllegalArgumentException_whenNewFlightDateIsLessThan29DaysAfterAppointment() throws SQLException {
        LocalDate appointmentDate = LocalDate.now().plusDays(10);
        LocalDate newFlightDate = appointmentDate.plusDays(15);
        String appointmentDateStr = appointmentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        Appointment appointment = new Appointment(3, testUserId, "CustFirst", "CustLast", "doc1", appointmentDateStr, "12:00", 0);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(appointment);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.updateFlightDateWithValidation(testUserId, newFlightDate));
        assertEquals("Flight date must be at least 30 days after your medical appointment date (" + appointmentDateStr + ").", exception.getMessage());
        verify(mockAppointmentServiceInstance).getAppointmentByCustomerId(testUserId);
        verify(mockCustomerDAO, never()).updateFlightDate(anyString(), any(LocalDate.class));
    }

    @Test
    void updateFlightDateWithValidation_throwsDateTimeParseException_whenAppointmentDateFormatIsInvalid() throws SQLException {
        LocalDate validFutureDate = LocalDate.now().plusDays(40);
        Appointment appointmentWithInvalidDate = new Appointment(4, testUserId, "CustFirst", "CustLast", "doc1", "2023-01-01", "13:00", 0);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(appointmentWithInvalidDate);

        // The SUT catches DateTimeParseException and would proceed as if date is not parseable,
        // so it would allow update if other conditions met or throw its own error based on logic path
        // For this specific test, if date parsing fails, it behaves like no valid appointment date was found.
        // This means the check `!newFlightDate.isAfter(appointmentDate.plusDays(29))` is skipped.
        // Let's verify that it attempts to update if this parsing fails and newFlightDate is otherwise valid.

        assertThrows(java.time.format.DateTimeParseException.class, () -> {
            customerService.updateFlightDateWithValidation(testUserId, validFutureDate);
        });

        // As the SUT does not catch and re-throw DateTimeParseException, we should not verify updateFlightDate here.
        // The SUT will fail internally. This test highlights a potential unhandled exception type if format is wrong.
        verify(mockAppointmentServiceInstance).getAppointmentByCustomerId(testUserId);
        verify(mockCustomerDAO, never()).updateFlightDate(anyString(), any(LocalDate.class));
    }

    @Test
    void updateFlightDateWithValidation_callsDaoUpdateFlightDate_onSuccessfulValidation() throws SQLException {
        LocalDate validFutureDate = LocalDate.now().plusDays(60);
        // No appointment or appointment with date far in the past, so validation should pass
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(null);

        customerService.updateFlightDateWithValidation(testUserId, validFutureDate);

        verify(mockCustomerDAO).updateFlightDate(testUserId, validFutureDate);
    }

    @Test
    void updateFlightDateWithValidation_throwsSQLException_whenGetAppointmentByCustomerIdFails() throws SQLException {
        LocalDate validFutureDate = LocalDate.now().plusDays(40);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenThrow(new SQLException("Get_Appointment_DAO error"));

        SQLException exception = assertThrows(SQLException.class,
                () -> customerService.updateFlightDateWithValidation(testUserId, validFutureDate));
        assertEquals("Get_Appointment_DAO error", exception.getMessage());
        verify(mockCustomerDAO, never()).updateFlightDate(anyString(), any(LocalDate.class));
    }

    @Test
    void updateFlightDateWithValidation_throwsSQLException_whenDaoUpdateFlightDateFails() throws SQLException {
        LocalDate validFutureDate = LocalDate.now().plusDays(40);
        when(mockAppointmentServiceInstance.getAppointmentByCustomerId(testUserId)).thenReturn(null); // No appointment, so this check passes
        doThrow(new SQLException("Update_FlightDate_DAO error")).when(mockCustomerDAO).updateFlightDate(testUserId, validFutureDate);

        SQLException exception = assertThrows(SQLException.class,
                () -> customerService.updateFlightDateWithValidation(testUserId, validFutureDate));
        assertEquals("Update_FlightDate_DAO error", exception.getMessage());
        verify(mockCustomerDAO).updateFlightDate(testUserId, validFutureDate); // Still called
    }

    // Further test methods will be added here
}