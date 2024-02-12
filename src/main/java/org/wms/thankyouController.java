package org.wms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class thankyouController implements Initializable {
    @FXML
    private Button redirectBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private ImageView thanksPageLogo;
    @FXML
    private ImageView wmsLogoThanks;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File thanksLoc = new File("images/thanks.png");
        Image thanksIMG = new Image(thanksLoc.toURI().toString());
        thanksPageLogo.setImage(thanksIMG);

        File wmsLogoLoc = new File("images/WMSLoginPage.png");
        Image logoIMG = new Image(wmsLogoLoc.toURI().toString());
        wmsLogoThanks.setImage(logoIMG);
    }

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
    public void close(ActionEvent e) throws IOException {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
    }
}
