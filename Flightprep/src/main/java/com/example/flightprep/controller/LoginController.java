package com.example.flightprep.controller;

import com.example.flightprep.service.UserService;
import com.example.flightprep.users.Doctor;
import com.example.flightprep.users.User;
import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private final UserService userService = new UserService();

    @FXML
    private ImageView loginImageView;
    @FXML
    private TextField user_idInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        Image loginImage = new Image(getClass().getResource("/images/LoginScreen.png").toExternalForm());
        loginImageView.setImage(loginImage);
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        String ressource = "";
        String user_id = user_idInput.getText();
        String password = passwordInput.getText();

        User user = userService.authenticateUser(user_id, password);
        if (user != null) {
            if (user instanceof Doctor) {
                ressource = "/com/example/flightprep/DocScreens/DocHome.fxml";
            } else {
                ressource = "/com/example/flightprep/CustomerScreens/CustomerHome.fxml";
            }

            try {
                SceneSwitcher.switchScene(ressource, event);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error loading the screen.");
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.setText("Username or Password incorrect.");
            errorLabel.setVisible(true);
        }
    }
}