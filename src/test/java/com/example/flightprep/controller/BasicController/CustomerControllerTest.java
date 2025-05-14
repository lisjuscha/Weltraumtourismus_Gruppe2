package com.example.flightprep.controller.BasicController;

import com.example.flightprep.controller.Customer.CustomerHomeController;
import com.example.flightprep.util.SceneSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test suite for the abstract {@link CustomerController}.
 * This class uses a concrete subclass, {@link CustomerHomeController}, as the SUT to test
 * the navigation methods defined in {@link CustomerController}. It verifies correct scene
 * switching logic and error handling for common customer navigation actions.
 * JavaFX toolkit initialization and static mocking for {@link SceneSwitcher} are utilized.
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    // Using CustomerHomeController as a concrete class to test abstract CustomerController
    @Spy
    private CustomerHomeController customerController = new CustomerHomeController();

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
            // Fall through to attempt startup
        }

        try {
            CountDownLatch startupLatch = new CountDownLatch(1);
            Platform.startup(startupLatch::countDown);
            if (!startupLatch.await(5, TimeUnit.SECONDS)) {
                System.err.println("Timeout waiting for JavaFX platform to start in CustomerControllerTest.");
                return;
            }
            fxInitialized = true;
        } catch (IllegalStateException e) {
            fxInitialized = true; // Already started
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("JavaFX startup attempt was interrupted in CustomerControllerTest.");
        }
    }

    @AfterAll
    static void tearDownJavaFX() {
        // Potentially Platform.exit() if it was the last test, but generally managed by JVM shutdown.
    }

    // --- Test switchToHome ---
    @Test
    void switchToHome_success() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            customerController.switchToHome(mockEvent);
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerHome.fxml", mockEvent));
            verify(customerController, never()).showError(anyString(), anyString());
        }
    }

    @Test
    void switchToHome_failure_showsError() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            mockedSceneSwitcher.when(() -> SceneSwitcher.switchScene(anyString(), any(ActionEvent.class)))
                    .thenThrow(new IOException("Test switch scene error"));
            doNothing().when(customerController).showError(anyString(), anyString());

            customerController.switchToHome(mockEvent);

            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerHome.fxml", mockEvent));
            verify(customerController).showError("Error", "Error switching to home screen: Test switch scene error");
        }
    }

    // --- Test switchToFlightPrep ---
    @Test
    void switchToFlightPrep_success() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            customerController.switchToFlightPrep(mockEvent);
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", mockEvent));
            verify(customerController, never()).showError(anyString(), anyString());
        }
    }

    @Test
    void switchToFlightPrep_failure_showsError() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            mockedSceneSwitcher.when(() -> SceneSwitcher.switchScene(anyString(), any(ActionEvent.class)))
                    .thenThrow(new IOException("Test switch scene error"));
            doNothing().when(customerController).showError(anyString(), anyString());

            customerController.switchToFlightPrep(mockEvent);

            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", mockEvent));
            verify(customerController).showError("Error", "Error switching to preparation screen: Test switch scene error");
        }
    }

    // --- Test switchToCalendar ---
    @Test
    void switchToCalendar_success() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            customerController.switchToCalendar(mockEvent);
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendar.fxml", mockEvent));
            verify(customerController, never()).showError(anyString(), anyString());
        }
    }

    @Test
    void switchToCalendar_failure_showsError() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            mockedSceneSwitcher.when(() -> SceneSwitcher.switchScene(anyString(), any(ActionEvent.class)))
                    .thenThrow(new IOException("Test switch scene error"));
            doNothing().when(customerController).showError(anyString(), anyString());

            customerController.switchToCalendar(mockEvent);

            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendar.fxml", mockEvent));
            verify(customerController).showError("Error", "Error switching to calendar screen: Test switch scene error");
        }
    }

    // --- Test switchToMyFlight ---
    @Test
    void switchToMyFlight_success() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            customerController.switchToMyFlight(mockEvent);
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerMyFlight.fxml", mockEvent));
            verify(customerController, never()).showError(anyString(), anyString());
        }
    }

    @Test
    void switchToMyFlight_failure_showsError() {
        ActionEvent mockEvent = mock(ActionEvent.class);
        try (MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {
            mockedSceneSwitcher.when(() -> SceneSwitcher.switchScene(anyString(), any(ActionEvent.class)))
                    .thenThrow(new IOException("Test switch scene error"));
            doNothing().when(customerController).showError(anyString(), anyString());

            customerController.switchToMyFlight(mockEvent);

            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerMyFlight.fxml", mockEvent));
            verify(customerController).showError("Error", "Error switching to my flight screen: Test switch scene error");
        }
    }

}