package org.wms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("loginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            displayError("Error loading loginView.fxml", e);
        } catch (Exception e) {
            displayError("An unexpected error occurred", e);
        }
    }

    private void displayError(String message, Exception e) {
        // You can customize how you want to handle/display the error message here
        System.err.println(message);
        e.printStackTrace();
    }
    public static void main(String[] args) {
        launch();
    }
}