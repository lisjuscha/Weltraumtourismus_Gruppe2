package com.example.flightprep.controller.BasicController;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Test suite for the abstract {@link GeneralController}.
 * This class uses a concrete inner subclass, {@code TestGeneralController}, to facilitate testing
 * of the alert creation and display methods ({@code createAlert}, {@code showSuccess}, {@code showError})
 * defined in {@link GeneralController}. It leverages Mockito's {@code mockConstruction} to verify
 * interactions with JavaFX {@link Alert} objects without actually displaying them.
 * JavaFX toolkit initialization is also handled.
 */
@ExtendWith(MockitoExtension.class)
class GeneralControllerTest {

    // Concrete class for testing the abstract GeneralController
    static class TestGeneralController extends GeneralController {}

    @Spy
    private TestGeneralController generalController = new TestGeneralController();

    // JavaFX setup
    private static volatile boolean fxInitialized = false;

    @BeforeAll
    static void setUpJavaFX() {
        if (fxInitialized) {
            return;
        }
        try {
            CountDownLatch checkLatch = new CountDownLatch(1);
            Platform.runLater(checkLatch::countDown);
            if (checkLatch.await(200, TimeUnit.MILLISECONDS)) {
                fxInitialized = true;
                return;
            }
        } catch (IllegalStateException e) {
            // Toolkit not initialized, proceed to start
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while checking JavaFX platform status in GeneralControllerTest.");
            // Fall through to attempt startup
        }

        try {
            CountDownLatch startupLatch = new CountDownLatch(1);
            Platform.startup(startupLatch::countDown);
            if (!startupLatch.await(5, TimeUnit.SECONDS)) {
                System.err.println("Timeout waiting for JavaFX platform to start in GeneralControllerTest.");
                return;
            }
            fxInitialized = true;
        } catch (IllegalStateException e) {
            fxInitialized = true; // Already started
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("JavaFX startup attempt was interrupted in GeneralControllerTest.");
        }
    }


    @Test
    void createAlert_createsAndConfiguresAlert() {
        String title = "Test Title";
        String header = "Test Header";
        String message = "Test Message";
        Alert.AlertType type = Alert.AlertType.CONFIRMATION;

        try (MockedConstruction<Alert> mockedAlertConstruction = mockConstruction(Alert.class,
                (mock, context) -> {
                    assertEquals(type, context.arguments().get(0));
                })) {

            Alert createdAlert = generalController.createAlert(title, header, message, type);

            assertNotNull(createdAlert);
            assertEquals(1, mockedAlertConstruction.constructed().size());
            Alert constructedAlertMock = mockedAlertConstruction.constructed().get(0);

            verify(constructedAlertMock).setTitle(title);
            verify(constructedAlertMock).setHeaderText(header);
            verify(constructedAlertMock).setContentText(message);
            verify(constructedAlertMock, never()).showAndWait();
        }
    }

    @Test
    void showSuccess_createsAndShowsInformationAlert() {
        String title = "Success Title";
        String content = "Success Content";

        try (MockedConstruction<Alert> mockedAlertConstruction = mockConstruction(Alert.class,
                (mock, context) -> {
                    assertEquals(Alert.AlertType.INFORMATION, context.arguments().get(0));
                })) {

            generalController.showSuccess(title, content);

            assertEquals(1, mockedAlertConstruction.constructed().size());
            Alert constructedAlertMock = mockedAlertConstruction.constructed().get(0);

            verify(constructedAlertMock).setTitle(title);
            verify(constructedAlertMock).setHeaderText(null);
            verify(constructedAlertMock).setContentText(content);
            verify(constructedAlertMock).showAndWait();
        }
    }

    @Test
    void showError_createsAndShowsErrorAlert() {
        String title = "Error Title";
        String content = "Error Content";

        try (MockedConstruction<Alert> mockedAlertConstruction = mockConstruction(Alert.class,
                (mock, context) -> {
                    assertEquals(Alert.AlertType.ERROR, context.arguments().get(0));
                })) {

            generalController.showError(title, content);

            assertEquals(1, mockedAlertConstruction.constructed().size());
            Alert constructedAlertMock = mockedAlertConstruction.constructed().get(0);

            verify(constructedAlertMock).setTitle(title);
            verify(constructedAlertMock).setHeaderText(null);
            verify(constructedAlertMock).setContentText(content);
            verify(constructedAlertMock).showAndWait();
        }
    }
}