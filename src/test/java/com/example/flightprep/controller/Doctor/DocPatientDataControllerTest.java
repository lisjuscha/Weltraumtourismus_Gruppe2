package com.example.flightprep.controller.Doctor;

import com.example.flightprep.model.MedicalData;
import com.example.flightprep.service.CustomerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.Label;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocPatientDataControllerTest {

    @Mock
    private CustomerService mockCustomerService;

    private DocPatientDataController docPatientDataController;

    private static MockedStatic<CustomerService> mockedCustomerServiceStatic;

    // Add Mocks for FXML Labels from PatientDataDisplayController
    @Mock private Label heightLabel;
    @Mock private Label weightLabel;
    @Mock private Label smokingLabel;
    @Mock private Label alcoholLabel;
    @Mock private Label trainingLabel;
    @Mock private Label disabilityLabel;
    @Mock private Label disabilityDetailsLabel;
    @Mock private Label heartDiseaseLabel;
    @Mock private Label bloodPressureLabel;
    @Mock private Label heartbeatLabel;
    @Mock private Label strokeLabel;
    @Mock private Label asthmaLabel;
    @Mock private Label lungDiseaseLabel;
    @Mock private Label seizureLabel;
    @Mock private Label neurologicalLabel;
    @Mock private Label hspRespiratoryCardioLabel;
    @Mock private Label hspHeartLungLabel;
    @Mock private Label medicationLabel;
    @Mock private Label allergiesLabel;
    @Mock private Label surgeryLabel;
    @Mock private Label injuryLabel;

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        mockedCustomerServiceStatic = Mockito.mockStatic(CustomerService.class);
        mockedCustomerServiceStatic.when(() -> CustomerService.getInstance()).thenReturn(mock(CustomerService.class, Mockito.RETURNS_DEEP_STUBS));

        if (!DocCalendarControllerTest.fxInitialized) { // Use shared flag
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(() -> {
                    latch.countDown();
                    DocCalendarControllerTest.fxInitialized = true; // Set shared flag
                });
                if (!latch.await(5, TimeUnit.SECONDS)) {
                    throw new ExceptionInInitializerError("JavaFX Platform.startup() timed out in DocPatientDataControllerTest.");
                }
            } catch (IllegalStateException e) {
                // Platform already started by another test, set shared flag
                System.out.println("JavaFX Platform was already initialized (detected in DocPatientDataControllerTest).");
                DocCalendarControllerTest.fxInitialized = true;
            }
        }
    }

    @AfterAll
    static void afterAll() {
        if (mockedCustomerServiceStatic != null) {
            mockedCustomerServiceStatic.close();
        }
        // Platform.setImplicitExit(false); // Only if responsible for toolkit lifecycle globally
    }

    @BeforeEach
    void setUp() throws Exception {
        docPatientDataController = new DocPatientDataController();

        // Use reflection to set the final customerService field in the controller
        setPrivateField(docPatientDataController, "customerService", mockCustomerService);

        // Inject FXML Label mocks
        setPrivateField(docPatientDataController, "heightLabel", heightLabel);
        setPrivateField(docPatientDataController, "weightLabel", weightLabel);
        setPrivateField(docPatientDataController, "smokingLabel", smokingLabel);
        setPrivateField(docPatientDataController, "alcoholLabel", alcoholLabel);
        setPrivateField(docPatientDataController, "trainingLabel", trainingLabel);
        setPrivateField(docPatientDataController, "disabilityLabel", disabilityLabel);
        setPrivateField(docPatientDataController, "disabilityDetailsLabel", disabilityDetailsLabel);
        setPrivateField(docPatientDataController, "heartDiseaseLabel", heartDiseaseLabel);
        setPrivateField(docPatientDataController, "bloodPressureLabel", bloodPressureLabel);
        setPrivateField(docPatientDataController, "heartbeatLabel", heartbeatLabel);
        setPrivateField(docPatientDataController, "strokeLabel", strokeLabel);
        setPrivateField(docPatientDataController, "asthmaLabel", asthmaLabel);
        setPrivateField(docPatientDataController, "lungDiseaseLabel", lungDiseaseLabel);
        setPrivateField(docPatientDataController, "seizureLabel", seizureLabel);
        setPrivateField(docPatientDataController, "neurologicalLabel", neurologicalLabel);
        setPrivateField(docPatientDataController, "hspRespiratoryCardioLabel", hspRespiratoryCardioLabel);
        setPrivateField(docPatientDataController, "hspHeartLungLabel", hspHeartLungLabel);
        setPrivateField(docPatientDataController, "medicationLabel", medicationLabel);
        setPrivateField(docPatientDataController, "allergiesLabel", allergiesLabel);
        setPrivateField(docPatientDataController, "surgeryLabel", surgeryLabel);
        setPrivateField(docPatientDataController, "injuryLabel", injuryLabel);

        docPatientDataController = spy(docPatientDataController);
        lenient().doNothing().when(docPatientDataController).showError(anyString(), anyString());
        lenient().doNothing().when(disabilityDetailsLabel).setVisible(anyBoolean());
        lenient().doNothing().when(disabilityDetailsLabel).setDisable(anyBoolean());
    }

    // Helper for reflection
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = findField(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in class " + clazz.getName() + " or its superclasses.");
    }

    @Test
    void testLoadPatientData_Success() throws SQLException {
        // Given
        String patientId = "testPatient123";
        MedicalData mockData = mock(MedicalData.class);
        when(mockCustomerService.getMedicalData(patientId)).thenReturn(mockData);

        // When
        docPatientDataController.loadPatientData(patientId);

        // Then
        verify(mockCustomerService).getMedicalData(patientId);
        // verify(docPatientDataController).updateUI(mockData); // Cannot verify protected method from different package
        verify(docPatientDataController, never()).showError(anyString(), anyString());
    }

    @Test
    void testLoadPatientData_ServiceThrowsSQLException() throws SQLException {
        // Given
        String patientId = "testPatientWithException";
        when(mockCustomerService.getMedicalData(patientId)).thenThrow(new SQLException("Database error simulated"));

        // When
        docPatientDataController.loadPatientData(patientId);

        // Then
        verify(mockCustomerService).getMedicalData(patientId);
        // verify(docPatientDataController, never()).updateUI(any(MedicalData.class)); // Cannot verify protected method from different package
        verify(docPatientDataController).showError("Error", "Error loading patient data. Try again later.");
    }
}