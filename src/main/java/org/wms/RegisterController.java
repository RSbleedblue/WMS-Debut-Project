package org.wms;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController implements Initializable {
    private final Logger logger = LogManager.getLogger(RegisterController.class);

    private Connection connectionDB;

    public RegisterController() {
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }

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
    @FXML
    private ComboBox<String> isAdmin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            File fileshield = new File("images/WMSLoginPage.png");
            Image shield = new Image(fileshield.toURI().toString());
            brandingImageView.setImage(shield);

            isAdmin.setItems(FXCollections.observableArrayList("Admin", "User"));
            logger.info("Registration form initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing registration form", e);
        }
    }

    public class RegisterException extends Exception {
        public RegisterException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void register() throws RegisterException {
        String registerQuery = "INSERT INTO user_account (firstname, lastname, username, password, isAdmin) VALUES (?, ?, ?, ?, ?)";

        try {
            if (reg_Fname.getText().isEmpty() || reg_Lname.getText().isEmpty() || reg_Uname.getText().isEmpty()
                    || reg_password.getText().isEmpty()) {
                // Prompt text in the respective fields
                reg_Fname.setPromptText("First name required");
                reg_Lname.setPromptText("Last name required");
                reg_Uname.setPromptText("Email required");
                reg_password.setPromptText("Password required");
                return;
            }

            // Validate email format using the username field
            if (!isValidEmail(reg_Uname.getText())) {
                // Show error message or handle invalid email
                throw new RegisterException("Invalid email address", null);
            }

            boolean isAdminValue = isAdmin.getValue().equals("Admin");

            // Hash the password before storing it
            String hashedPassword = BCrypt.hashpw(reg_password.getText(), BCrypt.gensalt());

            PreparedStatement preparedStatement = connectionDB.prepareStatement(registerQuery);

            preparedStatement.setString(1, reg_Fname.getText());
            preparedStatement.setString(2, reg_Lname.getText());
            preparedStatement.setString(3, reg_Uname.getText());
            preparedStatement.setString(4, hashedPassword);
            preparedStatement.setBoolean(5, isAdminValue);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            logger.info("User registered successfully");
            returnBack();
        } catch (SQLException e) {
            logger.error("SQL error occurred while registering", e);
            throw new RegisterException("SQL error occurred while registering", e);
        } catch (IOException e) {
            logger.error("IO error occurred while registering", e);
            throw new RegisterException("IO error occurred while registering", e);
        }
    }

    public void returnBack() throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        logger.debug("Returning back to login view after registration");
    }

    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }
}
