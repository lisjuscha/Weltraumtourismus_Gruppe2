package com.example.flightprep.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {
    // Static method to switch scenes
    public static void switchScene(String fxmlFile, javafx.event.ActionEvent event) throws IOException {
        // Load the new scene from the FXML file
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlFile));
        // Load the new scene from the FXML file
        Parent root =  loader.load();
        // Get the current stage (window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Create a new scene and set it on the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
