package org.wms;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wms.utils.DatabaseConnection;
import org.wms.utils.MapQuality;
import org.wms.utils.OrderItem;

// Custom exception class for order placement errors
class OrderPlacementException extends Exception {
    public OrderPlacementException(String message) {
        super(message);
    }
}

public class userController implements Initializable {
    private static final Logger logger = LogManager.getLogger(userController.class);
    private static final String IMAGES_PATH = "images/";

    private Connection connectionDB;
    private ArrayList<OrderItem> items = new ArrayList<>();
    public userController(){
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }
    @FXML
    private TextField address_order;
    @FXML
    private TextField order_phone;

    @FXML
    private ImageView bedImage;

    @FXML
    private ImageView main_image;

    @FXML
    private ComboBox<String> bedQuality;

    @FXML
    private ComboBox<String> bedQuantity;

    @FXML
    private ImageView bed_image;

    @FXML
    private AnchorPane bed_light;

    @FXML
    private Label bed_ql;

    @FXML
    private Label bed_qt;

    @FXML
    private TextField bts_add;

    @FXML
    private TextField bts_b_ql;

    @FXML
    private TextField bts_b_qt;

    @FXML
    private TextField bts_id;

    @FXML
    private TextField bts_name;

    @FXML
    private TextField bts_s_ql;

    @FXML
    private TextField bts_s_qt;

    @FXML
    private TextField bts_t_ql;

    @FXML
    private TextField bts_t_qt;

    @FXML
    private Button cancelButton;

    @FXML
    private Button cancelConfirm;

    @FXML
    private Button confirmOrder;

    @FXML
    private TextField feedback_order;

    @FXML
    private AnchorPane isBed;

    @FXML
    private AnchorPane isSofa;

    @FXML
    private AnchorPane isTable;

    @FXML
    private TextField nameField;

    @FXML
    private AnchorPane orderDetail;

    @FXML
    private AnchorPane order_summary;

    @FXML
    private ImageView order_summary_img;

    @FXML
    private Button placeOrderBtn;

    @FXML
    private Label s_add;

    @FXML
    private Label s_name;

    @FXML
    private Label s_orderid;

    @FXML
    private ImageView sofaImage;

    @FXML
    private ComboBox<String> sofaQuality;

    @FXML
    private ComboBox<String> sofaQuantity;

    @FXML
    private ImageView sofa_image;

    @FXML
    private AnchorPane sofa_light;

    @FXML
    private Label sofa_ql;

    @FXML
    private Label sofa_qt;
    @FXML
    private ImageView tableImage;

    @FXML
    private ComboBox<String> tableQuality;

    @FXML
    private ComboBox<String> tableQuantity;

    @FXML
    private ImageView table_image;
    @FXML
    private Label s_phone;
    @FXML
    private AnchorPane table_light;

    @FXML
    private Label table_ql;

    @FXML
    private Label table_qt;

    @FXML
    private ImageView wmsLogo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialization code...
//            nameField.setText(userName.toUpperCase());
            bedQuality.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));
            sofaQuality.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));
            tableQuality.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));

            bedQuantity.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
            sofaQuantity.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
            tableQuantity.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));

            File bedFile = new File("images/bed.png");
            File sofaFile = new File("images/sofa.png");
            File tableFile = new File("images/table.png");
            File wmsFile = new File("images/WMSLoginPage.png");

            Image bedimage = new Image(bedFile.toURI().toString());
            bedImage.setImage(bedimage);

            Image sofa = new Image(sofaFile.toURI().toString());
            sofaImage.setImage(sofa);

            Image table = new Image(tableFile.toURI().toString());
            tableImage.setImage(table);

//          Setting images of order summary
            table_image.setImage(table);
            sofa_image.setImage(sofa);
            bed_image.setImage(bedimage);

            Image wmslogo = new Image(wmsFile.toURI().toString());
            wmsLogo.setImage(wmslogo);

            logger.info("User controller initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing user controller", e);
        }
    }

    public void closePlaceOrder(ActionEvent e) {
        try {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        } catch (IOException ex) {
            logger.error("Error closing place order", ex);
        }
    }
    public void cancelHandler(ActionEvent e) {
        bedQuality.setValue(null);
        bedQuality.setPromptText("0");

        sofaQuality.setValue(null);
        sofaQuality.setPromptText("0");

        tableQuality.setValue(null);
        tableQuality.setPromptText("0");

        bedQuantity.setValue(null);
        bedQuantity.setPromptText("0");

        sofaQuantity.setValue(null);
        sofaQuantity.setPromptText("0");

        tableQuantity.setValue(null);
        tableQuantity.setPromptText("0");

        orderDetail.setVisible(true);
        clearOrderDetails();
    }

    String orderID = generateID();
    public void placeOrder(ActionEvent event) throws OrderPlacementException, SQLException, IOException {
        if (address_order.getText().isBlank() || feedback_order.getText().isBlank() || nameField.getText().isBlank() || order_phone.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please fill the Order details before proceeding");
            alert.showAndWait();

            logger.error("Address or feedback not filled");
            return;
        }
        if (order_phone.getText().isBlank() || !isInteger(order_phone.getText()) || order_phone.getText().length() != 10) {
            // Show error message or take appropriate action
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a valid phone number");
            alert.showAndWait();

            logger.error("Invalid phone number entered");
            return;
        }
        items.clear();
        MapQuality map = new MapQuality();

        if (bedQuality.getValue() != null && bedQuantity.getValue() != null) {
            int bedQL = map.getQualitiesValues(bedQuality.getValue().toLowerCase());
            int bedQN = Integer.parseInt(bedQuantity.getValue());
            items.add(new OrderItem("bed", bedQL, bedQN));
        }
        if (sofaQuality.getValue() != null && sofaQuantity.getValue() != null) {
            int sofaQL = map.getQualitiesValues(sofaQuality.getValue().toLowerCase());
            int sofaQN = Integer.parseInt(sofaQuantity.getValue());
            items.add(new OrderItem("sofa", sofaQL, sofaQN));
        }
        if (tableQuality.getValue() != null && tableQuantity.getValue() != null) {
            int tableQN = Integer.parseInt(tableQuantity.getValue());
            int tableQL = map.getQualitiesValues(tableQuality.getValue().toLowerCase());
            items.add(new OrderItem("table", tableQL, tableQN));
        }

        if (items.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Your Selection is Empty");
            alert.showAndWait();
            return;
        }
        s_name.setText(nameField.getText().toUpperCase());
        s_add.setText(address_order.getText());
        s_orderid.setText(orderID.toUpperCase());
        s_phone.setText(order_phone.getText());
        // Clear existing values
        clearOrderDetails();
        // Show the summary section
        orderDetail.setVisible(false);
        order_summary.setVisible(true);
        // Display order details based on the selected items
        displayOrderDetails();
    }
    private void clearOrderDetails() {
        // Clear existing order details
        bed_light.setStyle("-fx-background-color: #FF5733;" +
                "-fx-background-radius: 100");
        sofa_light.setStyle("-fx-background-color: #FF5733;" +
                "-fx-background-radius: 100");
        table_light.setStyle("-fx-background-color: #FF5733;" +
                "-fx-background-radius: 100");
        bed_ql.setText("");
        bed_qt.setText("");
        sofa_ql.setText("");
        sofa_qt.setText("");
        table_ql.setText("");
        table_qt.setText("");
        bed_ql.setVisible(true);
        bed_qt.setVisible(true);
        sofa_ql.setVisible(true);
        sofa_qt.setVisible(true);
        table_ql.setVisible(true);
        table_qt.setVisible(true);
        sofa_image.setOpacity(1);
        bed_image.setOpacity(1);
        table_image.setOpacity(1);
        bed_light.setOpacity(1);
        table_light.setOpacity(1);
        sofa_light.setOpacity(1);
    }
    public boolean isInteger(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    MapQuality map = new MapQuality();
    private void displayOrderDetails() {
        File bedSofaFile = new File("images/BedSofa.png");
        File sofaTableFile = new File("images/sofaTable.png");
        File bedTableFile = new File("images/bedTable.png");
        File bedSofaTableFile = new File("images/sofaBedTable.png");

        Image bedSofa = new Image(bedSofaFile.toURI().toString());
        Image bedTable = new Image(bedTableFile.toURI().toString());
        Image sofaTable = new Image(sofaTableFile.toURI().toString());
        Image bedSofaTable = new Image(bedSofaTableFile.toURI().toString());
        int sizeOfList = items.size();
        if (sizeOfList == 1) {
            OrderItem item1 = items.get(0);
            if (item1.getCommodity().equals("bed")) {
                bed_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                bed_ql.setText(map.getQualitiesName(item1.getQuality()));
                bed_qt.setText(String.valueOf(item1.getQuantity()));
                sofa_ql.setVisible(false);
                sofa_qt.setVisible(false);
                table_ql.setVisible(false);
                table_qt.setVisible(false);
                sofa_image.setOpacity(.2);
                sofa_image.setScaleX(0.8);
                sofa_light.setOpacity(0.2);
                sofa_image.setScaleY(0.8);
                table_image.setOpacity(.2);
                table_image.setScaleX(0.8);
                table_light.setOpacity(0.2);
                table_image.setScaleY(0.8);
                main_image.setImage(bedImage.getImage());
            } else if (item1.getCommodity().equals("sofa")) {
                sofa_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                sofa_ql.setText(map.getQualitiesName(item1.getQuality()));
                sofa_qt.setText(String.valueOf(item1.getQuantity()));
                bed_ql.setVisible(false);
                bed_qt.setVisible(false);
                table_ql.setVisible(false);
                table_qt.setVisible(false);
                bed_image.setOpacity(.2);
                bed_image.setScaleX(0.8);
                bed_light.setOpacity(0.2);
                bed_image.setScaleY(0.8);
                table_image.setOpacity(.2);
                table_image.setScaleX(0.8);
                table_light.setOpacity(0.2);
                table_image.setScaleY(0.8);
                main_image.setImage(sofaImage.getImage());
            } else if (item1.getCommodity().equals("table")) {
                table_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                table_ql.setText(map.getQualitiesName(item1.getQuality()));
                table_qt.setText(String.valueOf(item1.getQuantity()));
                bed_ql.setVisible(false);
                bed_qt.setVisible(false);
                sofa_ql.setVisible(false);
                sofa_qt.setVisible(false);
                bed_image.setOpacity(.2);
                bed_image.setScaleX(0.8);
                bed_light.setOpacity(0.2);
                bed_image.setScaleY(0.8);
                sofa_image.setOpacity(.2);
                sofa_image.setScaleX(0.8);
                sofa_light.setOpacity(0.2);
                sofa_image.setScaleY(0.8);
                main_image.setImage(tableImage.getImage());
            }
        }
        else if (sizeOfList == 2) {

            String cName1 = items.get(0).getCommodity();
            String cName2 = items.get(1).getCommodity();
            String Cname = cName1 + cName2;
            if (Cname.equals("sofatable") || Cname.equals("tablesofa")) {
                sofa_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                table_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                sofa_ql.setText(map.getQualitiesName(items.get(0).getQuality()));
                sofa_qt.setText(String.valueOf(items.get(0).getQuantity()));
                table_ql.setText(map.getQualitiesName(items.get(1).getQuality()));
                table_qt.setText(String.valueOf(items.get(1).getQuantity()));
                bed_image.setOpacity(.2);
                bed_image.setScaleX(0.8);
                bed_light.setOpacity(0.2);
                bed_image.setScaleY(0.8);
                main_image.setImage(sofaTable);

           }
            else if(Cname.equals("bedsofa")|| Cname.equals("sofabed")){
                bed_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                sofa_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                bed_ql.setText(map.getQualitiesName(items.get(0).getQuality()));
                bed_qt.setText(String.valueOf(items.get(0).getQuantity()));
                sofa_ql.setText(map.getQualitiesName(items.get(1).getQuality()));
                sofa_qt.setText(String.valueOf(items.get(1).getQuantity()));
                table_image.setOpacity(.2);
                table_image.setScaleX(0.8);
                table_light.setOpacity(0.2);
                table_image.setScaleY(0.8);
                main_image.setImage(bedSofa);
            }
            else{
                bed_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                table_light.setStyle("-fx-background-color: #50C878;" +
                        "-fx-background-radius: 100");
                bed_ql.setText(map.getQualitiesName(items.get(0).getQuality()));
                bed_qt.setText(String.valueOf(items.get(0).getQuantity()));
                table_ql.setText(map.getQualitiesName(items.get(1).getQuality()));
                table_qt.setText(String.valueOf(items.get(1).getQuantity()));
                sofa_image.setOpacity(.2);
                sofa_image.setScaleX(0.8);
                sofa_light.setOpacity(0.2);
                sofa_image.setScaleY(0.8);
                main_image.setImage(bedTable);
            }
        }
        else {
            bed_light.setStyle("-fx-background-color: #50C878;" +
                    "-fx-background-radius: 100");
            sofa_light.setStyle("-fx-background-color: #50C878;" +
                    "-fx-background-radius: 100");
            table_light.setStyle("-fx-background-color: #50C878;" +
                    "-fx-background-radius: 100");
            bed_ql.setText(map.getQualitiesName(items.get(0).getQuality()));
            bed_qt.setText(String.valueOf(items.get(0).getQuantity()));
            table_ql.setText(map.getQualitiesName(items.get(1).getQuality()));
            table_qt.setText(String.valueOf(items.get(1).getQuantity()));
            sofa_ql.setText(map.getQualitiesName(items.get(1).getQuality()));
            sofa_qt.setText(String.valueOf(items.get(1).getQuantity()));
            main_image.setImage(bedSofaTable);
        }
    }
    public void confirmHandler(ActionEvent event) throws IOException, SQLException {
        insertIntoOrder(items,orderID);
        Stage stage = (Stage) placeOrderBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("thanksView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }
    private boolean insertIntoOrder(ArrayList<OrderItem> items, String orderID) throws SQLException {
        int commodityID = -1;
        String query = "INSERT INTO order_detail (order_id, commodity_id, quality, quantity) VALUES (?, ?,  ?, ?)";
        for (int i = 0; i < items.size(); i++) {
            String commodityName = items.get(i).getCommodity();
            commodityID = getCommodityID(commodityName);
            PreparedStatement statement = connectionDB.prepareStatement(query);
            statement.setString(1, orderID);
            statement.setInt(2, commodityID);
            statement.setInt(3, items.get(i).getQuality());
            statement.setInt(4, items.get(i).getQuantity());
            statement.executeUpdate();
        }
        return true;
    }

    private int getCommodityID(String name) throws SQLException {
        int commodityId = -1;
        String insertQuery = "SELECT c_ID FROM commodities WHERE name = ?";
        try {
            PreparedStatement statement = connectionDB.prepareStatement(insertQuery);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                commodityId = resultSet.getInt("c_ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return commodityId;
    }
    private String generateID() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString().replace("-", "");
        return uuidString.substring(0, 4);
    }
}

