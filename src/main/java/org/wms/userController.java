package org.wms;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class userController implements Initializable {
    @FXML
    private ComboBox<String> bedQuality;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        bedQuality.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
    }
}
