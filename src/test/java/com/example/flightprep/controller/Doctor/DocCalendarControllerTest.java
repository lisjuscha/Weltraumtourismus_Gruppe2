package com.example.flightprep.controller.Doctor;

import com.example.flightprep.model.Appointment;
import com.example.flightprep.service.AppointmentService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocCalendarControllerTest {

    @Mock
    private AppointmentService mockAppointmentService; // Instance mock

    // @Spy // Removed
    // @InjectMocks // Removed
    private DocCalendarController controller;

    @Mock
    private VBox appointmentsContainer;
    @Mock
    private Label weekLabel;
    @Mock
    private ObservableList<Node> appointmentsChildren; // For appointmentsContainer.getChildren()

    private static MockedStatic<Platform> mockedPlatform;
    private static MockedStatic<AppointmentService> mockedAppointmentServiceStatic; // Added for AppointmentService.getInstance()
    public static boolean fxInitialized = false;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // Added back

    @BeforeAll
    static void setUpJavaFX() throws InterruptedException {
        if (!DocCalendarControllerTest.fxInitialized) { // Use its own flag (it's the source of truth)
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(() -> {
                    latch.countDown();
                    DocCalendarControllerTest.fxInitialized = true;
                });
                if (!latch.await(5, TimeUnit.SECONDS)) {
                    throw new ExceptionInInitializerError("JavaFX Platform.startup() timed out in DocCalendarControllerTest.");
                }
            } catch (IllegalStateException e) {
                System.out.println("JavaFX Platform was already initialized (detected in DocCalendarControllerTest).");
                DocCalendarControllerTest.fxInitialized = true;
            }
        }

        if (mockedPlatform == null || mockedPlatform.isClosed()) {
            mockedPlatform = Mockito.mockStatic(Platform.class);
        }
        mockedPlatform.when(() -> Platform.runLater(any(Runnable.class))).thenAnswer(invocation -> {
            Runnable r = invocation.getArgument(0);
            r.run();
            return null;
        });

        // Static mock for AppointmentService
        mockedAppointmentServiceStatic = Mockito.mockStatic(AppointmentService.class);
        mockedAppointmentServiceStatic.when(() -> AppointmentService.getInstance()).thenReturn(mock(AppointmentService.class, Mockito.RETURNS_DEEP_STUBS));
    }

    @AfterAll
    static void tearDownJavaFX() {
        if (mockedPlatform != null && !mockedPlatform.isClosed()) mockedPlatform.close();
        if (mockedAppointmentServiceStatic != null && !mockedAppointmentServiceStatic.isClosed()) mockedAppointmentServiceStatic.close(); // Close static service mock
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new DocCalendarController();
        setPrivateField(controller, "appointmentService", mockAppointmentService);
        setPrivateField(controller, "appointmentsContainer", appointmentsContainer);
        setPrivateField(controller, "weekLabel", weekLabel);
        when(appointmentsContainer.getChildren()).thenReturn(appointmentsChildren);

        controller = spy(controller);
        lenient().doNothing().when(controller).showError(anyString(), anyString()); // Made lenient
    }

    // Helper methods setPrivateField, findField (assuming they are added or exist)
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
    void initialize_loadsAppointmentsForCurrentWeek() throws SQLException {
        // Given
        LocalDate today = LocalDate.now();
        when(mockAppointmentService.getCurrentWeekStart()).thenReturn(today); // This should now work
        List<Appointment> appointments = new ArrayList<>();
        when(mockAppointmentService.getWeekAppointments(today)).thenReturn(appointments);

        // When
        controller.initialize(null, null);

        // Then
        verify(mockAppointmentService).getCurrentWeekStart();
        verify(mockAppointmentService).getWeekAppointments(today);
        verify(appointmentsChildren, times(7)).add(any(VBox.class)); // Verifying on the mocked children list
        verify(weekLabel).setText(anyString());
        verify(controller, never()).showError(anyString(), anyString());
    }

    // Other tests like loadAppointments_Success will need to verify against appointmentsChildren
    // And ensure mockAppointmentService calls are correctly verified.

    @Test
    void loadAppointments_Success() throws Exception, SQLException {
        // Given
        LocalDate testWeekStart = LocalDate.of(2023, 1, 2); // Montag
        when(mockAppointmentService.getCurrentWeekStart()).thenReturn(testWeekStart); // Stub for initialize() call

        Appointment apt1 = new Appointment(1, "c1", "John", "Doe", "doc1", testWeekStart.format(formatter), "10:00", 1);

        List<Appointment> weekAppointments = new ArrayList<>();
        weekAppointments.add(apt1);
        when(mockAppointmentService.getWeekAppointments(testWeekStart)).thenReturn(weekAppointments);
        when(mockAppointmentService.getRiskGroupColor(1)).thenReturn("#00FF00");

        // When
        controller.initialize(null, null);

        // Then
        verify(appointmentsChildren).clear();
        ArgumentCaptor<VBox> dayContainerCaptor = ArgumentCaptor.forClass(VBox.class);
        verify(appointmentsChildren, times(7)).add(dayContainerCaptor.capture());

        boolean appointmentFound = false;
        for (VBox dayContainer : dayContainerCaptor.getAllValues()) {
            Label dateLabel = (Label) dayContainer.getChildren().get(0);
            if (dateLabel.getText().equals(testWeekStart.format(formatter))) {
                if (dayContainer.getChildren().size() > 1 && dayContainer.getChildren().get(1) instanceof HBox) {
                    HBox appointmentBox = (HBox) dayContainer.getChildren().get(1);
                    Label timeLabel = (Label) appointmentBox.getChildren().get(0);
                    assertEquals("10:00", timeLabel.getText());
                    appointmentFound = true;
                    break;
                }
            }
        }
        assertTrue(appointmentFound, "Termin sollte im Kalender angezeigt werden.");
        verify(weekLabel).setText(testWeekStart.format(formatter) + " - " + testWeekStart.plusDays(6).format(formatter));
    }

    @Test
    void loadAppointments_ServiceThrowsException_ShowsError() throws Exception {
        // Given
        LocalDate testWeekStart = LocalDate.of(2023, 1, 2);
        when(mockAppointmentService.getCurrentWeekStart()).thenReturn(testWeekStart); // Stub for initialize() call
        when(mockAppointmentService.getWeekAppointments(testWeekStart)).thenThrow(new RuntimeException("DB Fehler"));

        // When
        controller.initialize(null, null);

        // Then
        verify(appointmentsChildren).clear();
        verify(controller).showError("Error", "Error loading appointments");
    }

    @Test
    void previousWeek_updatesWeekAndReloadsAppointments() throws Exception, SQLException {
        // Given
        LocalDate initialWeekStart = LocalDate.of(2023, 1, 9);
        setPrivateField(controller, "currentWeekStart", initialWeekStart);
        when(mockAppointmentService.getWeekAppointments(any(LocalDate.class))).thenReturn(new ArrayList<>());

        // When
        controller.previousWeek(null);

        // Then
        LocalDate expectedNewWeekStart = initialWeekStart.minusWeeks(1);
        assertEquals(expectedNewWeekStart, getPrivateField(controller, "currentWeekStart"));
        verify(mockAppointmentService).getWeekAppointments(expectedNewWeekStart);
    }

    @Test
    void nextWeek_updatesWeekAndReloadsAppointments() throws Exception, SQLException {
        // Given
        LocalDate initialWeekStart = LocalDate.of(2023, 1, 2);
        setPrivateField(controller, "currentWeekStart", initialWeekStart);
        when(mockAppointmentService.getWeekAppointments(any(LocalDate.class))).thenReturn(new ArrayList<>());

        // When
        controller.nextWeek(null);

        // Then
        LocalDate expectedNewWeekStart = initialWeekStart.plusWeeks(1);
        assertEquals(expectedNewWeekStart, getPrivateField(controller, "currentWeekStart"));
        verify(mockAppointmentService).getWeekAppointments(expectedNewWeekStart);
    }

    private Object getPrivateField(Object target, String fieldName) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}