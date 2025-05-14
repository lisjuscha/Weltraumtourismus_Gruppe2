package com.example.flightprep.controller.Login;

import com.example.flightprep.model.User;
import com.example.flightprep.service.UserService;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.beans.property.DoubleProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Answers;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for {@link LoginController}.
 * This class tests the user authentication logic, role-based redirection,
 * and UI initialization behavior of the login screen. It employs Mockito for mocking
 * dependencies such as {@link UserService}, {@link SessionManager}, and {@link SceneSwitcher},
 * as well as FXML injected components. JavaFX toolkit initialization is handled
 * to support testing of JavaFX components and properties.
 */
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private ImageView loginImageView;
    @Mock
    private TextField user_idInput;
    @Mock
    private TextField passwordInput;
    @Mock
    private BorderPane borderPane; // Mocked because initialize() binds to its properties

    @Spy
    @InjectMocks
    private LoginController loginController;

    private static volatile boolean fxInitialized = false;

    @BeforeAll
    static void setUpJavaFX() {
        if (fxInitialized) {
            return;
        }
        try {
            // Check if FX platform is already running by trying to execute on FX thread
            CountDownLatch checkLatch = new CountDownLatch(1);
            Platform.runLater(checkLatch::countDown);
            if (checkLatch.await(200, TimeUnit.MILLISECONDS)) {
                fxInitialized = true; // Platform is responsive
                return;
            }
            // If timeout, platform might not be started or is unresponsive
        } catch (IllegalStateException e) {
            // This exception means toolkit is not initialized, so we need to start it.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while checking JavaFX platform status.");
            // Fall through to attempt startup
        }

        // If not yet initialized or check failed, attempt to start the platform
        try {
            CountDownLatch startupLatch = new CountDownLatch(1);
            Platform.startup(startupLatch::countDown);
            if (!startupLatch.await(5, TimeUnit.SECONDS)) {
                System.err.println("Timeout waiting for JavaFX platform to start.");
                return; // Avoid setting fxInitialized if startup timed out
            }
            fxInitialized = true;
        } catch (IllegalStateException e) {
            // This means it was already started (possibly by another thread/class between our check and this attempt)
            fxInitialized = true; // Mark as initialized
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("JavaFX startup attempt was interrupted.");
        }
    }

    @AfterAll
    static void tearDownJavaFX() {
        // Platform.exit(); // Usually not needed / can cause issues with subsequent test runs in a suite
    }

    @BeforeEach
    void setUp() {
        // This method is now empty, as property stubbings are moved to the specific test that needs them.
    }

    // Tests for initialize() will go here
    @Test
    void initialize_setsImageAndBindsProperties() {
        // Arrange
        // Mock properties for loginImageView
        DoubleProperty mockFitHeightProperty = mock(DoubleProperty.class, Answers.RETURNS_DEEP_STUBS);
        when(loginImageView.fitHeightProperty()).thenReturn(mockFitHeightProperty);

        DoubleProperty mockFitWidthProperty = mock(DoubleProperty.class, Answers.RETURNS_DEEP_STUBS);
        when(loginImageView.fitWidthProperty()).thenReturn(mockFitWidthProperty);

        // Mock properties for borderPane
        DoubleProperty mockHeightProperty = mock(DoubleProperty.class, Answers.RETURNS_DEEP_STUBS);
        when(borderPane.heightProperty()).thenReturn(mockHeightProperty);

        DoubleProperty mockWidthProperty = mock(DoubleProperty.class, Answers.RETURNS_DEEP_STUBS);
        when(borderPane.widthProperty()).thenReturn(mockWidthProperty);

        // Mock the construction of Image objects to avoid actual file loading
        try (MockedConstruction<Image> mockedImageConstruction = mockConstruction(Image.class)) {

            // Act
            loginController.initialize();

            // Assert
            // Verify that an Image was constructed (implicitly, a resource was attempted to be loaded)
            assertEquals(1, mockedImageConstruction.constructed().size());
            Image constructedImage = mockedImageConstruction.constructed().get(0);

            // Verify that setImage was called on the mock ImageView with the constructed Image
            verify(loginImageView).setImage(constructedImage);

            // Verify that property binding methods were called
            // We don't need to assert the actual binding, just that the calls were made
            verify(loginImageView).fitHeightProperty(); // Implicitly, bind was called on its result
            verify(borderPane).heightProperty();

            verify(loginImageView).fitWidthProperty(); // Implicitly, bind was called on its result
            verify(borderPane).widthProperty();
            // We can't easily verify the .multiply(0.8) part without more complex mocking of DoubleProperty etc.
            // For coverage, ensuring fitWidthProperty() and widthProperty() are called is a good start.
        }
    }

    // Tests for login() will go here
    @Test
    void login_successfulDoctorLogin_switchesToDoctorHome() throws IOException {
        // Arrange
        String userId = "doc1";
        String password = "pass";
        User doctorUser = new User(userId, password, "doctor");
        ActionEvent mockEvent = mock(ActionEvent.class);

        when(user_idInput.getText()).thenReturn(userId);
        when(passwordInput.getText()).thenReturn(password);

        // Mock construction of UserService and its authenticateUser method
        try (MockedConstruction<UserService> mockedUserServiceConstruction = mockConstruction(UserService.class,
                (mock, context) -> when(mock.authenticateUser(userId, password)).thenReturn(doctorUser));
             MockedStatic<SessionManager> mockedSessionManager = mockStatic(SessionManager.class);
             MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {

            // Act
            loginController.login(mockEvent);

            // Assert
            assertEquals(1, mockedUserServiceConstruction.constructed().size());
            UserService constructedService = mockedUserServiceConstruction.constructed().get(0);
            verify(constructedService).authenticateUser(userId, password);

            mockedSessionManager.verify(() -> SessionManager.setCurrentUser(doctorUser));
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocHome.fxml", mockEvent));
            verify(loginController, never()).showError(anyString(), anyString());
        }
    }

    @Test
    void login_successfulCustomerLogin_switchesToCustomerHome() throws IOException {
        // Arrange
        String userId = "cust1";
        String password = "pass123";
        User customerUser = new User(userId, password, "customer");
        ActionEvent mockEvent = mock(ActionEvent.class);

        when(user_idInput.getText()).thenReturn(userId);
        when(passwordInput.getText()).thenReturn(password);

        try (MockedConstruction<UserService> mockedUserServiceConstruction = mockConstruction(UserService.class,
                (mock, context) -> when(mock.authenticateUser(userId, password)).thenReturn(customerUser));
             MockedStatic<SessionManager> mockedSessionManager = mockStatic(SessionManager.class);
             MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {

            // Act
            loginController.login(mockEvent);

            // Assert
            assertEquals(1, mockedUserServiceConstruction.constructed().size());
            UserService constructedService = mockedUserServiceConstruction.constructed().get(0);
            verify(constructedService).authenticateUser(userId, password);

            mockedSessionManager.verify(() -> SessionManager.setCurrentUser(customerUser));
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerHome.fxml", mockEvent));
            verify(loginController, never()).showError(anyString(), anyString());
        }
    }

    @Test
    void login_authenticationFails_showsError() throws IOException {
        // Arrange
        String userId = "wrongUser";
        String password = "wrongPass";
        ActionEvent mockEvent = mock(ActionEvent.class);

        when(user_idInput.getText()).thenReturn(userId);
        when(passwordInput.getText()).thenReturn(password);

        // Mock UserService to return null for authentication
        try (MockedConstruction<UserService> mockedUserServiceConstruction = mockConstruction(UserService.class,
                (mock, context) -> when(mock.authenticateUser(userId, password)).thenReturn(null));
             MockedStatic<SessionManager> mockedSessionManager = mockStatic(SessionManager.class);
             MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {

            // Ensure showError does not throw an exception itself (it's a spy)
            doNothing().when(loginController).showError(anyString(), anyString());

            // Act
            loginController.login(mockEvent);

            // Assert
            assertEquals(1, mockedUserServiceConstruction.constructed().size());
            UserService constructedService = mockedUserServiceConstruction.constructed().get(0);
            verify(constructedService).authenticateUser(userId, password);

            mockedSessionManager.verifyNoInteractions();
            mockedSceneSwitcher.verifyNoInteractions();
            verify(loginController).showError("Error", "Invalid user ID or password.");
        }
    }

    @Test
    void login_sceneSwitchThrowsIOException_showsError() throws IOException {
        // Arrange
        String userId = "doc1";
        String password = "pass";
        User doctorUser = new User(userId, password, "doctor");
        ActionEvent mockEvent = mock(ActionEvent.class);

        when(user_idInput.getText()).thenReturn(userId);
        when(passwordInput.getText()).thenReturn(password);

        try (MockedConstruction<UserService> mockedUserServiceConstruction = mockConstruction(UserService.class,
                (mock, context) -> when(mock.authenticateUser(userId, password)).thenReturn(doctorUser));
             MockedStatic<SessionManager> mockedSessionManager = mockStatic(SessionManager.class);
             MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {

            // Configure SceneSwitcher to throw IOException
            mockedSceneSwitcher.when(() -> SceneSwitcher.switchScene(anyString(), any(ActionEvent.class)))
                    .thenThrow(new IOException("Test Scene Load Failure"));

            // Ensure showError does not throw an exception itself
            doNothing().when(loginController).showError(anyString(), anyString());

            // Act
            loginController.login(mockEvent);

            // Assert
            verify(mockedUserServiceConstruction.constructed().get(0)).authenticateUser(userId, password); // UserService still called
            mockedSessionManager.verify(() -> SessionManager.setCurrentUser(doctorUser)); // SessionManager still called
            verify(loginController).showError("Error", "Failed to load the home screen.");
        }
    }

    @Test
    void login_sceneSwitchThrowsNullPointerException_showsError() throws IOException {
        // Arrange
        String userId = "doc1";
        String password = "pass";
        User doctorUser = new User(userId, password, "doctor");
        ActionEvent mockEvent = mock(ActionEvent.class);

        when(user_idInput.getText()).thenReturn(userId);
        when(passwordInput.getText()).thenReturn(password);

        try (MockedConstruction<UserService> mockedUserServiceConstruction = mockConstruction(UserService.class,
                (mock, context) -> when(mock.authenticateUser(userId, password)).thenReturn(doctorUser));
             MockedStatic<SessionManager> mockedSessionManager = mockStatic(SessionManager.class);
             MockedStatic<SceneSwitcher> mockedSceneSwitcher = mockStatic(SceneSwitcher.class)) {

            // Configure SceneSwitcher to throw NullPointerException
            mockedSceneSwitcher.when(() -> SceneSwitcher.switchScene(anyString(), any(ActionEvent.class)))
                    .thenThrow(new NullPointerException("Test NPE from SceneSwitcher"));

            // Ensure showError does not throw an exception itself
            doNothing().when(loginController).showError(anyString(), anyString());

            // Act
            loginController.login(mockEvent);

            // Assert
            verify(mockedUserServiceConstruction.constructed().get(0)).authenticateUser(userId, password);
            mockedSessionManager.verify(() -> SessionManager.setCurrentUser(doctorUser));
            verify(loginController).showError("Error", "Failed to load the home screen.");
        }
    }

}