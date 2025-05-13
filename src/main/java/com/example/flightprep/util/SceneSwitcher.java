package com.example.flightprep.util;

import com.example.flightprep.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The `SceneSwitcher` class provides utility methods for switching between different scenes
 * in a JavaFX application. It supports loading FXML files, applying stylesheets, and updating
 * the scene or stage as needed.
 */
public class SceneSwitcher {

    /**
     * Switches the current scene to a new one specified by the FXML file.
     *
     * @param fxmlFile The path to the FXML file for the new scene.
     * @param event    The `ActionEvent` that triggered the scene switch.
     * @throws IOException If an error occurs while loading the FXML file.
     */
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

    /**
     * Switches the current scene to a new one specified by the FXML file, using an existing `Scene` object.
     *
     * @param fxmlFile The path to the FXML file for the new scene.
     * @param scene    The existing `Scene` object to update.
     * @throws IOException If an error occurs while loading the FXML file.
     */
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

    /**
     * Switches the current scene to a new one specified by the FXML file and a provided `Parent` root.
     *
     * @param fxmlFile The path to the FXML file for the new scene.
     * @param root     The `Parent` root node for the new scene.
     * @param event    The `ActionEvent` that triggered the scene switch.
     * @throws IOException If an error occurs while loading the FXML file.
     */
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
