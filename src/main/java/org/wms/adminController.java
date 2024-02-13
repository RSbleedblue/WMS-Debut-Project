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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class adminController implements Initializable {
    private Connection connectionDB;
    public int truckCapcityVal;

    public adminController()  {
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
        try {
            truckCapcityVal = getTruckCapacity();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    private AnchorPane orders_Section;

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
    private TableView<placedOrders> order_table;
    @FXML
    private TableColumn<placedOrders, String> order_id_col;
    @FXML
    private TableColumn<placedOrders,String> order_name_col;
    @FXML
    private TableColumn<placedOrders,Integer> order_quality_col;
    @FXML
    private TableColumn<placedOrders, Integer> orders_quantity_col;

    @FXML
    private ImageView truckIN;
    @FXML
    private ImageView pendingImg;

    @FXML
    private ImageView truckOUT;

    @FXML
    private ImageView warehouseIMG;
    @FXML
    private TextField truck_capacity_orderField;

    @FXML
    private ComboBox<String> commodity_Select;
    @FXML
    private TextField quantity_select;
    @FXML
    private ComboBox<Integer> quality_select;
    @FXML
    private Label truckINTime;
    @FXML
    private Label truckOutTime;
    @FXML
    private Button orders_BTN;
    @FXML
    private Button place_order_btn;
    @FXML
    private TextField orderID_field_order;
    @FXML
    private TextField name_field_order;
    @FXML
    private TextField quality_field_order;
    @FXML
    private TextField quantity_field_order;
    @FXML
    private TextField rem_storage_field_order;
    @FXML
    private ImageView order_sec_image;

    private static final Logger logger = LogManager.getLogger(adminController.class);




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        Default loading for the dashboard
        initializeImages();
        getOrdersPending();
        loadWarehouseData();
        initializeDropdowns();
        truckTimeLoad();
        loadDeliverOrder();

    }
    public class DatabaseQueryException extends RuntimeException {
        public DatabaseQueryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    public class RemainingQuantityException extends Exception {
        public RemainingQuantityException() {
            super();
        }
    }

    public class OrderSelectionException extends RuntimeException {
        public OrderSelectionException() {
            super();
        }

        public OrderSelectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    public class WarehouseUpdateException extends Exception {
        public WarehouseUpdateException() {
            super();
        }

        public WarehouseUpdateException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    public class CancelPushedOrdersException extends Exception {
        public CancelPushedOrdersException() {
            super();
        }

        public CancelPushedOrdersException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class PushOrderException extends Exception {
        public PushOrderException() {
            super();
        }

        public PushOrderException(String message) {
            super(message);
        }
    }

    private void truckTimeLoad() {
        String query = "select * from truck_status";
        try {
            PreparedStatement statement = connectionDB.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String truckIn = result.getString("truck_in");
                String truckOut = result.getString("truck_out");
                truckINTime.setText(truckIn);
                truckOutTime.setText(truckOut);
                logger.info("Truck time loaded successfully.");
            }
        } catch (SQLException e) {
            logger.error("Error loading truck time from the database", e);
            throw new DatabaseQueryException("Error loading truck time from the database", e);
        }
    }

    private void initializeImages() {
        Image adminImg = loadImage("WMSLoginPage.png");
        Image addImg = loadImage("add.png");
        Image dashboardImg = loadImage("dashboard.png");
        Image orderIconImg = loadImage("orders.png");
        Image truckIn = loadImage("truckIN.png");
        Image truckOut = loadImage("truckOUT.png");
        Image wareHouseImg = loadImage("WMSLoginPageBW.png");
        Image pendingIMG = loadImage("pending.png");

        addIcon.setImage(addImg);
        adminIcon.setImage(adminImg);
        dashboardIcon.setImage(dashboardImg);
        orderIcon.setImage(orderIconImg);
        truckIN.setImage(truckIn);
        truckOUT.setImage(truckOut);
        warehouseIMG.setImage(wareHouseImg);
        pendingImg.setImage(pendingIMG);
        logger.info("Images initialized successfully.");
    }

    private Image loadImage(String filename) {
        File file = new File("images/" + filename);
        return new Image(file.toURI().toString());
    }

    private void initializeDropdowns() {
        commodity_Select.setItems(FXCollections.observableArrayList("Bed", "Sofa", "Table"));
        quality_select.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        logger.info("Dropdowns initialized successfully.");
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
        } catch (SQLException e) {
            logger.error("Error executing warehouse data query", e);
            throw new DatabaseQueryException("Error executing warehouse data query", e);
        }
        return warehouseData;
    }

    private ObservableList<warehouseData> list;

    public void loadWarehouseData() {
        list = getWareHouseData();
        c_ID_Col.setCellValueFactory(new PropertyValueFactory<>("c_ID"));
        c_Name_Col.setCellValueFactory(new PropertyValueFactory<>("name"));
        quality_Col.setCellValueFactory(new PropertyValueFactory<>("quality"));
        quantity_Col.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tableView.setItems(list);
        logger.info("Warehouse data loaded successfully.");
    }

    public void setHomeBtn(ActionEvent event) {
        if (event.getSource() == homeBtn) {
            addCommoditySection.setVisible(false);
            dashboardSection.setVisible(true);
            orders_Section.setVisible(false);
            logger.info("Home button clicked. Dashboard section displayed.");
        } else if (event.getSource() == addCommodityBtn) {
            addCommoditySection.setVisible(true);
            dashboardSection.setVisible(false);
            orders_Section.setVisible(false);
            logger.info("Add Commodity button clicked. Add commodity section displayed.");
        } else if (event.getSource() == orders_BTN) {
            addCommoditySection.setVisible(false);
            dashboardSection.setVisible(false);
            orders_Section.setVisible(true);
            logger.info("Orders button clicked. Orders section displayed.");
        }
    }
    private void getOrdersPending() {
        int count = 0;
        String getOrders = "SELECT COUNT(*) AS not_delivered_count\n" +
                "FROM order_detail\n" +
                "WHERE delivery_status = 'not_delivered';";
        try {
            PreparedStatement prepare = connectionDB.prepareStatement(getOrders);
            ResultSet result = prepare.executeQuery();
            if (result.next()) {
                count = result.getInt("not_delivered_count");
                orderPending.setText(String.valueOf(count));
                logger.info("Retrieved pending orders count: " + count);
            } else {
                orderPending.setText("0");
                logger.info("No pending orders found.");
            }
        } catch (SQLException e) {
            logger.error("Error retrieving pending orders count from the database", e);
            throw new DatabaseQueryException("Error retrieving pending orders count from the database", e);
        }
    }

    private ObservableList<placedOrders> getNotDeliveredOrders() {
        String query = "SELECT Order_ID, commodity_ID, quality, quantity\n" +
                "FROM order_detail\n" +
                "WHERE delivery_status ='not_delivered'\n";

        ObservableList<placedOrders> pList = FXCollections.observableArrayList();
        try (PreparedStatement getOrderStatement = connectionDB.prepareStatement(query)) {
            ResultSet orders = getOrderStatement.executeQuery();
            while (orders.next()) {
                String Order_ID = orders.getString("Order_ID");
                int commodity_ID = orders.getInt("commodity_ID");
                int quality = orders.getInt("quality");
                int quantity = orders.getInt("quantity");
                String commodityName = getCommodityName(commodity_ID); // Fetch commodity name
                placedOrders pOds = new placedOrders(Order_ID, commodityName, quality, quantity); // Pass commodity name
                pList.add(pOds);
            }
            logger.info("Retrieved not delivered orders from the database.");
        } catch (SQLException e) {
            logger.error("Error retrieving not delivered orders from the database", e);
            throw new DatabaseQueryException("Error retrieving not delivered orders from the database", e);
        }
        return pList;
    }

    ObservableList<placedOrders> placedList;
    private void loadDeliverOrder() {
        placedList = getNotDeliveredOrders();
        order_id_col.setCellValueFactory(new PropertyValueFactory<>("Order_ID"));
        order_name_col.setCellValueFactory(new PropertyValueFactory<>("commodityName"));
        order_quality_col.setCellValueFactory(new PropertyValueFactory<>("quality"));
        orders_quantity_col.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        order_table.setItems(placedList);
        logger.info("Loaded not delivered orders into the table.");
    }
    //    To show the list of the Orders selected

    public void selectOrderList() {
        try {
            placedOrders pOrds = order_table.getSelectionModel().getSelectedItem();
            if (pOrds == null) {
                // No order selected, return or show an alert
                logger.warn("No order selected.");
                showAlert("No Order Selected", "Please select an order.");
                return;
            }

            String selectedCommodity = pOrds.getCommodityName();
            if (selectedCommodity == null || selectedCommodity.isEmpty()) {
                // Selected commodity is null or empty, show an alert
                logger.warn("Selected commodity is null or empty.");
                showAlert("Commodity Not Found", "The selected commodity is not available.");
                return;
            }

            // Proceed with displaying order details and image
            int num = order_table.getSelectionModel().getSelectedIndex();
            if ((num - 1) < -1) {
                return;
            }
            orderID_field_order.setText(pOrds.getOrder_ID());
            name_field_order.setText(selectedCommodity);
            quality_field_order.setText(Integer.toString(pOrds.getQuality()));
            quantity_field_order.setText(Integer.toString(pOrds.getQuantity()));

            // Finding total capacity;
            int QuantityStatusVal = getRemainingQuantity(pOrds.getQuality(), selectedCommodity);
            rem_storage_field_order.setText(Integer.toString(QuantityStatusVal));
            // Find Truck Capacity;
            truck_capacity_orderField.setText(Integer.toString(truckCapcityVal));

            // Set image based on commodity
            if (selectedCommodity.equals("bed")) {
                File bedLoc = new File("images/bed.png");
                Image bedIMG = new Image(bedLoc.toURI().toString());
                order_sec_image.setImage(bedIMG);
            } else if (selectedCommodity.equals("sofa")) {
                File sofaLoc = new File("images/sofa.png");
                Image sofaIMG = new Image(sofaLoc.toURI().toString());
                order_sec_image.setImage(sofaIMG);
            } else if (selectedCommodity.equals("table")) {
                File tableLoc = new File("images/table.png");
                Image tableIMG = new Image(tableLoc.toURI().toString());
                order_sec_image.setImage(tableIMG);
            }
        } catch (Exception e) {
            logger.error("Error occurred while selecting an order", e);
            throw new OrderSelectionException("Error occurred while selecting an order", e);
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private ArrayList<placedOrders> pushOrders = new ArrayList<>();

    public void pushOrder() throws PushOrderException, SQLException {
        try {
            placedOrders pOds = order_table.getSelectionModel().getSelectedItem();
            int remainingQuantity = getRemainingQuantity(pOds.getQuality(), pOds.getCommodityName());

            if (pOds.getQuantity() <= remainingQuantity && pOds.getQuantity() <= truckCapcityVal) {
                // Update global truck capacity
                truckCapcityVal -= pOds.getQuantity();
                truck_capacity_orderField.setText(Integer.toString(truckCapcityVal));
                pushOrders.add(pOds);
                // Update database with new quantity
                String updateQuantityQuery = "UPDATE commodities SET quantity = quantity - ? WHERE name = ? AND quality = ?";
                try (PreparedStatement updateQuantityStatement = connectionDB.prepareStatement(updateQuantityQuery)) {
                    updateQuantityStatement.setInt(1, pOds.getQuantity());
                    updateQuantityStatement.setString(2, pOds.getCommodityName());
                    updateQuantityStatement.setInt(3, pOds.getQuality());
                    updateQuantityStatement.executeUpdate();
                }
                remainingQuantity -= pOds.getQuantity();
                rem_storage_field_order.setText(Integer.toString(remainingQuantity));

            } else {
                throw new PushOrderException("Insufficient quantity or truck capacity.");
            }
        } catch (SQLException e) {
            logger.error("Error pushing order", e);
            throw e;
        }
    }

    public void cancelPushedOrders() throws CancelPushedOrdersException, SQLException {
        try {
            int idx = pushOrders.size() - 1;
            // Update global truck capacity
            truckCapcityVal += pushOrders.get(idx).getQuantity();
            // Update database with the canceled quantity
            String updateQuantityQuery = "UPDATE commodities SET quantity = quantity + ? WHERE name = ? AND quality = ?";
            try (PreparedStatement updateQuantityStatement = connectionDB.prepareStatement(updateQuantityQuery)) {
                updateQuantityStatement.setInt(1, pushOrders.get(idx).getQuantity());
                updateQuantityStatement.setString(2, pushOrders.get(idx).getCommodityName());
                updateQuantityStatement.setInt(3, pushOrders.get(idx).getQuality());
                updateQuantityStatement.executeUpdate();
            }
            // Update the truck capacity field
            truck_capacity_orderField.setText(Integer.toString(truckCapcityVal));
            // Clear the list of pushed orders
            pushOrders.remove(pushOrders.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            logger.error("No orders pushed to cancel.", e);
            throw new CancelPushedOrdersException("No orders pushed to cancel.", e);
        } catch (SQLException e) {
            logger.error("Error while canceling pushed orders.", e);
            throw new CancelPushedOrdersException("Error while canceling pushed orders.", e);
        }
    }

    private int getRemainingQuantity(int quality, String name) throws SQLException {
        int QuantityStatusVal = -1;
        String query = "SELECT quantity FROM commodities WHERE quality = ? AND name = ?";
        try (PreparedStatement prepared = connectionDB.prepareStatement(query)) {
            prepared.setInt(1, quality);
            prepared.setString(2, name);
            ResultSet result = prepared.executeQuery();
            if (result.next()) {
                QuantityStatusVal = result.getInt("quantity");
            }
        } catch (SQLException e) {
            logger.error("Error getting remaining quantity", e);
            throw e;
        }
        return QuantityStatusVal;
    }

    private String getCommodityName(int c_id) {
        String c_name = null;
        String query = "SELECT name FROM commodities WHERE c_ID = ?";
        try (PreparedStatement preparedStatement = connectionDB.prepareStatement(query)) {
            preparedStatement.setInt(1, c_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                c_name = resultSet.getString("name");
            }
        } catch (SQLException e) {
            logger.error("Error getting commodity name", e);
            e.printStackTrace();
        }
        return c_name;
    }
    public void placeOrder() throws SQLException {
        if (pushOrders.isEmpty()) {
            logger.error("No orders pushed to place");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Push orders to place");
            alert.showAndWait();
        } else {
            String updateDeliveryQuery = "UPDATE order_detail " +
                    "SET delivery_status = 'delivered' " +
                    "WHERE order_id = ?";
            try (PreparedStatement updateDeliveryStatement = connectionDB.prepareStatement(updateDeliveryQuery)) {
                for (placedOrders pOds : pushOrders) {
                    logger.info("Updating delivery status for order ID: " + pOds.getOrder_ID());
                    updateDeliveryStatement.setString(1, pOds.getOrder_ID());
                    updateDeliveryStatement.executeUpdate();
                    truckCapcityVal -= pOds.getQuantity();
                }
            } catch (SQLException e) {
                logger.error("Error updating delivery status", e);
                throw e;
            }

            try {
                LocalTime time = LocalTime.now();
                String updateTruckOutTimeQuery = "UPDATE truck_status\n" +
                        "SET truck_out = ?\n" +
                        "WHERE truck_id = 1";
                try (PreparedStatement updateTruckOutTimeStatement = connectionDB.prepareStatement(updateTruckOutTimeQuery)) {
                    updateTruckOutTimeStatement.setObject(1, time);
                    updateTruckOutTimeStatement.executeUpdate();
                }

                String updateTruckCapacityQuery = "UPDATE truck_status\n" +
                        "SET truck_capacity = ?\n" +
                        "WHERE truck_id = 1";
                try (PreparedStatement updateTruckCapacityStatement = connectionDB.prepareStatement(updateTruckCapacityQuery)) {
                    updateTruckCapacityStatement.setInt(1, truckCapcityVal);
                    updateTruckCapacityStatement.executeUpdate();
                }

                loadDeliverOrder();
                truckTimeLoad();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Truck items match with order details");
                alert.showAndWait();
            } catch (SQLException e) {
                logger.error("Error updating truck status or reloading data", e);
                throw e;
            }
        }
    }

    private int getTruckCapacity() throws SQLException {
        String query = "SELECT truck_capacity FROM truck_status";
        try {
            PreparedStatement truckStatusStatement = connectionDB.prepareStatement(query);
            ResultSet resultSet = truckStatusStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("truck_capacity");
            } else {
                throw new SQLException("No truck capacity data found");
            }
        } catch (SQLException e) {
            logger.error("Error retrieving truck capacity", e);
            throw new SQLException("Error retrieving truck capacity", e);
        }
    }

    public void updateWarehouse() throws WarehouseUpdateException, SQLException {
        try {
            // Set truck capacity to 100
            truckCapcityVal = 100;

            String c_Name = commodity_Select.getValue().toLowerCase();
            int quality = quality_select.getValue();
            int quantity;
            try {
                quantity = Integer.parseInt(quantity_select.getText().trim());
            } catch (NumberFormatException e) {
                logger.error("Invalid quantity entered", e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Quantity");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid integer for quantity.");
                alert.showAndWait();
                return;
            }

            // Query to update the commodity quantity
            String updateQuery = "UPDATE commodities\n" +
                    "SET quantity = quantity + ?\n" +
                    "WHERE name = ? AND quality = ?;";

            try (PreparedStatement prepare = connectionDB.prepareStatement(updateQuery)) {
                prepare.setInt(1, quantity);
                prepare.setString(2, c_Name);
                prepare.setInt(3, quality);
                prepare.executeUpdate();
            }

            // Update truck in time
            LocalTime time = LocalTime.now();
            String truckInTimeQuery = "UPDATE truck_status\n" +
                    "SET truck_in = ?\n" +
                    "WHERE truck_id = 1";
            try (PreparedStatement truckInPrepare = connectionDB.prepareStatement(truckInTimeQuery)) {
                truckInPrepare.setObject(1, time);
                truckInPrepare.executeUpdate();
            }

            // Update truck out time to 00:00:00
            String truckOutTimeQuery = "UPDATE truck_status\n" +
                    "SET truck_out = '00:00:00'\n" +
                    "WHERE truck_id = 1";
            try (PreparedStatement truckOutPrepare = connectionDB.prepareStatement(truckOutTimeQuery)) {
                truckOutPrepare.executeUpdate();
            }

            // Update truck capacity to 100
            String updateTruckCapacityQuery = "UPDATE truck_status\n" +
                    "SET truck_capacity = ?\n" +
                    "WHERE truck_id = 1";
            try (PreparedStatement updateTruckCapacityStatement = connectionDB.prepareStatement(updateTruckCapacityQuery)) {
                updateTruckCapacityStatement.setInt(1, truckCapcityVal);
                updateTruckCapacityStatement.executeUpdate();
            }

            // Reload truck time and warehouse data
            truckTimeLoad();
            loadWarehouseData();
        } catch (SQLException e) {
            logger.error("Error updating warehouse.", e);
            throw new WarehouseUpdateException("Error updating warehouse.", e);
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
