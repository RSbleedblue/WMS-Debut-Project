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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Define a custom exception class for handling redirection errors
class RedirectionException extends Exception {
    public RedirectionException(String message) {
        super(message);
    }
}

// Define a custom exception class for handling close errors
class CloseException extends Exception {
    public CloseException(String message) {
        super(message);
    }
}

public class thankyouController implements Initializable {
    private static final Logger logger = LogManager.getLogger(thankyouController.class);

    @FXML
    private Button redirectBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private ImageView thanksPageLogo;
    @FXML
    private ImageView wmsLogoThanks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            File thanksLoc = new File("images/thanks.png");
            Image thanksIMG = new Image(thanksLoc.toURI().toString());
            thanksPageLogo.setImage(thanksIMG);

            File wmsLogoLoc = new File("images/WMSLoginPage.png");
            Image logoIMG = new Image(wmsLogoLoc.toURI().toString());
            wmsLogoThanks.setImage(logoIMG);

            logger.info("Thank you page initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing thank you page", e);
        }
    }

    @FXML
    public void redirect(ActionEvent e) throws RedirectionException {
        try {
            Stage stage = (Stage) redirectBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userView.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            logger.info("Redirecting to user view");
        } catch (IOException ex) {
            logger.error("Error redirecting to user view", ex);
            throw new RedirectionException("Error redirecting to user view");
        }
    }

    @FXML
    public void close(ActionEvent e) throws CloseException {
        try {
            Stage stage = (Stage) closeBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loginView.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            logger.info("Closing the window and returning to login view");
        } catch (IOException ex) {
            logger.error("Error closing the window", ex);
            throw new CloseException("Error closing the window");
        }
    }
}
