package org.wms;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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

    private Connection connectionDB;
    private String userName;

    public userController(String name) {
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
        this.userName = name;
    }
    public userController(){
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
    }
    @FXML
    private AnchorPane summarySection;
    @FXML
    private ComboBox<String> bedQuality;
    @FXML
    private ComboBox<String> sofaQuality;
    @FXML
    private ComboBox<String> tableQuality;
    @FXML
    private AnchorPane orderSummaryBed;
    @FXML
    private AnchorPane orderSummarySofa;
    @FXML
    private AnchorPane orderSummaryTable;
    @FXML
    private AnchorPane orderSummarySofaBed;
    @FXML
    private TextField userAddress;
    @FXML
    private TextField detail_quality;
    @FXML
    private TextField orderUserName;
    @FXML
    private AnchorPane orderSummaryBedTable;
    @FXML
    private AnchorPane orderSummarySofaTable;
    @FXML
    private AnchorPane orderSummarySofaBedTable;
    @FXML
    private AnchorPane orderDetail;

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
    private ImageView wmsLogo;
    @FXML
    private Button cancelButton;
    @FXML
    private Button placeOrderBtn;
    @FXML
    private TextField orderID_details;
    @FXML
    private TextField nameField;
    @FXML
    private TextField address_order;
    @FXML
    private TextField feedback_order;
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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialization code...
            nameField.setText(userName);
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
    String orderID = generateID();

    public void placeOrder() throws OrderPlacementException {
        if (address_order.getText().isBlank() || feedback_order.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please fill the Order details before proceeding");
            alert.showAndWait();

            logger.error("Address or feedback not filled");

            return;
        }
        MapQuality map = new MapQuality();
            ArrayList<OrderItem> items = new ArrayList<>();
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
            logger.info("Printing the Placed items ");

            if (items.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Your Selection is Empty");
                alert.showAndWait();
                return;
            }

            int sizeOfList = items.size();
            if (sizeOfList == 1) {
                summarySection.setVisible(true);
                orderDetail.setVisible(false);
                String cName = items.get(0).getCommodity();
                if (cName.equals("bed")) {
                    logger.info("Bed Only");
                    orderSummarySofaBedTable.setVisible(false);
                    orderSummaryBed.setVisible(true);
                    orderSummarySofa.setVisible(false);
                    orderSummaryTable.setVisible(false);
                    orderSummarySofaBed.setVisible(false);
                    orderSummaryBedTable.setVisible(false);
                    orderSummarySofaTable.setVisible(false);
                } else if (cName.equals("sofa")) {
                    logger.info("Sofa only");
                    orderSummarySofaBedTable.setVisible(false);
                    orderSummaryBed.setVisible(false);
                    orderSummarySofa.setVisible(true);
                    orderSummaryTable.setVisible(false);
                    orderSummarySofaBed.setVisible(false);
                    orderSummaryBedTable.setVisible(false);
                    orderSummarySofaTable.setVisible(false);
                } else if (cName.equals("table")) {
                    logger.info("table Only ");
                    orderSummarySofaBedTable.setVisible(false);
                    orderSummaryBed.setVisible(false);
                    orderSummarySofa.setVisible(false);
                    orderSummaryTable.setVisible(true);
                    orderSummarySofaBed.setVisible(false);
                    orderSummaryBedTable.setVisible(false);
                    orderSummarySofaTable.setVisible(false);
                }

            } else if (sizeOfList == 2) {
                summarySection.setVisible(true);
                orderDetail.setVisible(false);
                String cName1 = items.get(0).getCommodity();
                String cName2 = items.get(1).getCommodity();
                String Cname = cName1 + cName2;
                if (Cname.equals("sofabed") || Cname.equals("bedsofa")) {
                    logger.info("Sofa Bed Only ");
                    orderSummarySofaBedTable.setVisible(false);
                    orderSummaryBed.setVisible(false);
                    orderSummarySofa.setVisible(false);
                    orderSummaryTable.setVisible(false);
                    orderSummarySofaBed.setVisible(true);
                    orderSummaryBedTable.setVisible(false);
                    orderSummarySofaTable.setVisible(false);
                } else if (Cname.equals("bedtable") || Cname.equals("tablebed")) {
                    logger.info("Bed and table ");
                    orderSummarySofaBedTable.setVisible(false);
                    orderSummaryBed.setVisible(false);
                    orderSummarySofa.setVisible(false);
                    orderSummaryTable.setVisible(false);
                    orderSummarySofaBed.setVisible(false);
                    orderSummaryBedTable.setVisible(true);
                    orderSummarySofaTable.setVisible(false);
                } else if (Cname.equals("sofatable") || Cname.equals("tablesofa")) {
                    logger.info("Sofa and table only ");
                    orderSummarySofaBedTable.setVisible(false);
                    orderSummaryBed.setVisible(false);
                    orderSummarySofa.setVisible(false);
                    orderSummaryTable.setVisible(false);
                    orderSummarySofaBed.setVisible(false);
                    orderSummaryBedTable.setVisible(false);
                    orderSummarySofaTable.setVisible(true);
                }
            } else if (sizeOfList == 3) {
                summarySection.setVisible(true);
                orderDetail.setVisible(false);
                logger.info("Sofa bed and table ");
                orderSummarySofaBedTable.setVisible(true);
                orderSummaryBed.setVisible(false);
                orderSummarySofa.setVisible(false);
                orderSummaryTable.setVisible(false);
                orderSummarySofaBed.setVisible(false);
                orderSummaryBedTable.setVisible(false);
                orderSummarySofaTable.setVisible(false);
                bts_name.setText(userName);
               // bts_add.setText(userAddress.getText());
                bts_id.setText(orderID);


                for (OrderItem item : items) {
                    if (item.getCommodity().equals("bed")) {
                        // Set the quality and quantity of the bed
                        bts_b_ql.setText(map.getQualitiesName(item.getQuality()));
                        bts_b_qt.setText(Integer.toString(item.getQuantity()));
                    } else if (item.getCommodity().equals("sofa")) {
                        // Set the quality and quantity of the sofa
                        bts_s_ql.setText(map.getQualitiesName(item.getQuality()));
                        bts_s_qt.setText(Integer.toString(item.getQuantity()));
                    } else if (item.getCommodity().equals("table")) {
                        // Set the quality and quantity of the table
                        bts_t_ql.setText(map.getQualitiesName(item.getQuality()));
                        bts_t_qt.setText(Integer.toString(item.getQuantity()));
                    }
                }

                // Place the order
//                boolean res = insertIntoOrder(items, orderID);
//                if (!res) {
//                    throw new OrderPlacementException("Failed to place the order.");
//                } else {
//                    Stage stage = (Stage) placeOrderBtn.getScene().getWindow();
//                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("thanksView.fxml"));
//                    Scene scene = new Scene(fxmlLoader.load());
//                    stage.setScene(scene);
//                }
            }

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
