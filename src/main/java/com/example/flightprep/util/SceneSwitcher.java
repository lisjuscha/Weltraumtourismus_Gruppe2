package com.example.flightprep.util;

import com.example.flightprep.Main;
import com.example.flightprep.controller.BasicController.DocController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchScene(String fxmlFile, ActionEvent event) throws IOException {
        try {
            Parent newRoot = FXMLLoader.load(Main.class.getResource(fxmlFile));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(newRoot);
            String cssPath = "/com/example/flightprep/Stylesheets/Prep.css";
            scene.getStylesheets().add(Main.class.getResource(cssPath).toExternalForm());
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void switchScene(String fxmlFile, Scene scene) throws IOException {
        try {
            Parent newRoot = FXMLLoader.load(Main.class.getResource(fxmlFile));
            scene.setRoot(newRoot);
            String cssPath = "/com/example/flightprep/Stylesheets/Prep.css";
            scene.getStylesheets().add(Main.class.getResource(cssPath).toExternalForm());
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void switchScene(String fxmlFile, Parent root, ActionEvent event) throws IOException {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/com/example/flightprep/Stylesheets/Prep.css").toExternalForm());
            stage.setScene(scene);
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    }
