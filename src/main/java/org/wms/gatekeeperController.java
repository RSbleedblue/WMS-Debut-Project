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
        bed_quality.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));

        sofa_quantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        sofa_quality.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));

        table_quantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        table_quality.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));
    }
    private List<gatekeeperData> gatekeeperDataList = new ArrayList<>();
    public void confirmOrder() throws IOException {
        String cname = "";
        if(bed_check.isSelected()){
            cname = "bed";
            String quality =Integer.toString(map.getQualitiesValues(bed_quality.getValue().toLowerCase()));
            String quantity = bed_quantity.getValue();
            gatekeeperData data = new gatekeeperData(cname,quality,quantity);
            gatekeeperDataList.add(data);
        }
        if (sofa_check.isSelected()) {
            cname = "sofa";
            String quality = Integer.toString(map.getQualitiesValues(sofa_quality.getValue().toLowerCase()));
            String quantity = sofa_quantity.getValue();
            gatekeeperData data = new gatekeeperData(cname, quality, quantity);
            gatekeeperDataList.add(data);
        }
        if (table_check.isSelected()) {
            cname = "table";
            String quality = Integer.toString(map.getQualitiesValues(table_quality.getValue().toLowerCase()));
            String quantity = table_quantity.getValue();
            gatekeeperData data = new gatekeeperData(cname, quality, quantity);
            gatekeeperDataList.add(data);
        }
        String insertQuery = "Insert into gatekeeper_data (c_name, quality, quantity, process) Values (? , ? , ?, ?)";
        try (PreparedStatement insertStatement = connectDB.prepareStatement(insertQuery)){
            for(gatekeeperData data : gatekeeperDataList){
                String qualityChanged = data.getQuality().toLowerCase();
                insertStatement.setString(1, data.getCname());
                insertStatement.setInt(2,Integer.parseInt(qualityChanged));
                insertStatement.setInt(3,Integer.parseInt(data.getQuantity()));
                insertStatement.setInt(4, generateProcessID());
                insertStatement.executeUpdate();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Form Filled");
        alert.setContentText("Your Form is sent to admin");
        alert.showAndWait();
        returnback();
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
}
