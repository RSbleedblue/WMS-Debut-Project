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

    // Establishes a connection to the database upon instantiation of the class
    private Connection connectionDB;
    public loginController(){
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }

    // Logger for logging events in the class
    private final Logger logger = LogManager.getLogger(loginController.class);

    // FXML fields
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

    // Initializes the controller upon loading the FXML file
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Loads and sets an image to the brandingImageView
            File brandingFile = new File("images/WMSLoginPage.png");
            Image branding = new Image(brandingFile.toURI().toString());
            brandingImageView.setImage(branding);
            logger.info("Loading Login Page"); // Logs the successful loading of the login page
        } catch (Exception e) {
            logger.error("Error loading Login Page", e); // Logs any error occurred during loading the login page
        }
    }

    // Custom runtime exception class for database query exceptions
    public class DatabaseQueryException extends RuntimeException {
        public DatabaseQueryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Custom checked exception class for view loading exceptions
    public class ViewLoadException extends Exception {
        public ViewLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Method to switch the scene to a new one with a fade transition effect
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
        logger.debug("Switching scene"); // Logs the switching of the scene
    }

    // Handles the login button click event
    @FXML
    public void loginButtonOnAction(ActionEvent event) {
        // Checks if username and password fields are not empty
        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            validateLogin(); // Calls the method to validate the login
        } else if (!usernameTextField.getText().isBlank() && enterPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("Password is Empty");
            logger.warn("Password is Empty"); // Logs a warning if the password is empty
        }
        else if (usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("User name is Empty");
            logger.warn("User name is Empty"); // Logs a warning if the username is empty
        }
        else{
            loginMessageLabel.setText("Please Enter the details");
            logger.warn("Empty user login attempt"); // Logs a warning if the user login attempt is invalid
        }
    }

    // Handles the cancel button click event
    @FXML
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        logger.info("Application closed"); // Logs the closure of the application
    }

    // Validates the login credentials entered by the user
    private void validateLogin() {
        // SQL query to retrieve the stored hashed password for the entered username
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
                        adminSwitchLoad(); // Switches to admin view if the user is an admin
                        logger.info("Admin logged in"); // Logs the successful admin login
                    } else {
                        userSwitchLoad(); // Switches to user view if the user is not an admin
                        logger.info("User logged in"); // Logs the successful user login
                    }
                    return; // Exit the method if login is successful
                }
            }
            if (!userFound) {
                loginMessageLabel.setText("User not found"); // User not found
                logger.warn("User not found during login attempt"); // Logs a warning if user not found
            } else {
                loginMessageLabel.setText("Invalid Password"); // Invalid password
                logger.warn("Invalid password during login attempt"); // Logs a warning if invalid password
            }
        } catch (Exception e) {
            logger.error("Error occurs while validating user", e);
            throw new DatabaseQueryException("Error occurs while validating user", e);
        }
    }

    // Handles the action when the user clicks the register button to switch to the register view
    @FXML
    public void registerSwitchLoad(ActionEvent event) throws ViewLoadException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registerView.fxml"));
        try {
            switchScene(new Scene(fxmlLoader.load())); // Switches to the register view
            logger.info("Switched to register view"); // Logs the successful switch to register view
        } catch (IOException e) {
            logger.error("Error loading register view", e); // Logs an error if loading register view fails
            throw new ViewLoadException("Error loading register view", e);
        }
    }

    // Method to get the first name of the user from the database based on the username
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

    // Method to switch to the user view after successful user login
    private void userSwitchLoad() throws ViewLoadException, SQLException {
        try {
            // Passes the first name of the user to the user controller
            userController userController = new userController();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userView.fxml"));
            fxmlLoader.setController(userController);
            switchScene(new Scene(fxmlLoader.load())); // Switches to the user view
            logger.info("Switched to user view"); // Logs the successful switch to user view
        } catch (IOException e) {
            logger.error("Error loading user view", e); // Logs an error if loading user view fails
            throw new ViewLoadException("Error loading user view", e);
        }
    }

    // Method to switch to the admin view after successful admin login
    private void adminSwitchLoad() throws ViewLoadException, SQLException {
        try {
            String userName = getName(usernameTextField.getText()); // Get the username
            adminController adminController = new adminController(userName); // Pass the username
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adminView.fxml"));
            fxmlLoader.setController(adminController);
            switchScene(new Scene(fxmlLoader.load())); // Switches to the admin view
            logger.info("Switched to admin view"); // Logs the successful switch to admin view
        } catch (IOException e) {
            logger.error("Error while Switching to admin",e); // Logs an error if switching to admin fails
            throw new ViewLoadException("Error loading admin view", e);
        }
    }
}
