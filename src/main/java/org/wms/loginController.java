package org.wms;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class loginController implements Initializable  {
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
    public void initialize(URL url, ResourceBundle resourceBundle){
        File brandingFile = new File("images/WMSLoginPage.png");
        Image branding = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(branding);
    }
    private void switchScene(Scene newScene) {
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
    }

    public void loginButtonOnAction(ActionEvent event){
        if(!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()){
            validateLogin();
        }
        else{
            loginMessageLabel.setText("Invalid User");
        }
    }
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    public void validateLogin() {
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
                    } else {
                        userSwitchLoad();
                    }
                    return; // Exit the method if login is successful
                }
            }
            if (!userFound) {
                loginMessageLabel.setText("User not found"); // User not found
            } else {
                loginMessageLabel.setText("Invalid Password"); // Invalid password
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void registerSwitchLoad(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registerView.fxml"));
        switchScene(new Scene(fxmlLoader.load()));
    }

    private void userSwitchLoad() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userView.fxml"));
            switchScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void adminSwitchLoad() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adminView.fxml"));
            switchScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}