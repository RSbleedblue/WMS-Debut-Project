package org.wms;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class loginController  implements Initializable {
    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private ImageView lockImageView;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File brandingFile = new File("images/0_k3B9c-aDz179qBT0.jpg");
        Image branding = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(branding);

        File lockFile = new File("images/lock.png");
        Image lock = new Image(lockFile.toURI().toString());
        lockImageView.setImage(lock);
    }

    public void loginButtonOnAction(ActionEvent event){
        if(!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()){
            validateLogin();
        }
        else{
            loginMessageLabel.setText("Invalid");
        }
    }
    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    public void validateLogin(){
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectionDB = connection.getConnection();

        String verifyLogin = "select count(1) FROM user_account WHERE username = '"+usernameTextField.getText() + "' AND password ='" + enterPasswordField.getText() + "'";

        try{
            Statement statement = connectionDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);
            while(queryResult.next()){
                if(queryResult.getInt(1) == 1){
                    loginMessageLabel.setText("Logged In");
                }
                else{
                    loginMessageLabel.setText("Invalid User");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }


}