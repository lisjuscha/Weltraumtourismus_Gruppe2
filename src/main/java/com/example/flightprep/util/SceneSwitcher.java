package com.example.flightprep.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {
    public static void switchScene(String fxmlFile, ActionEvent event, boolean fullscreen) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlFile));
        Parent newRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene newScene = new Scene(newRoot, stage.getWidth(), stage.getHeight());

        newScene.getStylesheets().add(SceneSwitcher.class
                .getResource("/com/example/flightprep/Stylesheets/Prep.css").toExternalForm());

        stage.setScene(newScene);
        if (fullscreen) {
            stage.setFullScreen(true);
        }
    }
}
