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

public class RegisterController implements Initializable {
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
    private ImageView shieldImage;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> isAdmin;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File fileshield = new File("images/registerShield.jpg");
        Image shield = new Image(fileshield.toURI().toString());
        shieldImage.setImage(shield);

        isAdmin.setItems(FXCollections.observableArrayList("admin","user"));
    }

    public void register() {
        String registerQuery = "INSERT INTO user_account (firstname, lastname, username, password, isAdmin) VALUES (?, ?, ?, ?, ?)";

        try {
            if (reg_Fname.getText().isEmpty() || reg_Lname.getText().isEmpty() || reg_Uname.getText().isEmpty() || reg_password.getText().isEmpty()) {
                // Prompt text in the respective fields
                reg_Fname.setPromptText("First name required");
                reg_Lname.setPromptText("Last name required");
                reg_Uname.setPromptText("Username required");
                reg_password.setPromptText("Password required");
            }

            boolean isAdminValue = isAdmin.getValue().equals("admin");

            PreparedStatement preparedStatement = connectionDB.prepareStatement(registerQuery);

            preparedStatement.setString(1, reg_Fname.getText());
            preparedStatement.setString(2, reg_Lname.getText());
            preparedStatement.setString(3, reg_Uname.getText());
            preparedStatement.setString(4, reg_password.getText());
            preparedStatement.setBoolean(5, isAdminValue);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            returnBack();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void closeConnection() {
        try {
            if (connectionDB != null && !connectionDB.isClosed()) {
                connectionDB.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void returnBack() throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
}
