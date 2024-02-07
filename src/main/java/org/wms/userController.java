package org.wms;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;

public class userController implements Initializable {
    private Connection connectionDB;

    public userController() {
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }
    @FXML
    private ComboBox<String> bedQuality;
    @FXML
    private ComboBox<String> sofaQuality;
    @FXML
    private ComboBox<String> tableQuality;
    @FXML
    private ComboBox<String> bedQuantity;
    @FXML
    private ComboBox<String> sofaQuantity;
    @FXML
    private ComboBox<String> tableQuantity;
    @FXML
    private ImageView bedImage;
    @FXML
    private ImageView sofaImage;
    @FXML
    private ImageView tableImage;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField placeOrderField;
    @FXML
    private Button placeOrderBtn;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        bedQuality.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        sofaQuality.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        tableQuality.setItems(FXCollections.observableArrayList("1","2","3","4","5"));

        bedQuantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        sofaQuantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));
        tableQuantity.setItems(FXCollections.observableArrayList("1","2","3","4","5"));

        File bedFile = new File("images/bed.jpg");
        File sofaFile = new File("images/sofa.jpg");
        File tableFile = new File("images/table.jpg");

        Image bedimage = new Image(bedFile.toURI().toString());
        bedImage.setImage(bedimage);

        Image sofa = new Image(sofaFile.toURI().toString());
        sofaImage.setImage(sofa);

        Image table = new Image(tableFile.toURI().toString());
        tableImage.setImage(table);

    }

    public void closePlaceOrder(ActionEvent e){
        Stage stage =(Stage)(cancelButton.getScene().getWindow());
        stage.close();
    }
    public void placeOrder() throws SQLException {

        int bedQL = Integer.parseInt(bedQuality.getValue());
        int bedQN = Integer.parseInt(bedQuantity.getValue());

        int sofaQL = Integer.parseInt(sofaQuality.getValue());
        int sofaQN = Integer.parseInt(sofaQuantity.getValue());

        int tableQN = Integer.parseInt(tableQuantity.getValue());
        int tableQL = Integer.parseInt(tableQuality.getValue());
        String bed = "";
        String table = "";
        String sofa = "";
        int orderID = generateID();

        ArrayList<OrderItem> items = new ArrayList<>();
        if(bedQL != 0 && bedQN != 0){
            items.add(new OrderItem("bed",bedQL,bedQN));
        }
        if(sofaQL != 0 && sofaQN != 0){
            items.add(new OrderItem("sofa",sofaQL,sofaQN));
        }
        if(tableQL != 0 && tableQN != 0){
            items.add(new OrderItem("table",tableQL,tableQN));
        }
        insertIntoOrder(items, orderID);
    }
    private void insertIntoOrder(ArrayList<OrderItem> items, int orderID) throws SQLException {
        int commodityID = -1;
        String query = "INSERT INTO order_detail (order_id, commodity_id, quality, quantity) VALUES (?, ?,  ?, ?)";
        for(int i = 0; i < items.size(); i++) {
            String commodityName = items.get(i).getCommodity();
            commodityID = getCommodityID(commodityName);
            PreparedStatement statement = connectionDB.prepareStatement(query);
            statement.setInt(1,orderID);
            statement.setInt(2,commodityID);
            statement.setInt(3,items.get(i).getQuality());
            statement.setInt(4,items.get(i).getQuantity());
            statement.executeUpdate();
        }

    }
    private int getCommodityID(String name){
        int commodityId = -1;
        String insertQuery = "SELECT commodity_id FROM commodities WHERE name = ?";
        try{
            PreparedStatement statement = connectionDB.prepareStatement(insertQuery);
            statement.setString(1,name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                commodityId = resultSet.getInt("commodity_id");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return  commodityId;
    }
    private int generateID(){
        return 10;
    }



}
