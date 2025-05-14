package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.Doctor.DocCalendarControllerTest;
import com.example.flightprep.service.AppointmentService;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SessionManager;
import com.example.flightprep.model.User;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerAppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private CustomerService customerService;

    private CustomerAppointmentController controller;
    private Stage stage;
    private Button prevWeekButton;
    private Button nextWeekButton;
    private Label weekLabel;
    private GridPane weekGrid;

    // Initialize JavaFX toolkit
    @BeforeAll
    public static void setupJavaFX() throws Exception {
        if (!DocCalendarControllerTest.fxInitialized) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(() -> {
                    latch.countDown();
                    DocCalendarControllerTest.fxInitialized = true;
                });
                if (!latch.await(5, TimeUnit.SECONDS)) {
                    throw new ExceptionInInitializerError("JavaFX Platform.startup() timed out in CustomerAppointmentControllerTest.");
                }
            } catch (IllegalStateException e) {
                System.out.println("JavaFX Platform was already initialized (detected in CustomerAppointmentControllerTest).");
                DocCalendarControllerTest.fxInitialized = true;
            }
        }
    }

    // Setup test environment before each test
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        Platform.setImplicitExit(false);

        // Setup SessionManager with mock user
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUser123");
        SessionManager.setCurrentUser(mockUser);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                stage = new Stage();
                controller = new CustomerAppointmentController();

                // Initialize UI components
                prevWeekButton = new Button("Previous");
                nextWeekButton = new Button("Next");
                weekLabel = new Label();
                weekGrid = new GridPane();

                // Set fields via reflection
                setField("appointmentService", appointmentService);
                setField("customerService", customerService);
                setField("weekGrid", weekGrid);
                setField("weekLabel", weekLabel);
                setField("prevWeekButton", prevWeekButton);
                setField("nextWeekButton", nextWeekButton);
                setField("currentWeekStart", LocalDate.now());
                setField("flightDate", LocalDate.now().plusMonths(1));

                VBox root = new VBox(10);
                root.getChildren().addAll(prevWeekButton, weekLabel, nextWeekButton, weekGrid);
                Scene scene = new Scene(root);
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX initialization timed out.");
        }
    }

    // Cleanup after each test
    @AfterEach
    public void cleanup() {
        SessionManager.setCurrentUser(null);
        runOnFXThread(() -> {
            if (stage != null) {
                stage.hide();
                stage = null;
            }
        });
    }

    // Helper method to set private fields
    private void setField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = CustomerAppointmentController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    // Helper method to run code on JavaFX thread
    private void runOnFXThread(Runnable action) {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testInitializeDates_Success() throws Exception {
        String userId = "123";
        LocalDate flightDate = LocalDate.now().plusDays(7);

        when(SessionManager.getCurrentUserId()).thenReturn(userId);
        when(customerService.getFlightDate(userId)).thenReturn(flightDate);

        runOnFXThread(() -> {
            try {
                controller.initializeDatesForTest();
                verify(customerService).getFlightDate(userId);
                assertEquals(flightDate, controller.getFlightDate());
            } catch (SQLException e) {
                fail("Test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testTimeSlotCreation_Available() throws Exception {
        LocalDate testDate = LocalDate.now();
        String testTime = "10:00";

        when(appointmentService.isValidSlot(any(), any())).thenReturn(true);

        runOnFXThread(() -> {
            Button slot = controller.createTimeSlotForTest(1, 2);
            assertNotNull(slot);
            assertFalse(slot.isDisabled());
            assertTrue(slot.getStyleClass().contains("time-slot"));
        });
    }

    @Test
    void testTimeSlotCreation_Unavailable() throws Exception {
        when(appointmentService.isValidSlot(any(), any())).thenReturn(false);

        runOnFXThread(() -> {
            Button slot = controller.createTimeSlotForTest(1, 2);
            assertNotNull(slot);
            assertTrue(slot.isDisabled());
            assertTrue(slot.getStyleClass().contains("time-slot-occupied"));
        });
    }

    @Test
    void testNavigationButtons_Functionality() throws Exception {
        LocalDate initialDate = LocalDate.now();
        setField("currentWeekStart", initialDate);
        setField("flightDate", initialDate.plusMonths(1));

        runOnFXThread(() -> {
            controller.handleNextWeekForTest();
            assertEquals(initialDate.plusWeeks(1), controller.getCurrentWeekStart());

            controller.handlePreviousWeekForTest();
            assertEquals(initialDate, controller.getCurrentWeekStart());
        });
    }

    @Test
    void testUpdateWeekLabel() throws Exception {
        LocalDate testDate = LocalDate.of(2024, 4, 1);
        setField("currentWeekStart", testDate);

        runOnFXThread(() -> {
            controller.updateWeekLabelForTest();
            assertNotNull(weekLabel.getText());
            assertTrue(weekLabel.getText().contains("2024"));
        });
    }
}