package com.example.flightprep.controller.Doctor;

import com.example.flightprep.controller.BasicController.DocController;
import com.example.flightprep.model.Customer;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DocPatientsController extends DocController implements Initializable {
    @FXML private TableView<Customer> patientsTable;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> riskGroupColumn;
    @FXML private TableColumn<Customer, String> flightDateColumn;
    @FXML private TableColumn<Customer, Void> summaryColumn;

    private final CustomerService customerService;

    public DocPatientsController() {
        this.customerService = CustomerService.getInstance();
    }

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
                    Customer customer = getTableView().getItems().get(getIndex());
                    openPatientSummary(customer.getUserId(), event);
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

    private void openPatientSummary(String customerId, javafx.event.ActionEvent event) {
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

    private void loadPatients() {
        patientsTable.getItems().clear();
        try {
            patientsTable.getItems().addAll(customerService.getCustomerWithUploadedFiles());
        } catch (SQLException e) {
            showError("Error", "Failed to load patients: " + e.getMessage());
        }
    }
}