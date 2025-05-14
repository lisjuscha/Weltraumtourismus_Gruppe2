package com.example.flightprep.controller.Doctor;

import com.example.flightprep.controller.BasicController.DocController;
import com.example.flightprep.model.Customer;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * The `DocPatientsController` class manages the patient list view for doctors in the application.
 * It displays a table of patients with their details and provides functionality to view individual
 * patient summaries. This class extends `DocController` and implements `Initializable`.
 */
public class DocPatientsController extends DocController implements Initializable {
    @FXML private TableView<Customer> patientsTable;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> riskGroupColumn;
    @FXML private TableColumn<Customer, String> flightDateColumn;
    @FXML private TableColumn<Customer, Void> summaryColumn;

    private final CustomerService customerService;

    /**
     * Constructs a new `DocPatientsController` and initializes the `CustomerService` instance.
     */
    public DocPatientsController() {
        this.customerService = CustomerService.getInstance();
    }

    /**
     * Initializes the patient list view by setting up table columns and loading patient data.
     * This method is called automatically after the FXML file has been loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if not known.
     * @param resourceBundle The resources used to localize the root object, or null if not specified.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        riskGroupColumn.setCellValueFactory(new PropertyValueFactory<>("riskGroup"));
        flightDateColumn.setCellValueFactory(new PropertyValueFactory<>("flightDate"));

        summaryColumn.setCellFactory(col -> new TableCell<Customer, Void>() {
            private final Button button = new Button("View Summary");
            {
                button.getStyleClass().add("view-details-button");
                button.setOnAction(event -> {
                    handleSummaryButtonAction(this, event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        loadPatients();
    }

    /**
     * Handles the action for the summary button in a table cell.
     * Retrieves the customer for the cell and opens their summary view.
     *
     * @param cell The table cell where the action originated.
     * @param event The action event.
     */
    void handleSummaryButtonAction(TableCell<Customer, Void> cell, ActionEvent event) {
        if (cell != null && cell.getTableView() != null && cell.getTableView().getItems() != null) {
            Customer customer = cell.getTableView().getItems().get(cell.getIndex());
            if (customer != null) {
                openPatientSummary(customer.getUserId(), event);
            }
        } else {
            // Handle cases where cell, tableview or items might be null, perhaps log or show an error
            System.err.println("Could not retrieve customer from cell context for summary action.");
        }
    }

    /**
     * Opens the summary view for a specific patient.
     *
     * @param customerId The ID of the customer whose summary is to be opened.
     * @param event The `ActionEvent` triggered by the button click.
     */
    private void openPatientSummary(String customerId, ActionEvent event) {
        try {
            String fxmlFile = "/com/example/flightprep/DocScreens/DocPatientSummary.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            DocPatientSummaryController controller = loader.getController();
            controller.loadPatientData(customerId);

            SceneSwitcher.switchScene(fxmlFile, root, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of patients from the database and populates the table view.
     * Displays an error message if the data cannot be loaded.
     */
    private void loadPatients() {
        patientsTable.getItems().clear();
        try {
            patientsTable.getItems().addAll(customerService.getCustomerWithUploadedFiles());
        } catch (SQLException e) {
            showError("Error", "Failed to load patients: " + e.getMessage());
        }
    }
}