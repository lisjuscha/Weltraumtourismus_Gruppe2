package com.example.flightprep.controller.BasicController;

import com.example.flightprep.model.MedicalData;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Test suite for the abstract {@link PatientDataDisplayController}.
 * This class utilizes a concrete inner subclass, {@code TestPatientDataDisplayController},
 * to test the {@code updateUI(MedicalData data)} method. It verifies that FXML labels
 * are correctly populated based on the provided {@link MedicalData}, including handling
 * of null data, boolean conversions to "Yes"/"No", and visibility of disability details.
 * Mocks are used for all FXML {@link Label} fields, and JavaFX toolkit initialization is performed.
 */
@ExtendWith(MockitoExtension.class)
class PatientDataDisplayControllerTest {

    // Concrete class for testing the abstract PatientDataDisplayController
    static class TestPatientDataDisplayController extends PatientDataDisplayController {
        // Expose showError for verification if needed, or rely on @Spy
        @Override
        public void showError(String title, String content) {
            super.showError(title, content);
        }
    }

    @Spy
    private TestPatientDataDisplayController controllerSpy = new TestPatientDataDisplayController();

    // Mock all FXML labels
    @Mock Label heightLabel; @Mock Label weightLabel; @Mock Label smokingLabel; @Mock Label alcoholLabel;
    @Mock Label trainingLabel; @Mock Label disabilityLabel; @Mock Label disabilityDetailsLabel;
    @Mock Label heartDiseaseLabel; @Mock Label bloodPressureLabel; @Mock Label heartbeatLabel;
    @Mock Label strokeLabel; @Mock Label asthmaLabel; @Mock Label lungDiseaseLabel;
    @Mock Label seizureLabel; @Mock Label neurologicalLabel; @Mock Label hspRespiratoryCardioLabel;
    @Mock Label hspHeartLungLabel; @Mock Label medicationLabel; @Mock Label allergiesLabel;
    @Mock Label surgeryLabel; @Mock Label injuryLabel;

    private static volatile boolean fxInitialized = false;

    @BeforeAll
    static void setUpJavaFX() {
        if (fxInitialized) return;
        try {
            CountDownLatch checkLatch = new CountDownLatch(1);
            Platform.runLater(checkLatch::countDown);
            if (checkLatch.await(200, TimeUnit.MILLISECONDS)) {
                fxInitialized = true; return;
            }
        } catch (Exception e) { /* proceed to start */ }
        try {
            CountDownLatch startupLatch = new CountDownLatch(1);
            Platform.startup(startupLatch::countDown);
            if (!startupLatch.await(5, TimeUnit.SECONDS)) {
                System.err.println("Timeout waiting for JavaFX platform to start in PatientDataDisplayControllerTest.");
                return;
            }
            fxInitialized = true;
        } catch (Exception e) {
            fxInitialized = true; // Assume already started or failed
        }
    }

    @BeforeEach
    void setUp() {
        // Inject mock labels into the controller instance
        // Direct field assignment is possible because the test subclass can access protected fields.
        controllerSpy.heightLabel = heightLabel;
        controllerSpy.weightLabel = weightLabel;
        controllerSpy.smokingLabel = smokingLabel;
        controllerSpy.alcoholLabel = alcoholLabel;
        controllerSpy.trainingLabel = trainingLabel;
        controllerSpy.disabilityLabel = disabilityLabel;
        controllerSpy.disabilityDetailsLabel = disabilityDetailsLabel;
        controllerSpy.heartDiseaseLabel = heartDiseaseLabel;
        controllerSpy.bloodPressureLabel = bloodPressureLabel;
        controllerSpy.heartbeatLabel = heartbeatLabel;
        controllerSpy.strokeLabel = strokeLabel;
        controllerSpy.asthmaLabel = asthmaLabel;
        controllerSpy.lungDiseaseLabel = lungDiseaseLabel;
        controllerSpy.seizureLabel = seizureLabel;
        controllerSpy.neurologicalLabel = neurologicalLabel;
        controllerSpy.hspRespiratoryCardioLabel = hspRespiratoryCardioLabel;
        controllerSpy.hspHeartLungLabel = hspHeartLungLabel;
        controllerSpy.medicationLabel = medicationLabel;
        controllerSpy.allergiesLabel = allergiesLabel;
        controllerSpy.surgeryLabel = surgeryLabel;
        controllerSpy.injuryLabel = injuryLabel;

        // Spy on showError to verify calls
        // This is already handled by @Spy on controllerSpy and TestPatientDataDisplayController overriding showError
        // We need to ensure that showError in GeneralController (parent of DocController) is mockable/verifiable.
        // For now, let's assume the @Spy setup is sufficient.
    }

    @Test
    void updateUI_nullData_showsErrorAndReturns() {
        // Arrange
        doNothing().when(controllerSpy).showError(anyString(), anyString());

        // Act
        controllerSpy.updateUI(null);

        // Assert
        verify(controllerSpy).showError("Error", "No patient data found");
        // Verify no labels were touched
        verifyNoInteractions(heightLabel, weightLabel, smokingLabel, alcoholLabel, trainingLabel, disabilityLabel,
                disabilityDetailsLabel, heartDiseaseLabel, bloodPressureLabel, heartbeatLabel, strokeLabel,
                asthmaLabel, lungDiseaseLabel, seizureLabel, neurologicalLabel, hspRespiratoryCardioLabel,
                hspHeartLungLabel, medicationLabel, allergiesLabel, surgeryLabel, injuryLabel);
    }

    private MedicalData createMedicalData(boolean allBooleansValue, boolean disabilityStatus, String disabilityDetailsText) {
        return new MedicalData(
                "180", "75", "Occasionally", "Never", allBooleansValue, /* trainingStatus */
                disabilityStatus, disabilityDetailsText, allBooleansValue, /* heartDisease */
                allBooleansValue, /* highBloodPressure */ allBooleansValue, /* irregularHeartbeat */
                allBooleansValue, /* strokeHistory */ allBooleansValue, /* asthma */
                allBooleansValue, /* lungDisease */ allBooleansValue, /* seizureHistory */
                allBooleansValue, /* neurologicalDisorder */ allBooleansValue, /* hsp_respiratory_cardio */
                allBooleansValue, /* hsp_heart_lung */ allBooleansValue, /* persc_med */
                allBooleansValue, /* allergies */ allBooleansValue, /* surgery */
                allBooleansValue /* ser_injury */
        );
    }

    @Test
    void updateUI_withData_allBooleansFalse_disabilityFalse() {
        MedicalData data = createMedicalData(false, false, "");
        controllerSpy.updateUI(data);

        verify(controllerSpy, never()).showError(anyString(), anyString());
        verify(heightLabel).setText("180 cm");
        verify(weightLabel).setText("75 kg");
        verify(smokingLabel).setText("Never");
        verify(alcoholLabel).setText("Occasionally");
        verify(trainingLabel).setText("No");
        verify(disabilityLabel).setText("No");
        verify(disabilityDetailsLabel, never()).setText(anyString()); // Not disabled
        verify(disabilityDetailsLabel, never()).setVisible(true);      // Should remain invisible

        verify(heartDiseaseLabel).setText("No");
        verify(bloodPressureLabel).setText("No");
        verify(heartbeatLabel).setText("No");
        verify(strokeLabel).setText("No");
        verify(asthmaLabel).setText("No");
        verify(lungDiseaseLabel).setText("No");
        verify(seizureLabel).setText("No");
        verify(neurologicalLabel).setText("No");
        verify(hspRespiratoryCardioLabel).setText("No");
        verify(hspHeartLungLabel).setText("No");
        verify(medicationLabel).setText("No");
        verify(allergiesLabel).setText("No");
        verify(surgeryLabel).setText("No");
        verify(injuryLabel).setText("No");
    }

    @Test
    void updateUI_withData_allBooleansTrue_disabilityFalse() {
        MedicalData data = createMedicalData(true, false, ""); // Disability false, details don't matter
        controllerSpy.updateUI(data);

        verify(controllerSpy, never()).showError(anyString(), anyString());
        verify(trainingLabel).setText("Yes");
        verify(disabilityLabel).setText("No"); // Disability is false
        verify(disabilityDetailsLabel, never()).setText(anyString());
        verify(disabilityDetailsLabel, never()).setVisible(true);

        verify(heartDiseaseLabel).setText("Yes");
        verify(bloodPressureLabel).setText("Yes");
        verify(heartbeatLabel).setText("Yes");
        verify(strokeLabel).setText("Yes");
        verify(asthmaLabel).setText("Yes");
        verify(lungDiseaseLabel).setText("Yes");
        verify(seizureLabel).setText("Yes");
        verify(neurologicalLabel).setText("Yes");
        verify(hspRespiratoryCardioLabel).setText("Yes");
        verify(hspHeartLungLabel).setText("Yes");
        verify(medicationLabel).setText("Yes");
        verify(allergiesLabel).setText("Yes");
        verify(surgeryLabel).setText("Yes");
        verify(injuryLabel).setText("Yes");
    }

    @Test
    void updateUI_withData_allBooleansTrue_disabilityTrue() {
        String details = "Some disability details";
        MedicalData data = createMedicalData(true, true, details);
        controllerSpy.updateUI(data);

        verify(controllerSpy, never()).showError(anyString(), anyString());
        verify(trainingLabel).setText("Yes");
        verify(disabilityLabel).setText("Yes");
        verify(disabilityDetailsLabel).setText(details);
        verify(disabilityDetailsLabel).setVisible(true);
        verify(disabilityDetailsLabel).setDisable(false);

        verify(heartDiseaseLabel).setText("Yes");
        verify(bloodPressureLabel).setText("Yes");
        verify(heartbeatLabel).setText("Yes");
        verify(strokeLabel).setText("Yes");
        verify(asthmaLabel).setText("Yes");
        verify(lungDiseaseLabel).setText("Yes");
        verify(seizureLabel).setText("Yes");
        verify(neurologicalLabel).setText("Yes");
        verify(hspRespiratoryCardioLabel).setText("Yes");
        verify(hspHeartLungLabel).setText("Yes");
        verify(medicationLabel).setText("Yes");
        verify(allergiesLabel).setText("Yes");
        verify(surgeryLabel).setText("Yes");
        verify(injuryLabel).setText("Yes");
    }
}