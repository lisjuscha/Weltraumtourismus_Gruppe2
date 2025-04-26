package com.example.flightprep.controller.Customer;

import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerHomeController {
    @FXML
    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    public void switchToCalendar(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendarHome.fxml", event);
    }
    @FXML
    public void switchToPrep(ActionEvent event) throws IOException {
        SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPreparation.fxml", event);
    }
}