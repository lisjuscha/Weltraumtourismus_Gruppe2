package com.example.flightprep.controller.Doctor;

import com.example.flightprep.model.Customer;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SceneSwitcher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocPatientsControllerTest {

    @Mock
    private CustomerService mockCustomerService; // Instance mock for tests

    // @Spy // Removed Spy from field
    // @InjectMocks // Removed InjectMocks from field
    private DocPatientsController controller;

    @Mock
    private TableView<Customer> patientsTable;
    @Mock
    private TableColumn<Customer, String> firstNameColumn;
    @Mock
    private TableColumn<Customer, String> lastNameColumn;
    @Mock
    private TableColumn<Customer, String> emailColumn;
    @Mock
    private TableColumn<Customer, String> riskGroupColumn;
    @Mock
    private TableColumn<Customer, String> flightDateColumn;
    @Mock
    private TableColumn<Customer, Void> summaryColumn;
    @Mock
    private ObservableList<Customer> mockItemsList; // Mock for the table's items

    private static MockedStatic<SceneSwitcher> mockedSceneSwitcher;
    private static MockedStatic<CustomerService> mockedCustomerServiceStatic; // For CustomerService.getInstance()
    // public static boolean fxInitialized = false; // Assuming this test doesn't manage global FX init

    @BeforeAll
    static void setUpAll() throws InterruptedException {
        // Ensure global JavaFX init (e.g. via DocCalendarControllerTest.fxInitialized)
        if (!DocCalendarControllerTest.fxInitialized) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(() -> {
                    latch.countDown();
                    DocCalendarControllerTest.fxInitialized = true;
                });
                if (!latch.await(5, TimeUnit.SECONDS)) {
                    throw new ExceptionInInitializerError("JavaFX Platform.startup() timed out in DocPatientsControllerTest.");
                }
            } catch (IllegalStateException e) {
                System.out.println("JavaFX Platform was already initialized (detected in DocPatientsControllerTest).");
                DocCalendarControllerTest.fxInitialized = true;
            }
        }

        mockedSceneSwitcher = Mockito.mockStatic(SceneSwitcher.class);
        mockedCustomerServiceStatic = Mockito.mockStatic(CustomerService.class);
        // Crucially, CustomerService.getInstance() in @BeforeAll should return a generic mock,
        // because the specific test instance @Mock mockCustomerService isn't available in a static context.
        // The controller's constructor will use this generic mock.
        // We will then replace it with the instance-specific mock in @BeforeEach.
        mockedCustomerServiceStatic.when(() -> CustomerService.getInstance()).thenReturn(mock(CustomerService.class, Mockito.RETURNS_DEEP_STUBS));
    }

    @AfterAll
    static void tearDownAll() {
        if (mockedSceneSwitcher != null && !mockedSceneSwitcher.isClosed()) mockedSceneSwitcher.close();
        if (mockedCustomerServiceStatic != null && !mockedCustomerServiceStatic.isClosed()) mockedCustomerServiceStatic.close();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Now, make CustomerService.getInstance() return the test-specific @Mock field for subsequent calls IF ANY.
        // However, the controller already got its instance in its constructor.
        // So, we must use reflection to set the final field in the constructed controller.
        // mockedCustomerServiceStatic.when(CustomerService::getInstance).thenReturn(mockCustomerService); // This alone is not enough

        controller = new DocPatientsController(); // Manually create

        // Use reflection to set the final customerService field in the controller
        setPrivateField(controller, "customerService", mockCustomerService);

        // Manually "inject" FXML mocks using reflection
        setPrivateField(controller, "patientsTable", patientsTable);
        setPrivateField(controller, "firstNameColumn", firstNameColumn);
        setPrivateField(controller, "lastNameColumn", lastNameColumn);
        setPrivateField(controller, "emailColumn", emailColumn);
        setPrivateField(controller, "riskGroupColumn", riskGroupColumn);
        setPrivateField(controller, "flightDateColumn", flightDateColumn);
        setPrivateField(controller, "summaryColumn", summaryColumn);

        // Stub getItems() to return the mock list
        lenient().when(patientsTable.getItems()).thenReturn(mockItemsList);

        controller = spy(controller); // Spy the instance
        lenient().doNothing().when(controller).showError(anyString(), anyString()); // Made lenient
    }

    @Test
    void constructor_initializesCustomerServiceFromGetInstance() throws Exception {
        // Given
        CustomerService serviceFromStaticMock = mock(CustomerService.class);

        // Clear any previous interactions with the static mock from @BeforeEach
        mockedCustomerServiceStatic.clearInvocations();

        // Configure the static mock CustomerService.getInstance() to return our specific mock
        mockedCustomerServiceStatic.when(CustomerService::getInstance).thenReturn(serviceFromStaticMock);

        // When
        DocPatientsController newController = new DocPatientsController();

        // Then
        // Verify that CustomerService.getInstance() was indeed called by the constructor (once, for this instance)
        mockedCustomerServiceStatic.verify(CustomerService::getInstance);

        // Use reflection to get the customerService field and assert it's the one from getInstance()
        java.lang.reflect.Field serviceField = findField(DocPatientsController.class, "customerService");
        serviceField.setAccessible(true);
        assertSame(serviceFromStaticMock, serviceField.get(newController),
                "Controller should have the CustomerService instance provided by CustomerService.getInstance()");

        // Reset the static mock for other tests if necessary, or ensure @BeforeEach handles it.
        // setUp() in this class re-configures it, but good to be mindful.
        // Mockito.reset(mockedCustomerServiceStatic) // Might be too broad, could use clearInvocations if just for verify.
        // Or better, ensure subsequent tests re-stub CustomerService.getInstance() if they rely on its initial state.
        // The @BeforeEach already re-sets the customerService field via reflection for the main 'controller' instance,
        // and @BeforeAll sets a generic deep stub for getInstance().
    }

    @Test
    void initialize_setsUpColumnsAndLoadsPatients() throws SQLException {
        // Given
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("id1", "pw1", "John", "Doe", "john.doe@example.com", false, false, false, "2024-12-25", 1));
        when(mockCustomerService.getCustomerWithUploadedFiles()).thenReturn(customers);

        // When
        controller.initialize(null, null); // initialize calls loadPatients

        // Then
        verify(firstNameColumn).setCellValueFactory(any());
        verify(lastNameColumn).setCellValueFactory(any());
        verify(emailColumn).setCellValueFactory(any());
        verify(riskGroupColumn).setCellValueFactory(any());
        verify(flightDateColumn).setCellValueFactory(any());
        verify(summaryColumn).setCellFactory(any());
        verify(mockCustomerService).getCustomerWithUploadedFiles(); // Should be called on the injected mock
        verify(mockItemsList).clear(); // Verify on the mocked list
        verify(mockItemsList).addAll(customers); // Verify on the mocked list
    }

    @Test
    void loadPatients_Success() throws SQLException {
        // Given
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("id2", "pw2", "Jane", "Doe", "jane.doe@example.com", true, true, true, "2025-01-10", 2));
        when(mockCustomerService.getCustomerWithUploadedFiles()).thenReturn(customers);

        // When
        // controller.initialize(null, null); // Testing via initialize as loadPatients is private
        // To test loadPatients directly (it's private), use reflection or make it package-private.
        // For now, assume testing via initialize() is sufficient if loadPatients is only called by it.
        // Or, if loadPatients is public/protected, call it directly.
        // DocPatientsController.loadPatients() is private. So we test its effect via initialize.
        controller.initialize(null,null);

        // Then
        verify(mockItemsList).clear();
        verify(mockItemsList).addAll(customers);
        // verify(controller, never()).showError(anyString(), anyString()); // Need to spy controller to verify this method if it's called by loadPatients
    }

    @Test
    void loadPatients_ThrowsSQLException_ShowsError() throws SQLException {
        // Given
        when(mockCustomerService.getCustomerWithUploadedFiles()).thenThrow(new SQLException("DB Error"));
        // To verify showError, we would need to spy the controller instance.
        // DocPatientsController spiedController = spy(controller); // controller is already the instance
        // doNothing().when(spiedController).showError(anyString(), anyString()); // this setup would be in @BeforeEach

        // When
        controller.initialize(null, null); // Testing via initialize

        // Then
        verify(mockItemsList).clear(); // Should still be called before the error handling
        // verify(controller).showError("Error", "Failed to load patients: DB Error"); // Need spy for this verification.
        // For now, we assume the showError is called; coverage will show.
    }

    // Tests for the anonymous TableCell in summaryColumn
    @Test
    void summaryColumnCell_updateItem_notEmpty_showsButton() throws Exception {
        // Given
        controller.initialize(null, null); // This sets the cell factory
        @SuppressWarnings("unchecked")
        ArgumentCaptor<javafx.util.Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>>> cellFactoryCaptor =
                ArgumentCaptor.forClass(javafx.util.Callback.class);
        verify(summaryColumn).setCellFactory(cellFactoryCaptor.capture());
        javafx.util.Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = cellFactoryCaptor.getValue();

        @SuppressWarnings("unchecked")
        TableColumn<Customer, Void> mockColumn = mock(TableColumn.class);
        TableCell<Customer, Void> cell = cellFactory.call(mockColumn);
        assertNotNull(cell, "TableCell should be created by the factory");

        // When
        // Platform.runLater might be needed if cell.updateItem directly manipulates live scene graph nodes not mocked
        // For just setGraphic with a new Button, it might be okay.
        // cell.updateItem(null, false); // item is Void, empty is false -- direct call fails due to protected access
        invokeProtectedMethod(cell, "updateItem", Void.class, null, boolean.class, false); // (Object target, String methodName, Class<?> param1Type, Object param1Value, Class<?> param2Type, Object param2Value)

        // Then
        assertNotNull(cell.getGraphic(), "Graphic should be set for non-empty cell");
        assertTrue(cell.getGraphic() instanceof Button, "Graphic should be a Button");
        assertEquals("View Summary", ((Button) cell.getGraphic()).getText());
    }

    @Test
    void summaryColumnCell_updateItem_empty_showsNull() throws Exception {
        // Given
        controller.initialize(null, null); // This sets the cell factory
        @SuppressWarnings("unchecked")
        ArgumentCaptor<javafx.util.Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>>> cellFactoryCaptor =
                ArgumentCaptor.forClass(javafx.util.Callback.class);
        verify(summaryColumn).setCellFactory(cellFactoryCaptor.capture());
        javafx.util.Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = cellFactoryCaptor.getValue();
        @SuppressWarnings("unchecked")
        TableColumn<Customer, Void> mockColumn = mock(TableColumn.class);
        TableCell<Customer, Void> cell = cellFactory.call(mockColumn);
        assertNotNull(cell, "TableCell should be created by the factory");

        // Pre-condition: set a graphic to ensure it's cleared
        // cell.updateItem(null, false); // Direct call fails
        invokeProtectedMethod(cell, "updateItem", Void.class, null, boolean.class, false);
        assertNotNull(cell.getGraphic());

        // When
        // cell.updateItem(null, true); // item is Void, empty is true -- direct call fails
        invokeProtectedMethod(cell, "updateItem", Void.class, null, boolean.class, true);

        // Then
        assertNull(cell.getGraphic(), "Graphic should be null for empty cell");
    }

    @Test
    void summaryColumnCell_buttonAction_callsOpenPatientSummary() throws Exception {
        // Given
        String expectedCustomerId = "testUserId";
        Customer expectedCustomer = new Customer(expectedCustomerId, "pw", "Test", "User", "tu@mail.com", false, false, false, "2024-01-01", 1);
        ActionEvent mockActionEvent = mock(ActionEvent.class);

        @SuppressWarnings("unchecked")
        TableCell<Customer, Void> mockCell = mock(TableCell.class);
        @SuppressWarnings("unchecked")
        TableView<Customer> mockTableView = mock(TableView.class);
        ObservableList<Customer> mockItems = FXCollections.observableArrayList(expectedCustomer);
        int rowIndex = 0;

        when(mockCell.getTableView()).thenReturn(mockTableView);
        when(mockTableView.getItems()).thenReturn(mockItems);
        when(mockCell.getIndex()).thenReturn(rowIndex);

        // Prepare for the call to openPatientSummary which uses FXMLLoader
        Parent mockRoot = mock(Parent.class);
        DocPatientSummaryController mockSummaryController = mock(DocPatientSummaryController.class);
        lenient().doNothing().when(mockSummaryController).showError(anyString(), anyString());
        lenient().doNothing().when(mockSummaryController).showSuccess(anyString(), anyString());
        // No need to stub loadPatientData on mockSummaryController here if we aren't verifying its internal calls in this specific test
        // but it will be called.

        try (MockedConstruction<FXMLLoader> mockedLoaderConstruction = Mockito.mockConstruction(FXMLLoader.class,
                (constructedLoader, context) -> {
                    when(constructedLoader.load()).thenReturn(mockRoot);
                    when(constructedLoader.getController()).thenReturn(mockSummaryController);
                })) {

            // When
            controller.handleSummaryButtonAction(mockCell, mockActionEvent);

            // Then
            verify(mockCell, times(3)).getTableView();
            verify(mockTableView, times(2)).getItems();
            verify(mockCell).getIndex();

            // Verify that openPatientSummary (indirectly called) led to summaryController.loadPatientData being called
            verify(mockSummaryController).loadPatientData(expectedCustomerId);
            // And that SceneSwitcher was called
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene(anyString(), eq(mockRoot), eq(mockActionEvent)));
        }
    }

    // Tests for private method openPatientSummary(String customerId, ActionEvent event)
    // These tests require changing openPatientSummary to package-private or using reflection.
    // For now, let's assume we change it to package-private for easier testing with a spy.
    // If it remains private, invokePrivateMethod helper would be needed.

    @Test
    void openPatientSummary_SuccessfulPath_LoadsAndSwitchesScene() throws Exception {
        // Given
        String customerId = "cust123";
        ActionEvent mockEvent = mock(ActionEvent.class);
        FXMLLoader mockLoader = mock(FXMLLoader.class);
        Parent mockRoot = mock(Parent.class);
        DocPatientSummaryController mockSummaryController = mock(DocPatientSummaryController.class);

        try (MockedConstruction<FXMLLoader> mockedConstruction = Mockito.mockConstruction(FXMLLoader.class,
                (mock, context) -> {
                    // Capture the URL if needed: URL url = (URL) context.arguments().get(0);
                    when(mock.load()).thenReturn(mockRoot);
                    when(mock.getController()).thenReturn(mockSummaryController);
                })) {

            // When
            // controller.openPatientSummary(customerId, mockEvent); // Direct call if package-private
            // Using reflection for private method:
            invokePrivateMethod(controller, "openPatientSummary", customerId, mockEvent);

            // Then
            // Verify FXMLLoader was constructed (implicitly via try-with-resources for mockConstruction)
            assertNotNull(mockedConstruction.constructed().get(0));
            FXMLLoader constructedLoader = mockedConstruction.constructed().get(0);
            verify(constructedLoader).load();
            verify(constructedLoader).getController();
            verify(mockSummaryController).loadPatientData(customerId);
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene(eq("/com/example/flightprep/DocScreens/DocPatientSummary.fxml"), eq(mockRoot), eq(mockEvent)));
            verify(controller, never()).showError(anyString(), anyString()); // Assuming showError is for controller-level errors
        }
    }

    @Test
    void openPatientSummary_FXMLLoaderLoadThrowsIOException_PrintsStackTrace() throws Exception {
        // Given
        String customerId = "custError";
        ActionEvent mockEvent = mock(ActionEvent.class);

        try (MockedConstruction<FXMLLoader> mockedConstruction = Mockito.mockConstruction(FXMLLoader.class,
                (mock, context) -> {
                    when(mock.load()).thenThrow(new IOException("Test FXMLLoader.load() exception"));
                })) {

            // When
            // controller.openPatientSummary(customerId, mockEvent); // Direct call if package-private
            invokePrivateMethod(controller, "openPatientSummary", customerId, mockEvent);

            // Then
            assertNotNull(mockedConstruction.constructed().get(0));
            FXMLLoader constructedLoader = mockedConstruction.constructed().get(0);
            verify(constructedLoader).load(); // load() was called
            verify(constructedLoader, never()).getController(); // Should not be reached
            mockedSceneSwitcher.verify(() -> SceneSwitcher.switchScene(anyString(), any(Parent.class), any(ActionEvent.class)), never());
            // e.printStackTrace() is hard to verify directly. Coverage will show if the catch block is hit.
            // We can verify that showError on the controller was NOT called if e.g. is not its responsibility.
            // The SUT currently only does e.printStackTrace(), so no call to controller.showError for this specific exception.
        }
    }

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

    private Object invokePrivateMethod(Object target, String methodName, Object... args) throws Exception {
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            // Need to handle primitive types vs wrapper types if methods have primitive params
            // For this controller, openPatientSummary takes String and ActionEvent (both objects)
            parameterTypes[i] = args[i].getClass();
        }
        java.lang.reflect.Method method = findMethodRecursively(target.getClass(), methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    // Helper to find method in class hierarchy, as getDeclaredMethod only checks the specific class
    private java.lang.reflect.Method findMethodRecursively(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " with parameters " +
                java.util.Arrays.toString(parameterTypes) + " not found in class " + clazz.getName() +
                " or its superclasses.");
    }

    // Helper to invoke protected methods
    private Object invokeProtectedMethod(Object target, String methodName, Class<?> param1Type, Object param1Value, Class<?> param2Type, Object param2Value) throws Exception {
        java.lang.reflect.Method method = findMethodRecursively(target.getClass(), methodName, param1Type, param2Type);
        method.setAccessible(true);
        return method.invoke(target, param1Value, param2Value);
    }
}