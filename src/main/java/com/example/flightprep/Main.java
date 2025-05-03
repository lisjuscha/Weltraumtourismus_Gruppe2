package com.example.flightprep;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flightprep/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
         scene.getStylesheets().add(getClass().getResource("/com/example/flightprep/Stylesheets/Prep.css").toExternalForm());
        stage.setTitle("Flight Preparation");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}