package org.wms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class adminController implements Initializable {
    private Connection connectionDB;

    public adminController() {
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }
    @FXML
    private Button addCommodityBtn;

    @FXML
    private AnchorPane addCommoditySection;

    @FXML
    private ImageView addIcon;

    @FXML
    private ImageView adminIcon;

    @FXML
    private ImageView dashboardIcon;

    @FXML
    private AnchorPane dashboardSection;

    @FXML
    private Button homeBtn;

    @FXML
    private ImageView orderIcon;

    @FXML
    private Label orderPending;

    @FXML
    private TableColumn<warehouseData, Integer> quality_Col;

    @FXML
    private TableColumn<warehouseData, Integer> quantity_Col;

    @FXML
    private TableColumn<warehouseData, Integer> c_ID_Col;

    @FXML
    private TableColumn<warehouseData, String> c_Name_Col;

    @FXML
    private TableView<warehouseData> tableView;

    @FXML
    private ImageView truckINOUT;
    @FXML
    private ImageView pendingImg;

    @FXML
    private ImageView truckINOUT2;

    @FXML
    private ImageView warehouseIMG;

    @FXML
    private ComboBox<String> commodity_Select;
    @FXML
    private TextField quantity_select;
    @FXML
    private ComboBox<Integer> quality_select;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        Default loading for the dashboard
        initializeImages();
        getOrdersPending();
        loadWarehouseData();
        initializeDropdowns();

    }
    private void initializeImages() {
        Image adminImg = loadImage("admin.jpg");
        Image addImg = loadImage("add.png");
        Image dashboardImg = loadImage("dashboard.png");
        Image orderIconImg = loadImage("orders.png");
        Image truckInOutImg = loadImage("truckINOUT.jpg");
        Image wareHouseImg = loadImage("warehouse.png");
        Image pendingIMG = loadImage("pending.png");

        addIcon.setImage(addImg);
        adminIcon.setImage(adminImg);
        dashboardIcon.setImage(dashboardImg);
        orderIcon.setImage(orderIconImg);
        truckINOUT.setImage(truckInOutImg);
        truckINOUT2.setImage(truckInOutImg);
        warehouseIMG.setImage(wareHouseImg);
        pendingImg.setImage(pendingIMG);
    }

    private Image loadImage(String filename) {
        File file = new File("images/" + filename);
        return new Image(file.toURI().toString());
    }
    private void initializeDropdowns() {
        commodity_Select.setItems(FXCollections.observableArrayList("Bed", "Sofa", "Table"));
        quality_select.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
    }

    public ObservableList<warehouseData> getWareHouseData() {
        String query = "SELECT * FROM commodities";
        ObservableList<warehouseData> warehouseData = FXCollections.observableArrayList();
        try {
            PreparedStatement prepare = connectionDB.prepareStatement(query);
            ResultSet result = prepare.executeQuery();
            while (result.next()) {
                int c_id = Integer.parseInt(result.getString("c_ID"));
                String name = result.getString("name");
                int quality = Integer.parseInt(result.getString("quality"));
                int quantity = Integer.parseInt(result.getString("quantity"));
                warehouseData whd = new warehouseData(c_id, name, quality, quantity);
                warehouseData.add(whd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warehouseData;
    }
    private ObservableList<warehouseData> list;
    public void loadWarehouseData(){
        list = getWareHouseData();
        c_ID_Col.setCellValueFactory(new PropertyValueFactory<>("c_ID"));
        c_Name_Col.setCellValueFactory(new PropertyValueFactory<>("name"));
        quality_Col.setCellValueFactory(new PropertyValueFactory<>("quality"));
        quantity_Col.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tableView.setItems(list);
    }
    public void setHomeBtn(ActionEvent event){
        if(event.getSource() == homeBtn){
            addCommoditySection.setVisible(false);
            dashboardSection.setVisible(true);
        }
        else if(event.getSource() == addCommodityBtn){
            addCommoditySection.setVisible(true);
            dashboardSection.setVisible(false);
        }
    }
    private void getOrdersPending(){
        int count = 0;
        String getOrders = "SELECT COUNT(*) AS not_delivered_count\n" +
                "FROM order_detail\n" +
                "WHERE delivery_status = 'not_delivered';";
        try{
            PreparedStatement prepare = connectionDB.prepareStatement(getOrders);
            ResultSet result = prepare.executeQuery();
            if(result.next()){
                count = result.getInt("not_delivered_count");
                orderPending.setText(String.valueOf(count));
            }
            else{
                orderPending.setText("0");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateWarehouse() throws SQLException {
        String c_Name = commodity_Select.getValue().toLowerCase();
        int quality = quality_select.getValue();
        int quantity;
        try {
            quantity = Integer.parseInt(quantity_select.getText().trim());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Quantity");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid integer for quantity.");
            alert.showAndWait();
            return; // Exit the method
        }
//      Query to update the commodity
        String updateQuery = "UPDATE commodities\n" +
                "SET quantity = quantity + ?\n" +
                "WHERE name = ? AND quality = ?;";

        try (PreparedStatement prepare = connectionDB.prepareStatement(updateQuery)) {
            prepare.setInt(1, quantity);
            prepare.setString(2, c_Name);
            prepare.setInt(3, quality);
            prepare.executeUpdate();
        }
    }
    public void clearWarehouse() {
        commodity_Select.getSelectionModel().clearSelection();
        commodity_Select.setPromptText("Choose again");
        quantity_select.setText("0");
        quality_select.getSelectionModel().clearSelection();
        quality_select.setPromptText("0");
    }

}
