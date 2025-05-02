package com.example.flightprep.controller;

import com.example.flightprep.service.UserService;
import com.example.flightprep.model.Doctor;
import com.example.flightprep.model.User;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    private Stage stage;
    private Scene scene;
    private Parent root;


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
        String userId = user_idInput.getText();
        String password = passwordInput.getText();

        UserService userService = new UserService();
        User user = userService.authenticateUser(userId, password);

        if (user != null) {
            SessionManager.setCurrentUser(user);
            if (user instanceof Doctor) {
                ressource = "/com/example/flightprep/DocScreens/DocHome.fxml";
            } else {
                ressource = "/com/example/flightprep/CustomerScreens/CustomerHome.fxml";
            }

            try {
                SceneSwitcher.switchScene(ressource, event, true);
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