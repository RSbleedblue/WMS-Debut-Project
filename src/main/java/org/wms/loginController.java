package org.wms;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.wms.utils.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class loginController implements Initializable {
    private Connection connectionDB;
    public loginController(){
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }

    private final Logger logger = LogManager.getLogger(loginController.class);

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;
    @FXML
    private Button userSignup;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            File brandingFile = new File("images/WMSLoginPage.png");
            Image branding = new Image(brandingFile.toURI().toString());
            brandingImageView.setImage(branding);
            logger.info("Loading Login Page");
        } catch (Exception e) {
            logger.error("Error loading Login Page", e);
        }
    }
    public class DatabaseQueryException extends RuntimeException {
        public DatabaseQueryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class ViewLoadException extends Exception {
        public ViewLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void switchScene(Scene newScene) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> {
            stage.setScene(newScene);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), stage.getScene().getRoot());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
        logger.debug("Switching scene");
    }

    @FXML
    public void loginButtonOnAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            validateLogin();
        } else if (!usernameTextField.getText().isBlank() && enterPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("Password is Empty");
            logger.warn("Password is Empty");
        }
        else if (usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("User name is Empty");
            logger.warn("User name is Empty");
        }
        else{
            loginMessageLabel.setText("Invalid User");
            logger.warn("Invalid user login attempt");
        }
    }

    @FXML
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        logger.info("Application closed");
    }

    private void validateLogin() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectionDB = connection.getConnection();

        String verifyLogin = "SELECT password, isAdmin FROM user_account WHERE username = '"
                + usernameTextField.getText() + "'";

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);
            boolean userFound = false; // Flag to check if user exists
            while (queryResult.next()) {
                userFound = true; // Set the flag to true since user exists
                String storedHashedPassword = queryResult.getString("password");
                boolean isAdmin = queryResult.getBoolean("isAdmin");

                // Check if the entered password matches the stored hashed password
                if (BCrypt.checkpw(enterPasswordField.getText(), storedHashedPassword)) {
                    if (isAdmin) {
                        adminSwitchLoad();
                        logger.info("Admin logged in");
                    } else {
                        userSwitchLoad();
                        logger.info("User logged in");
                    }
                    return; // Exit the method if login is successful
                }
            }
            if (!userFound) {
                loginMessageLabel.setText("User not found"); // User not found
                logger.warn("User not found during login attempt");
            } else {
                loginMessageLabel.setText("Invalid Password"); // Invalid password
                logger.warn("Invalid password during login attempt");
            }
        } catch (Exception e) {
            logger.error("Error occurs while validating user", e);
            throw new DatabaseQueryException("Error occurs while validating user", e);
        }
    }

    @FXML
    public void registerSwitchLoad(ActionEvent event) throws ViewLoadException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registerView.fxml"));
        try {
            switchScene(new Scene(fxmlLoader.load()));
            logger.info("Switched to register view");
        } catch (IOException e) {
            logger.error("Error loading register view", e);
            throw new ViewLoadException("Error loading register view", e);
        }
    }
    private String getName(String userName) throws SQLException {
        String query = "Select firstname from user_account where username = ?";
        PreparedStatement statement = connectionDB.prepareStatement(query);
        statement.setString(1,userName);
        ResultSet result = statement.executeQuery();
        String firstName = "";
        if(result.next()){
            firstName = result.getString("firstname");
        }
        return firstName;
    }

    private void userSwitchLoad() throws ViewLoadException, SQLException {
        try {
            userController userController = new userController(getName(usernameTextField.getText()));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userView.fxml"));
            fxmlLoader.setController(userController);
            switchScene(new Scene(fxmlLoader.load()));
            logger.info("Switched to user view");
        } catch (IOException e) {
            logger.error("Error loading user view", e);
            throw new ViewLoadException("Error loading user view", e);
        }
    }

    private void adminSwitchLoad() throws ViewLoadException, SQLException {
        try {
            String userName = getName(usernameTextField.getText()); // Get the username
            adminController adminController = new adminController(userName); // Pass the username
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adminView.fxml"));
            fxmlLoader.setController(adminController);
            switchScene(new Scene(fxmlLoader.load()));
            logger.info("Switched to admin view");
        } catch (IOException e) {
            logger.error("Error while Switching to admin",e);
            throw new ViewLoadException("Error loading admin view", e);
        }
    }
}