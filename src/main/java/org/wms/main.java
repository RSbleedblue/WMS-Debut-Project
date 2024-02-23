package org.wms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class main extends Application {

    private final Logger logger = LogManager.getLogger(main.class);

    @Override
    public void start(Stage stage) {
        try {
            logger.info("Starting the application...");
            FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("loginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
            logger.info("Application started successfully.");
        } catch (IOException e) {
            logger.error("Error loading loginView.fxml", e);
            displayError("Error loading loginView.fxml", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            displayError("An unexpected error occurred", e);
        }
    }

    private void displayError(String message, Exception e) {
        // You can customize how you want to handle/display the error message here
        System.err.println(message);
        e.printStackTrace();
    }

    public static void main(String[] args) {
        // Configure Log4j
       System.setProperty("log4j.configurationFile", "src/main/resources/log4j2.xml");

        // Launch the JavaFX application
        launch();
    }

}