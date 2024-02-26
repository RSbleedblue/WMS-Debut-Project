package org.wms;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.wms.utils.DatabaseConnection;

public class RegisterController implements Initializable {
    // Logger for logging messages
    private final Logger logger = LogManager.getLogger(RegisterController.class);

    // Database connection
    private Connection connectionDB;

    // Constructor
    public RegisterController() {
        // Establish database connection
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }

    // FXML elements
    @FXML
    private TextField reg_Fname;
    @FXML
    private TextField reg_Lname;
    @FXML
    private TextField reg_Uname;
    @FXML
    private PasswordField reg_password;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private Button cancelButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Set branding image
            File fileshield = new File("images/WMSLoginPage.png");
            Image shield = new Image(fileshield.toURI().toString());
            brandingImageView.setImage(shield);
            logger.info("Registration form initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing registration form", e);
        }
    }

    // Custom exceptions for registration and account
    public class RegisterException extends Exception {
        public RegisterException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class AccountException extends Exception {
        public AccountException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Method to register user
    public void register() throws RegisterException, AccountException, SQLException, IOException {
        // Check if email is already registered
        if (isRegisteredEmail(reg_Uname.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setTitle(null);
            alert.setContentText("Already Registered Please Login");
            alert.showAndWait();
            returnBack();
            return;
        }

        String registerQuery = "INSERT INTO user_account (firstname, lastname, username, password, isAdmin) VALUES (?, ?, ?, ?, 0)";

        try {
            // Check if required fields are empty
            if (reg_Fname.getText().isEmpty() || reg_Lname.getText().isEmpty() || reg_Uname.getText().isEmpty()
                    || reg_password.getText().isEmpty()) {
                // Prompt text in the respective fields
                reg_Fname.setStyle("-fx-prompt-text-fill: red;");
                reg_Lname.setStyle("-fx-prompt-text-fill: red;");
                reg_Uname.setStyle("-fx-prompt-text-fill: red;");
                reg_password.setStyle("-fx-prompt-text-fill: red;");
                reg_Fname.setPromptText("First name required");
                reg_Lname.setPromptText("Last name required");
                reg_Uname.setPromptText("Email required");
                reg_password.setPromptText("Password required");
                return;
            }

            // Validate email format
            if (!isValidEmail(reg_Uname.getText())) {
                // Show error message or handle invalid email
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Email format");
                alert.showAndWait();
                throw new RegisterException("Invalid Email Format", null);
            }
            // Hash the password before storing it
            String hashedPassword = BCrypt.hashpw(reg_password.getText(), BCrypt.gensalt());

            // Prepare statement for registration
            PreparedStatement preparedStatement = connectionDB.prepareStatement(registerQuery);

            preparedStatement.setString(1, reg_Fname.getText());
            preparedStatement.setString(2, reg_Lname.getText());
            preparedStatement.setString(3, reg_Uname.getText());
            preparedStatement.setString(4, hashedPassword);
//            preparedStatement.setBoolean(5, isAdminValue);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            logger.info("User registered successfully");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Account");
            alert.setContentText("Account Successfully Created");
            alert.showAndWait();

            returnBack();
        } catch (SQLException e) {
            logger.error("SQL error occurred while registering", e);
            throw new RegisterException("SQL error occurred while registering", e);
        } catch (IOException e) {
            logger.error("IO error occurred while registering", e);
            throw new RegisterException("IO error occurred while registering", e);
        }
    }

    // Method to check if email is already registered
    private boolean isRegisteredEmail(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM user_account WHERE username = ?";
        try (PreparedStatement preparedStatement = connectionDB.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // If count > 0, email is registered
                }
            }
        }
        return false;
    }

    // Method to navigate back to login view
    public void returnBack() throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        logger.debug("Returning back to login view after registration");
    }

    // Method to validate email format
    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }
}
