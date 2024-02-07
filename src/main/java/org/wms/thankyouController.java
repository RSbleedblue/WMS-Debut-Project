package org.wms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class thankyouController {
    @FXML
    private Button redirectBtn;
    @FXML
    private Button closeBtn;

    @FXML
    public void redirect(ActionEvent e) {
        try {
            Stage stage = (Stage) redirectBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userView.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace(); // Handle or log the exception
        }
    }

    @FXML
    public void close(ActionEvent e) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
