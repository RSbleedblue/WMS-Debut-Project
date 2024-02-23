package org.wms;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wms.utils.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class adminController implements Initializable {
    private Connection connectionDB;

    private int truckID = generateProcessID();
    public int truckCapcityVal;
    private final String UserName;
    private static final Logger logger = LogManager.getLogger(adminController.class);

    private boolean isGraphVisible = true;


    //    Controller constructor modified.
    public adminController(String UserName) {
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
        this.UserName = UserName;
        try {
            truckCapcityVal = getTruckCapacity();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //  FXML FX: Ids;
    @FXML
    private BarChart<String, Number> areaChart;
    @FXML
    private AnchorPane area_chart_pane;

    @FXML
    private CategoryAxis areaXaxis;

    @FXML
    private NumberAxis areaYaxis;
    @FXML
    private TableView<gatekeeperData> pending_update_table;
    @FXML
    private TableColumn<gatekeeperData, String> pending_col_quality;

    @FXML
    private TableColumn<gatekeeperData, String> pending_col_quantity;

    @FXML
    private TableColumn<gatekeeperData, String> pending_name_col;
    @FXML
    private TableColumn<gatekeeperData, String> pending_id_col;
    @FXML
    private Label pending_update;
    @FXML
    private AnchorPane pendingUpdation_box;
    @FXML
    private Label pendingLabel;
    @FXML
    private Text truck_text_Status;
    @FXML
    private Label historyBTN;
    @FXML
    private AnchorPane graph_change_pane;
    @FXML
    private AnchorPane log_detail_pane;
    @FXML
    private TableView<logDetail> log_detail_table;
    @FXML
    private TableColumn<logDetail, String> log_truck_id;
    @FXML
    private TableColumn<logDetail, String> log_order_id;
    @FXML
    private TableColumn<logDetail, String> log_task_done;
    @FXML
    private TableColumn<logDetail, String> log_truckin;
    @FXML
    private TableColumn<logDetail, String> log_truckout;
    @FXML
    private TableColumn<logDetail, String> log_date;
    @FXML
    private ImageView truck_status_image;
    @FXML
    private Label truck_del_status;
    @FXML
    private Label nameField_dashboard;
    @FXML
    private BarChart<String, Number> barChart_dashboard;
    @FXML
    private PieChart deliveryPieChart;
    @FXML
    private Button addCommodityBtn;
    @FXML
    private Label total_order_detail;

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
    private TableColumn<warehouseData, String> quality_Col;

    @FXML
    private TableColumn<warehouseData, Integer> quantity_Col;

    @FXML
    private TableColumn<warehouseData, Integer> c_ID_Col;

    @FXML
    private TableColumn<warehouseData, String> c_Name_Col;
    @FXML
    private TableView<placedOrders> order_table;
    @FXML
    private TableColumn<placedOrders, String> order_id_col;
    @FXML
    private TableColumn<placedOrders, String> order_name_col;
    @FXML
    private TableColumn<placedOrders, String> order_quality_col;
    @FXML
    private TableColumn<placedOrders, Integer> orders_quantity_col;

    @FXML
    private ImageView truckIN;
    @FXML
    private ImageView pendingImg;

    @FXML
    private ImageView truckOUT;
    @FXML
    private TextField truck_capacity_orderField;

    @FXML
    private ComboBox<String> commodity_Select;
    @FXML
    private TextField quantity_select;
    @FXML
    private ComboBox<String> quality_select;
    @FXML
    private Label truckID_label;
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
    private Button signoutBTN_dashboard;
    @FXML
    private ImageView order_sec_image;

    //    Custom Exception classes
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

    //    When Admin Controller is triggered initialize method loads the data.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//     Default loading for the dashboard
        nameField_dashboard.setText(UserName);
        initializeImages();
        getOrdersPending();
//        loadWarehouseData();
        initializeDropdowns();
        loadDeliverOrder();
        setTruckID();
        loadLogData();
        setPending_updateTexT();
        getUpdateData();
        setupPendingUpdateTable();
        try {
            loadTotalChart();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            getTotalDeliveryCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            setTruckStatus();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            loadBarChartData("bed");
            loadBarChartData("sofa");
            loadBarChartData("table");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            loadPieChartData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        orderPending.setOnMouseClicked(event -> {
            addCommoditySection.setVisible(false);
            dashboardSection.setVisible(false);
            orders_Section.setVisible(true);
            logger.info("Orders button clicked. Orders section displayed.");
        });
        historyBTN.setOnMouseClicked(event -> {
            if (isGraphVisible) {
                area_chart_pane.setVisible(false);
                log_detail_pane.setVisible(true);
                logger.info("History button clicked. Log table displayed.");
                isGraphVisible = false;
            } else {
                area_chart_pane.setVisible(true);
                log_detail_pane.setVisible(false);
                logger.info("History button clicked. Graph displayed.");
                isGraphVisible = true;
            }
        });
        pendingLabel.setOnMouseClicked(event -> {
            addCommoditySection.setVisible(true);
            dashboardSection.setVisible(false);
            orders_Section.setVisible(false);
            logger.info("Pending button clicked. Orders section displayed.");
        });

    }

    //    Pie Chart Data, Bar Chart Data and Truck Status
    private void loadPieChartData() throws SQLException {
        ObservableList<PieChart.Data> piechartData = FXCollections.observableArrayList();
        int totalDelivered = getDeliveryCount("delivered");
        int notDelivered = getDeliveryCount("not_delivered");

        PieChart.Data deliveredData = new PieChart.Data("Delivered", totalDelivered);
        PieChart.Data notDeliveredData = new PieChart.Data("Not Delivered", notDelivered);

        piechartData.addAll(deliveredData, notDeliveredData);

        // Set the piechartData to the deliveryPieChart
        deliveryPieChart.setData(piechartData);
    }

    private void loadBarChartData(String commodityName) throws SQLException {
        ObservableList<OrderItem> commodityData = getBarChartData(commodityName);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(commodityName.toUpperCase());
        MapQuality map = new MapQuality();
        // Clear existing data from the series
        barChart_dashboard.getData().removeIf(s -> s.getName().equalsIgnoreCase(commodityName));
        // Add data points representing each quality
        for (int i = 0; i < commodityData.size(); i++) {
            series.getData().add(new XYChart.Data<>(map.getQualitiesName(i + 1), commodityData.get(i).getQuantity()));
        }

        // Add series to the chart
        barChart_dashboard.getData().add(series);
        barChart_dashboard.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
    }

    private ObservableList<OrderItem> getBarChartData(String name) throws SQLException {
        ObservableList<OrderItem> chartData = FXCollections.observableArrayList();
        String query = "SELECT * FROM commodities WHERE name = ?";
        try (
                PreparedStatement statement = connectionDB.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int quality = result.getInt("quality");
                    int quantity = result.getInt("quantity");
                    chartData.add(new OrderItem(name, quality, quantity));
                }
            }
        }
        return chartData;
    }

    private void setTruckStatus() throws SQLException {
        boolean isIn = isTruckIn();
        if (isIn) {
            truck_del_status.setText("IN");
            truck_text_Status.setText("IN");
            truck_del_status.setTextFill(Color.GREEN);
            truck_text_Status.setFill(Color.GREEN);
        } else {
            truck_del_status.setText("OUT");
            truck_text_Status.setText("OUT");

            truck_del_status.setTextFill(Color.RED);
            truck_text_Status.setFill(Color.RED);
        }
    }

    private void getTotalDeliveryCount() throws SQLException {
        int totalCount = getDeliveryCount("delivered");
        total_order_detail.setText(String.valueOf(totalCount));
    }

    private int getDeliveryCount(String status) throws SQLException {
        String query = "SELECT COUNT(*) AS delivered_count\n" +
                "FROM order_detail\n" +
                "WHERE delivery_status = ?";
        PreparedStatement preparedStatement = connectionDB.prepareStatement(query);
        preparedStatement.setString(1, status);
        ResultSet resultSet = preparedStatement.executeQuery();
        int count = -1;
        while (resultSet.next()) {
            count = resultSet.getInt("delivered_count");
        }
        return count;
    }

    private boolean isTruckIn() throws SQLException {
        String query = "SELECT truck_active FROM truck_status LIMIT 1"; // Limit the query to one row
        try (PreparedStatement statement = connectionDB.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() && resultSet.getBoolean("truck_active");
        }
    }

    //  To load images in the admin view
    private void initializeImages() {
        Image adminImg = loadImage("WMSLoginPage.png");
        Image addImg = loadImage("add.png");
        Image dashboardImg = loadImage("dashboard.png");
        Image orderIconImg = loadImage("orders.png");
        Image truckIn = loadImage("truckIN.png");
        Image truckOut = loadImage("truckOUT.png");
        Image pendingIMG = loadImage("pending.png");
        Image truckStatusIMG = loadImage("truckStatus.png");
        Image defaultImgCom = loadImage("add.png");

        addIcon.setImage(addImg);
        adminIcon.setImage(adminImg);
        dashboardIcon.setImage(dashboardImg);
        orderIcon.setImage(orderIconImg);
        truckIN.setImage(truckIn);
        truckOUT.setImage(truckOut);
        pendingImg.setImage(pendingIMG);
        truck_status_image.setImage(truckStatusIMG);

        logger.info("Images initialized successfully.");
    }

    //  To initialize images helper method
    private Image loadImage(String filename) {
        File file = new File("images/" + filename);
        return new Image(file.toURI().toString());
    }

    private void initializeDropdowns() {
        commodity_Select.setItems(FXCollections.observableArrayList("Bed", "Sofa", "Table"));
        quality_select.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));
        logger.info("Dropdowns initialized successfully.");
    }

    private ObservableList<logDetail> logList;

    public void loadLogData() {
        logList = getLogData();
        log_truck_id.setCellValueFactory(new PropertyValueFactory<>("truckID"));
        log_order_id.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        log_task_done.setCellValueFactory(new PropertyValueFactory<>("taskDone"));
        log_truckin.setCellValueFactory(new PropertyValueFactory<>("truckIn"));
        log_truckout.setCellValueFactory(new PropertyValueFactory<>("truckOut"));
        log_date.setCellValueFactory(new PropertyValueFactory<>("day"));

        // Load data into TableView
        log_detail_table.setItems(logList);
    }

    private ObservableList<logDetail> getLogData() {
        ObservableList<logDetail> logDetails = FXCollections.observableArrayList();
        String query = "SELECT Truck_ID, Order_ID, truck_in, truck_out, day FROM log_detail";
        try (PreparedStatement logStatement = connectionDB.prepareStatement(query)) {
            ResultSet resultSet = logStatement.executeQuery();
            while (resultSet.next()) {
                String truckID = resultSet.getString("Truck_ID");
                String orderID = resultSet.getString("Order_ID");
                String truckIn = resultSet.getString("truck_in");
                String truckOut = resultSet.getString("truck_out");
                String taskDone = "";
                String day = resultSet.getString("day");

                // Set task_done based on truck_in and truck_out
                if (truckIn == null) {
                    taskDone = "loaded";
                } else if (truckOut == null) {
                    taskDone = "Unloaded";
                }
                logDetails.add(new logDetail(truckID, orderID, taskDone, truckIn, truckOut, day));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logDetails;
    }

    public ObservableList<warehouseData> getWareHouseData() {
        String query = "SELECT * FROM commodities";
        ObservableList<warehouseData> warehouseData = FXCollections.observableArrayList();
        try {
            PreparedStatement prepare = connectionDB.prepareStatement(query);
            ResultSet result = prepare.executeQuery();
            while (result.next()) {
                int c_id = Integer.parseInt(result.getString("c_ID"));
                String name = result.getString("name").toUpperCase();
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

    //  Action Buttons to switch in the Admin pages
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
        } else if (event.getSource() == orderPending) {
            addCommoditySection.setVisible(false);
            dashboardSection.setVisible(false);
            orders_Section.setVisible(true);
            logger.info("Orders button clicked. Orders section displayed.");
        }
    }

    //  Loading Pending orders which are not delivered.
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
        MapQuality map = new MapQuality();
        order_id_col.setCellValueFactory(new PropertyValueFactory<>("Order_ID"));
        order_name_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCommodityName().toUpperCase()));
        order_quality_col.setCellValueFactory(cellData -> new SimpleStringProperty(map.getQualitiesName(cellData.getValue().getQuality())));
        orders_quantity_col.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        order_table.setItems(placedList);
        logger.info("Loaded not delivered orders into the table.");
    }

    public void selectOrderList() throws SQLException {
        placedOrders pOrds = order_table.getSelectionModel().getSelectedItem();
        if (pOrds == null) {
            logger.warn("No order selected.");
            showSuccessAlert("No Order Selected", null, "Please select an order.");
            return;
        }
        String selectedCommodity = pOrds.getCommodityName();
        if (selectedCommodity == null || selectedCommodity.isEmpty()) {
            // Selected commodity is null or empty, show an alert
            logger.warn("Selected commodity is null or empty.");
            showSuccessAlert("Commodity Not Found", null, "The selected commodity is not available.");
            return;
        }
        MapQuality map = new MapQuality();
        orderID_field_order.setText(pOrds.getOrder_ID());
        name_field_order.setText(pOrds.getCommodityName().toUpperCase());
        quality_field_order.setText(map.getQualitiesName(pOrds.getQuality()).toUpperCase());
        quantity_field_order.setText(Integer.toString(pOrds.getQuantity()));

        int QuantityStatusVal = getRemainingQuantity(pOrds.getQuality(), pOrds.getCommodityName());
        rem_storage_field_order.setText(Integer.toString(QuantityStatusVal));
        truck_capacity_orderField.setText(Integer.toString(truckCapcityVal));

        order_sec_image.setImage(setImageByCommodityName(pOrds.getCommodityName()));
    }

    private Image setImageByCommodityName(String commodityName) {
        String imageName = "";
        switch (commodityName) {
            case "bed":
                imageName = "bed.png";
                break;
            case "sofa":
                imageName = "sofa.png";
                break;
            case "table":
                imageName = "table.png";
                break;
            default:
                break;
        }
        if (!imageName.isEmpty()) {
            File imageFile = new File("images/" + imageName);
            Image image = new Image(imageFile.toURI().toString());
            return image;
        }
        return null;

    }

    //    Method of loading orders into the Truck.
    private ArrayList<placedOrders> pushOrders = new ArrayList<>();

    public void pushOrder() throws PushOrderException, SQLException {
//        if (!isTruckIn()) {
//            showErrorAlert("ERROR", "Unavailable", "Truck is out for Delivery");
//            logger.error("Truck is out for Delivery");
//            return;
//        }
//        Check for Pending Updation
        int pendingUpdation = getUpdatePending();
        if (pendingUpdation != 0) {
            showErrorAlert("Error", "Update", "First Update the Warehouse");
            logger.error("Updates Pending");
            return;
        }
        try {
            placedOrders pOds = order_table.getSelectionModel().getSelectedItem();
            if (pOds == null) {
                showErrorAlert("Error", null, "Select order to push!");
                return;
            }

            int remainingQuantity = getRemainingQuantity(pOds.getQuality(), pOds.getCommodityName());
            int orderQuantity = pOds.getQuantity();

            if (orderQuantity > remainingQuantity) {
                showErrorAlert("Error", null, "Quantity is not sufficient in warehouse");
            } else if (orderQuantity > truckCapcityVal) {
                showErrorAlert("Error", null, "Truck capacity is full");
            } else {
                // Update global truck capacity
                truckCapcityVal -= orderQuantity;
                truck_capacity_orderField.setText(Integer.toString(truckCapcityVal));

                // Update database with new quantity
                String updateQuantityQuery = "UPDATE commodities SET quantity = quantity - ? WHERE name = ? AND quality = ?";
                try (PreparedStatement updateQuantityStatement = connectionDB.prepareStatement(updateQuantityQuery)) {
                    updateQuantityStatement.setInt(1, orderQuantity);
                    updateQuantityStatement.setString(2, pOds.getCommodityName());
                    updateQuantityStatement.setInt(3, pOds.getQuality());
                    updateQuantityStatement.executeUpdate();
                }

                // Update remaining quantity in UI
                int updatedRemainingQuantity = remainingQuantity - orderQuantity;
                rem_storage_field_order.setText(Integer.toString(updatedRemainingQuantity));

                // Remove order from the placedList
                placedList.remove(pOds);
                pushOrders.add(pOds);

                loadBarChartData("bed");
                loadBarChartData("sofa");
                loadBarChartData("table");

                showSuccessAlert("Loaded", null, "Order with ID: " + pOds.getOrder_ID() + " has been loaded into the truck");
            }
        } catch (SQLException e) {
            logger.error("Error pushing order", e);
            throw e;
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
            // Log the exception or handle it appropriately
            throw new SQLException("Error retrieving truck capacity", e);
        }
    }

    //  If admin cancels the truck load.
    public void cancelPushedOrders() throws SQLException, CancelPushedOrdersException {
        try {
            if (pushOrders.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Truck is Empty");
                alert.showAndWait();
                return;
            }
            int idx = pushOrders.size() - 1;
            placedOrders canceledOrder = pushOrders.get(idx);

            // Update global truck capacity
            truckCapcityVal += canceledOrder.getQuantity();

            // Update database with the canceled quantity
            String updateQuantityQuery = "UPDATE commodities SET quantity = quantity + ? WHERE name = ? AND quality = ?";
            try (PreparedStatement updateQuantityStatement = connectionDB.prepareStatement(updateQuantityQuery)) {
                updateQuantityStatement.setInt(1, canceledOrder.getQuantity());
                updateQuantityStatement.setString(2, canceledOrder.getCommodityName());
                updateQuantityStatement.setInt(3, canceledOrder.getQuality());
                updateQuantityStatement.executeUpdate();
            }

            // Update the truck capacity field
            truck_capacity_orderField.setText(Integer.toString(truckCapcityVal));

            // Clear the list of pushed orders
            pushOrders.remove(idx);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Loaded");
            alert.setContentText("Order with ID: " + canceledOrder.getOrder_ID() + " has been unloaded from the truck");
            alert.showAndWait();

            // Re-add the canceled order back to the table view
            placedList.add(canceledOrder);
            loadBarChartData("bed");
            loadBarChartData("sofa");
            loadBarChartData("table");
//            loadWarehouseData();
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

    //    Method to load commodity Name.
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

    //    When Place Order button triggers placeOrder is triggered.
    public void placeOrder() throws SQLException {
        if (pushOrders.isEmpty()) {
            showErrorAlert("Error", null, "Push orders to place");
            logger.error("No orders pushed to place");
            return;
        }
        try {
            // Update delivery status for placed orders
            String updateDeliveryQuery = "UPDATE order_detail SET delivery_status = 'delivered' WHERE order_id =  ? and quality = ?";
            try (PreparedStatement updateDeliveryStatement = connectionDB.prepareStatement(updateDeliveryQuery)) {
                for (placedOrders pOds : pushOrders) {
                    updateDeliveryStatement.setString(1, pOds.getOrder_ID());
                    updateDeliveryStatement.setInt(2, pOds.getQuality());
                    updateDeliveryStatement.addBatch();
                }
                updateDeliveryStatement.executeBatch();
            }

            // Update truck out time and status
            LocalTime time = LocalTime.now();
            String updateTruckOutTimeQuery = "UPDATE truck_status SET truck_out = ?, truck_active = 0 WHERE truck_id = 1";
            try (PreparedStatement updateTruckOutTimeStatement = connectionDB.prepareStatement(updateTruckOutTimeQuery)) {
                updateTruckOutTimeStatement.setObject(1, time);
                updateTruckOutTimeStatement.executeUpdate();
            }

            // Update truck capacity
            String updateTruckCapacityQuery = "UPDATE truck_status SET truck_capacity = ? WHERE truck_id = 1";
            try (PreparedStatement updateTruckCapacityStatement = connectionDB.prepareStatement(updateTruckCapacityQuery)) {
                updateTruckCapacityStatement.setInt(1, truckCapcityVal);
                updateTruckCapacityStatement.executeUpdate();
            }
            // Updating Log_Table
            String logTruckDetailQuery = "Insert into log_detail (Order_ID, Truck_ID, Quality, Quantity, truck_out,day) values (?, ?, ?, ?, ?,?)";
            try (PreparedStatement insertLogStatement = connectionDB.prepareStatement(logTruckDetailQuery)) {
                for (placedOrders pOds : pushOrders) {
                    insertLogStatement.setString(1, pOds.getOrder_ID());
                    insertLogStatement.setInt(2, Integer.parseInt(truckID_label.getText()));
                    insertLogStatement.setInt(3, pOds.getQuality());
                    insertLogStatement.setInt(4, pOds.getQuantity());
                    insertLogStatement.setObject(5, time);
                    insertLogStatement.setObject(6, java.sql.Date.valueOf(LocalDate.now()));
                    insertLogStatement.executeUpdate();
                }
            }

            // Reload data and clear orders list
            loadDeliverOrder();
            getOrdersPending();
            loadPieChartData();
            setTruckStatus();
            loadLogData();
//            loadWarehouseData();
            getTotalDeliveryCount();
            loadBarChartData("bed");
            loadBarChartData("sofa");
            loadBarChartData("table");
            pushOrders.clear();
            showSuccessAlert("Success", null, "Order is Dispatched Successfully");
        } catch (SQLException e) {
            logger.error("Error updating truck status or reloading data", e);
            throw e;
        }
    }

    //    Custom Show Error Alert.
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //    Custom Show Success Alert.
    private void showSuccessAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //    Update Warehouse method to load Data
    public void updateWarehouse() throws WarehouseUpdateException, SQLException {
        try {
            if (commodity_Select.getValue() == null || quantity_select.getText().trim().isEmpty() || quality_select.getValue() == null) {
                showErrorAlert("Error", null, "Fill in the Details");
                return;
            }

            // Set truck capacity to 20
            truckCapcityVal = 20;
            MapQuality map = new MapQuality();
            String c_Name = commodity_Select.getValue().toLowerCase();
            Integer quality = map.getQualitiesValues(quality_select.getValue().toLowerCase());
            int quantity;
            try {
                quantity = Integer.parseInt(quantity_select.getText().trim());
            } catch (NumberFormatException e) {
                logger.error("Invalid quantity entered", e);
                showErrorAlert("Invalid Quantity", null, "Please enter a valid Integer for Quantity");
                return;
            }

            // Validate commodity name and quality
            if (c_Name == null) {
                logger.error("Commodity name or quality is not selected");
                showErrorAlert("Invalid Selection", null, "Please select commodity and quality");
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

            // Update truck active status
            String updateTruckStatusQuery = "UPDATE truck_status SET truck_active = '1' WHERE truck_id = 1";
            try (PreparedStatement updateTruckStatusStatement = connectionDB.prepareStatement(updateTruckStatusQuery)) {
                updateTruckStatusStatement.executeUpdate();
            }

            // Log truck details
            String logTruckQuery = "INSERT INTO log_detail (Truck_ID, Quality, Quantity, truck_in, day) VALUES (?, ?, ?, ?,?)";
            try (PreparedStatement logTruckStatement = connectionDB.prepareStatement(logTruckQuery)) {
                logTruckStatement.setInt(1, Integer.parseInt(truckID_label.getText()));
                logTruckStatement.setInt(2, quality);
                logTruckStatement.setInt(3, quantity);
                logTruckStatement.setObject(4, time);
                logTruckStatement.setObject(5, java.sql.Date.valueOf(LocalDate.now()));
                logTruckStatement.executeUpdate();
            }

            // Update truck capacity to 20
            String updateTruckCapacityQuery = "UPDATE truck_status\n" +
                    "SET truck_capacity = ?\n" +
                    "WHERE truck_id = 1";
            try (PreparedStatement updateTruckCapacityStatement = connectionDB.prepareStatement(updateTruckCapacityQuery)) {
                updateTruckCapacityStatement.setInt(1, truckCapcityVal);
                updateTruckCapacityStatement.executeUpdate();
            }

            // Change pending status
            String changePendingStatus = "UPDATE gatekeeper_data\n" +
                    "SET status = 'updated'\n" +
                    "WHERE ID = ?";
            if (pending_update_table.getSelectionModel().getSelectedItem() == null) {
                showErrorAlert("Error", null, "No Updation left");
                return;
            }
            try (PreparedStatement updatePendingStatusStatement = connectionDB.prepareStatement(changePendingStatus)) {
                updatePendingStatusStatement.setInt(1, pending_update_table.getSelectionModel().getSelectedItem().getId());
                updatePendingStatusStatement.executeUpdate();
            }

            // Reload data
            setTruckStatus();
            loadBarChartData("bed");
            loadBarChartData("sofa");
            loadBarChartData("table");
            setTruckID();
            loadLogData();
            getUpdateData();
            setPending_updateTexT();
            showSuccessAlert("Update", "success", "Warehouse updated Successfully");

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

    //    Sign Out button to close the portal and session
    public void signoutAdmin(ActionEvent e) throws IOException {
        Stage stage = (Stage) signoutBTN_dashboard.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    private int generateProcessID() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connect = connection.getConnection();
        String query = "SELECT process FROM gatekeeper_data ORDER BY process DESC LIMIT 1";
        int id = 0;
        try (PreparedStatement statement = connect.prepareStatement(query)){
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                id = resultSet.getInt("process");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println(id);
        return id;
    }
    private void setTruckID() {
        truckID_label.setText(Integer.toString(generateProcessID()));
    }

    private void setPending_updateTexT() {
        int pending = getUpdatePending();
        if (pending != 0) {
            pending_update.setText(Integer.toString(pending));
            pending_update.setTextFill(Color.RED);
            pendingLabel.setTextFill(Color.RED);
            return;
        }
        pending_update.setText("0");
        pending_update.setTextFill(Color.GREEN);
        pendingLabel.setTextFill(Color.GREEN);

    }

    private int getUpdatePending() {
        int updatePendingCount = 0;
        String query = "SELECT COUNT(*) FROM gatekeeper_data WHERE status = 'not_updated'";
        try (PreparedStatement statement = connectionDB.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                updatePendingCount = resultSet.getInt(1); // Retrieve the count from the first column
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately, such as logging or rethrowing
        }
        return updatePendingCount;
    }

    private void populateCommoditySelect(gatekeeperData selectedItem) {
        commodity_Select.setValue(selectedItem.getCname());
        quality_select.setValue(selectedItem.getQuality());
        quantity_select.setText(selectedItem.getQuantity());
    }

    private void setupPendingUpdateTable() {
        pending_update_table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Handle single click
                gatekeeperData selectedItem = pending_update_table.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    populateCommoditySelect(selectedItem);
                }
            }
        });
    }

    ObservableList<gatekeeperData> getKeeperData;

    private void getUpdateData() {
        getKeeperData = getUpdateGatekeeperData();
        pending_name_col.setCellValueFactory(new PropertyValueFactory<>("cname"));
        pending_col_quality.setCellValueFactory(new PropertyValueFactory<>("quality"));
        pending_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pending_id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        pending_update_table.setItems(getKeeperData);
    }

    private ObservableList<gatekeeperData> getUpdateGatekeeperData() {
        ObservableList<gatekeeperData> data = FXCollections.observableArrayList();
        MapQuality map = new MapQuality();
        gatekeeperData gkd;
        String query = "Select * from gatekeeper_data where status = 'not_updated'";
        try (PreparedStatement gatekeeperDataStatement = connectionDB.prepareStatement(query)) {
            ResultSet resultSet = gatekeeperDataStatement.executeQuery();
            while (resultSet.next()) {
                String cname = resultSet.getString("c_name").toUpperCase();
                String quality = map.getQualitiesName(resultSet.getInt("quality"));
                String quantity = Integer.toString(resultSet.getInt("quantity"));
                int id = resultSet.getInt("ID");
                gkd = new gatekeeperData(cname, quality, quantity, id);
                data.add(gkd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    ObservableList<OrderItem> areaChartData;
    private void loadTotalChart() throws SQLException {
        // Get total quantity for each commodity
        int totalBedQuantity = getTotalQuantityForCommodity("bed");
        int totalSofaQuantity = getTotalQuantityForCommodity("sofa");
        int totalTableQuantity = getTotalQuantityForCommodity("table");

        // Create series for all commodities
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // Add data points for each commodity
        series.getData().add(new XYChart.Data<>("Bed", totalBedQuantity));
        series.getData().add(new XYChart.Data<>("Sofa", totalSofaQuantity));
        series.getData().add(new XYChart.Data<>("Table", totalTableQuantity));

        // Clear existing data from the chart and add the new series
        areaChart.getData().clear();
        areaChart.getData().add(series);

        // Set different colors for each data point
        series.getData().forEach(data -> {
            switch (data.getXValue().toString()) {
                case "Bed":
                    data.getNode().setStyle("-fx-bar-fill: #474F7A;");
                    break;
                case "Sofa":
                    data.getNode().setStyle("-fx-bar-fill: #81689D;");
                    break;
                case "Table":
                    data.getNode().setStyle("-fx-bar-fill: #FFD0EC;");
                    break;
                default:
                    break;
            }
        });

        // Set the background color of the chart plot to transparent
        areaChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
    }

    private int getTotalQuantityForCommodity(String commodityName) {
        // Mapping commodity names to IDs
        int commodityID = getCommodityID(commodityName);

        if (commodityID == -1) {
            // Handle the case where commodityName is not recognized
            System.err.println("Unknown commodity: " + commodityName);
            return 0; // Or throw an exception, depending on your requirements
        }

        int totalQuantity = 0;
        String query = "SELECT SUM(quantity) AS total_quantity FROM order_detail WHERE commodity_ID = ?";

        try (PreparedStatement statement = connectionDB.prepareStatement(query)) {
            statement.setInt(1, commodityID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalQuantity = resultSet.getInt("total_quantity");
                }
            }
        } catch (SQLException e) {
            // Handle database errors appropriately
            System.err.println("Error fetching total quantity for commodity: " + commodityName);
            e.printStackTrace(); // Log the exception or handle it according to your application's needs
        }

        return totalQuantity;
    }

    private int getCommodityID(String commodityName) {
        switch (commodityName.toLowerCase()) {
            case "bed":
                return 1;
            case "sofa":
                return 2;
            case "table":
                return 3;
            default:
                return -1; // Indicate an unknown commodity
        }
    }
}
