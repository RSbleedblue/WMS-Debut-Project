package org.wms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.wms.utils.DatabaseConnection;
import org.wms.utils.MapQuality;
import org.wms.utils.gatekeeperData;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class gatekeeperController implements Initializable {

    @FXML
    private CheckBox bed_check;
    @FXML
    private Button logOutBTN;

    @FXML
    private ImageView bed_image;

    @FXML
    private ComboBox<String> bed_quality;

    @FXML
    private ComboBox<String> bed_quantity;

    @FXML
    private Button confirm;

    @FXML
    private Button reset;

    @FXML
    private CheckBox sofa_check;

    @FXML
    private ImageView sofa_image;

    @FXML
    private ComboBox<String> sofa_quality;

    @FXML
    private ComboBox<String> sofa_quantity;

    @FXML
    private CheckBox table_check;

    @FXML
    private ImageView table_image;

    @FXML
    private ComboBox<String> table_quality;

    @FXML
    private ComboBox<String> table_quantity;

    @FXML
    private Label truck_id;


    //    Default constructor
    private final Connection connectDB;
    public  gatekeeperController(){
        DatabaseConnection connection = new DatabaseConnection();
        connectDB = connection.getConnection();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        initializeImages();
        initializeDropdowns();
        truck_id.setText(Integer.toString(generateProcessID()));
    }
    MapQuality map = new MapQuality();
    private void initializeDropdowns(){
        bed_quantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        bed_quality.setItems(FXCollections.observableArrayList("BASIC", "FAIR", "AVERAGE", "GOOD", "BEST"));

        sofa_quantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        sofa_quality.setItems(FXCollections.observableArrayList("BASIC", "FAIR", "AVERAGE", "GOOD", "BEST"));

        table_quantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        table_quality.setItems(FXCollections.observableArrayList("BASIC", "FAIR", "AVERAGE", "GOOD", "BEST"));
    }
    private List<gatekeeperData> gatekeeperDataList = new ArrayList<>();
    private boolean isValidQuality(String quality) {
        return quality.equals("fair") || quality.equals("basic") ||
                quality.equals("good") || quality.equals("average") || quality.equals("best");
    }
    public void confirmOrder() throws IOException {
        // Check if no item is selected
        if (!bed_check.isSelected() && !sofa_check.isSelected() && !table_check.isSelected()) {
            showAlert("No Items Selected", "Please select at least one item to confirm the order.");
            return;
        }

        // Check if quality or quantity is not selected for any selected item
        if ((bed_check.isSelected() && (bed_quality.getValue() == null || bed_quantity.getValue() == null)) ||
                (sofa_check.isSelected() && (sofa_quality.getValue() == null || sofa_quantity.getValue() == null)) ||
                (table_check.isSelected() && (table_quality.getValue() == null || table_quantity.getValue() == null))) {
            showAlert("Missing Information", "Please select both quality and quantity for all selected items.");
            return;
        }

        // Check if quality or quantity is selected for any unselected item
        if (!bed_check.isSelected() && (bed_quality.getValue() != null || bed_quantity.getValue() != null) ||
                !sofa_check.isSelected() && (sofa_quality.getValue() != null || sofa_quantity.getValue() != null) ||
                !table_check.isSelected() && (table_quality.getValue() != null || table_quantity.getValue() != null)) {
            showAlert("Invalid Selection", "Quality or quantity is selected for an item that is not checked.");
            return;
        }

        // Add selected items to gatekeeperDataList
        if (bed_check.isSelected()) {

            String qualityValue = bed_quality.getValue().toLowerCase();

            // Check if quality is valid
            if (!isValidQuality(qualityValue)) {
                showAlert("Invalid Quality", "Please select a valid quality (FAIR, BASIC, GOOD, AVERAGE, BEST) .");
                return;
            }

            String quality = Integer.toString(map.getQualitiesValues(qualityValue));
            String quantity = bed_quantity.getValue();
            gatekeeperData data = new gatekeeperData("bed", quality, quantity);
            gatekeeperDataList.add(data);
        }

        if (sofa_check.isSelected()) {
            String qualityValue = sofa_quality.getValue().toLowerCase();
            if (!isValidQuality(qualityValue)) {
                showAlert("Invalid Quality", "Please select a valid quality (FAIR, BASIC, GOOD, AVERAGE, BEST) .");
                return;
            }
            String quality = Integer.toString(map.getQualitiesValues(sofa_quality.getValue().toLowerCase()));
            String quantity = sofa_quantity.getValue();
            gatekeeperData data = new gatekeeperData("sofa", quality, quantity);
            gatekeeperDataList.add(data);
        }
        if (table_check.isSelected()) {
            String qualityValue = table_quality.getValue().toLowerCase();
            if (!isValidQuality(qualityValue)) {
                showAlert("Invalid Quality", "Please select a valid quality (FAIR, BASIC, GOOD, AVERAGE, BEST) .");
                return;
            }
            String quality = Integer.toString(map.getQualitiesValues(table_quality.getValue().toLowerCase()));
            String quantity = table_quantity.getValue();
            gatekeeperData data = new gatekeeperData("table", quality, quantity);
            gatekeeperDataList.add(data);
        }

        // Insert data into database
        String insertQuery = "INSERT INTO gatekeeper_data (c_name, quality, quantity, process) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery)) {
            for (gatekeeperData data : gatekeeperDataList) {
                String qualityChanged = data.getQuality().toLowerCase();
                insertStatement.setString(1, data.getCname());
                insertStatement.setInt(2, Integer.parseInt(qualityChanged));
                insertStatement.setInt(3, Integer.parseInt(data.getQuantity()));
                insertStatement.setInt(4, generateProcessID());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while processing the order. Please try again later.");
            return;
        }

        // Show confirmation alert
        showAlert("Form Filled", "Your Form is sent to admin");
        returnback();
    }
    public void logOut(ActionEvent event) throws IOException {
        Stage stage = (Stage) logOutBTN.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
    public void returnback() throws IOException {
        Stage stage = (Stage) confirm.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
    public void setReset(){
        // Reset checkboxes
        bed_check.setSelected(false);
        sofa_check.setSelected(false);
        table_check.setSelected(false);

        // Reset dropdowns
        bed_quality.getSelectionModel().clearSelection();
        bed_quantity.getSelectionModel().clearSelection();
        sofa_quality.getSelectionModel().clearSelection();
        sofa_quantity.getSelectionModel().clearSelection();
        table_quality.getSelectionModel().clearSelection();
        table_quantity.getSelectionModel().clearSelection();
    }
    private int generateProcessID(){
        String query = "SELECT MAX(ID) AS latest_id FROM gatekeeper_data";
        int truckID = 1000;
        try(PreparedStatement statement = connectDB.prepareStatement(query)){
            ResultSet set =  statement.executeQuery();
            if(set.next()){
                truckID += set.getInt("latest_id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return truckID;
    }
    private void initializeImages(){
        Image sofaImage = loadImage("sofa.png");
        Image bedImage = loadImage("bed.png");
        Image tableImage = loadImage("table.png");

        sofa_image.setImage(sofaImage);
        bed_image.setImage(bedImage);
        table_image.setImage(tableImage);
    }
    private Image loadImage(String filepath){
        File path = new File("images/" + filepath);
        return new Image(path.toURI().toString());
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
