package com.example.flightprep.util;

import com.example.flightprep.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {
    public static void switchScene(String fxmlFile, ActionEvent event, boolean fullscreen) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(fxmlFile));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found!");
            }
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Korrigierter Stylesheet-Pfad
            String cssPath = "/com/example/flightprep/Stylesheets/Prep.css";
            scene.getStylesheets().add(Main.class.getResource(cssPath).toExternalForm());

            stage.setTitle("Flight Preparation");
            stage.setScene(scene);
            stage.setFullScreen(fullscreen);  // Verwendet den Parameter
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}