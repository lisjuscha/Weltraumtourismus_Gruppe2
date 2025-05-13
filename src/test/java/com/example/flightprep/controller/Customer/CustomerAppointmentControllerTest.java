package com.example.flightprep.controller.Customer;

import com.example.flightprep.service.AppointmentService;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SessionManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class CustomerAppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private CustomerService customerService;

    private CustomerAppointmentController controller;

    @BeforeAll
    public static void setupHeadless() {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
    }

    @Start
    private void start() throws Exception {
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller = new CustomerAppointmentController();
                setField("appointmentService", appointmentService);
                setField("customerService", customerService);
                setField("weekGrid", new GridPane());
                setField("weekLabel", new Label());
                setField("prevWeekButton", new Button());
                setField("nextWeekButton", new Button());
                latch.countDown();
            } catch (Exception e) {
                fail("Setup failed: " + e.getMessage());
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Setup timeout");
        }
    }

    private void setField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = CustomerAppointmentController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private void runAndWait(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Test timeout");
        }
    }

    @Test
    void testInitializeDates_FlightDateFound() throws Exception {
        String userId = "123";
        LocalDate flightDate = LocalDate.of(2023, 12, 25);

        when(SessionManager.getCurrentUserId()).thenReturn(userId);
        when(customerService.getFlightDate(userId)).thenReturn(flightDate);

        runAndWait(() -> {
            try {
                controller.initializeDatesForTest();
                assertEquals(flightDate, controller.getFlightDate());
            } catch (SQLException e) {
                fail(e);
            }
        });
    }

    @Test
    void testInitializeDates_NoUserId() throws Exception {
        when(SessionManager.getCurrentUserId()).thenReturn(null);

        runAndWait(() -> {
            try {
                controller.initializeDatesForTest();
                assertNull(controller.getFlightDate());
            } catch (SQLException e) {
                fail(e);
            }
        });
    }

    @Test
    void testConfigureUnavailableSlot() throws Exception {
        Button slot = new Button();

        runAndWait(() -> {
            controller.configureUnavailableSlotForTest(slot);
            assertEquals("Not Available", slot.getText());
            assertTrue(slot.getStyleClass().contains("time-slot-occupied"));
            assertTrue(slot.isDisabled());
        });
    }

    @Test
    void testConfigureAvailableSlot() throws Exception {
        Button slot = new Button();
        LocalDate date = LocalDate.of(2023, 12, 20);
        String time = "09:00";

        runAndWait(() -> {
            controller.configureAvailableSlotForTest(slot, date, time);
            assertEquals("Available", slot.getText());
            assertTrue(slot.getStyleClass().contains("time-slot"));
            assertNotNull(slot.getOnAction());
        });
    }

    @Test
    void testCreateTimeSlot_InvalidTime() throws Exception {
        // Mock für isValidSlot konfigurieren
        when(appointmentService.isValidSlot(any(LocalDate.class), anyString()))
                .thenThrow(new SQLException("Test exception"));

        runAndWait(() -> {
            Button slot = controller.createTimeSlotForTest(5, 1);
            assertTrue(slot.isDisabled());
            assertEquals("Error", slot.getText());
        });
    }

    @Test
    void testHandlePreviousWeek() throws Exception {
        LocalDate initialDate = LocalDate.of(2023, 12, 18);
        setCurrentWeekStart(initialDate);
        setFlightDate(initialDate.plusMonths(1));

        runAndWait(() -> {
            controller.handlePreviousWeekForTest();
            assertEquals(initialDate.minusWeeks(1), controller.getCurrentWeekStart());
        });
    }

    @Test
    void testHandleNextWeek() throws Exception {
        LocalDate initialDate = LocalDate.of(2023, 12, 18);
        setCurrentWeekStart(initialDate);
        setFlightDate(initialDate.plusMonths(1));

        runAndWait(() -> {
            controller.handleNextWeekForTest();
            assertEquals(initialDate.plusWeeks(1), controller.getCurrentWeekStart());
        });
    }

    @Test
    void testUpdateNavigationButtons_DisableNextWeek() throws Exception {
        setFlightDate(LocalDate.of(2023, 12, 25));
        setCurrentWeekStart(LocalDate.of(2023, 12, 18));

        runAndWait(() -> {
            controller.updateNavigationButtonsForTest();
            assertTrue(controller.getNextWeekButton().isDisabled());
        });
    }

    @Test
    void testUpdateNavigationButtons_DisablePrevWeek() throws Exception {
        setCurrentWeekStart(LocalDate.now());

        runAndWait(() -> {
            controller.updateNavigationButtonsForTest();
            assertTrue(controller.getPrevWeekButton().isDisabled());
        });
    }

    @Test
    void testUpdateNavigationButtons_NullFlightDate() throws Exception {
        setFlightDate(null);

        runAndWait(() -> {
            controller.updateNavigationButtonsForTest();
            assertTrue(controller.getPrevWeekButton().isDisabled());
            assertTrue(controller.getNextWeekButton().isDisabled());
        });
    }

    private void setCurrentWeekStart(LocalDate date) throws Exception {
        setField("currentWeekStart", date);
    }

    private void setFlightDate(LocalDate date) throws Exception {
        setField("flightDate", date);
    }
}