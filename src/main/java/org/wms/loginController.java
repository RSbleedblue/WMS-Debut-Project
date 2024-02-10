package org.wms;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.w3c.dom.events.MouseEvent;
import org.wms.utils.SceneUtil;

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
        File brandingFile = new File("images/0_k3B9c-aDz179qBT0.jpg");
        Image branding = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(branding);
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

        String verifyLogin = "SELECT count(1), isAdmin FROM user_account WHERE username = '" + usernameTextField.getText() + "' AND password = '" + enterPasswordField.getText() + "'";

        try {
            Statement statement = connectionDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);
            while (queryResult.next()) {
                int count = queryResult.getInt(1);
                if (count == 1) {
                    int isAdmin = queryResult.getInt("isAdmin");
                    if (isAdmin == 0) {
                        // User is not an admin
                        userSwitchLoad();
                    } else {
                        // User is an admin
                        adminSwitchLoad();
                    }
                } else {
                    loginMessageLabel.setText("Invalid User");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerSwitchLoad(ActionEvent event) throws IOException {

        Stage stage = (Stage) userSignup.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registerView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        SceneUtil.centerSceneOnScreen(stage,scene);
        stage.setScene(scene);
    }
    private void userSwitchLoad() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            SceneUtil.centerSceneOnScreen(stage,scene);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void adminSwitchLoad() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adminView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            SceneUtil.centerSceneOnScreen(stage,scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}