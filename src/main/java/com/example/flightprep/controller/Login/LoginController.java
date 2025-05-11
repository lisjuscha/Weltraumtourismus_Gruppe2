package com.example.flightprep.controller.Login;

import com.example.flightprep.service.UserService;
import com.example.flightprep.model.User;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
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
    private BorderPane borderPane;

    @FXML
    public void initialize() {
        Image loginImage = new Image(getClass().getResource("/images/LoginScreen.png").toExternalForm());
        loginImageView.setImage(loginImage);
        loginImageView.fitHeightProperty().bind(borderPane.heightProperty());
        loginImageView.fitWidthProperty().bind(borderPane.widthProperty().multiply(0.8)); // 80% der Breite
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        String ressource = "";
        String userId = user_idInput.getText();
        String password = passwordInput.getText();

        UserService userService = new UserService();
        User user = userService.authenticateUser(userId, password);


        // Factory or Strategy
        if (user != null) {
            SessionManager.setCurrentUser(user);
            if (user.getRole() == "doctor") {
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