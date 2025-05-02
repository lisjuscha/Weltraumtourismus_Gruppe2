package com.example.flightprep.util;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SceneSwitcher {
    // Static method to switch scenes
    public static void switchScene(String fxmlFile, ActionEvent event, boolean fullscreen) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlFile));
        Parent newRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene newScene = new Scene(newRoot, stage.getWidth(), stage.getHeight());

        newScene.getStylesheets().add(SceneSwitcher.class
                .getResource("/com/example/flightprep/Stylesheets/Prep.css").toExternalForm());

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            stage.setScene(newScene);
            stage.setFullScreen(fullscreen);  // <<<<< Fullscreen abhängig vom Parameter
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
}
