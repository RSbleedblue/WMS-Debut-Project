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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wms.utils.MapQuality;

import java.io.File;
import java.io.IOException;
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
    private String UserName;

    public adminController() {
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
        try {
            truckCapcityVal = getTruckCapacity();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public adminController(String UserName){
        DatabaseConnection connection = new DatabaseConnection();
        connectionDB = connection.getConnection();
        this.UserName = UserName;
        try{
            truckCapcityVal = getTruckCapacity();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    @FXML
    private Text truck_text_Status;
    @FXML
    private ImageView truck_status_image;
    @FXML
    private Label truck_del_status;
    @FXML
    private Label nameField_dashboard;
    @FXML
    private ImageView select_commodity_detail_img;
    @FXML
    private StackedBarChart<String, Number> barChart_dashboard;
    @FXML
    private PieChart deliveryPieChart;
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
    private ProgressBar progressBar;

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
    private TextField total_Quantity_com;
    @FXML
    private TextField quality_com;
    @FXML
    private TextField quantity_com;

    @FXML
    private TableView<warehouseData> tableView;
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
    private Label truckINTime;
    @FXML
    private TextField selected_com_update;
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
    private Button signoutBTN_dashboard;
    @FXML
    private ImageView order_sec_image;

    private static final Logger logger = LogManager.getLogger(adminController.class);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        Default loading for the dashboard
        nameField_dashboard.setText(UserName);
        initializeImages();
        getOrdersPending();
        loadWarehouseData();
        initializeDropdowns();
        truckTimeLoad();
        loadDeliverOrder();
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

    }

    private void loadPieChartData() throws SQLException {
        ObservableList<PieChart.Data> piechartData = FXCollections.observableArrayList();
        int totalDelivered = getDeliveryCount("delivered");
        int notDelivered = getDeliveryCount("not_delivered");

        PieChart.Data deliveredData = new PieChart.Data("Delivered", totalDelivered);
        PieChart.Data notDeliveredData = new PieChart.Data("Not Delivered", notDelivered);

        attachToolTip(deliveredData, totalDelivered);
        attachToolTip(notDeliveredData, notDelivered);
        piechartData.addAll(deliveredData, notDeliveredData);

        // Set the piechartData to the deliveryPieChart
        deliveryPieChart.setData(piechartData);
    }
    private void attachToolTip(PieChart.Data data, int value){
        Tooltip tooltip = new Tooltip("Value: "+ value);
        Tooltip.install(data.getNode(),tooltip);
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

    private void loadBarChartData(String commodityName) throws SQLException {
        ObservableList<OrderItem> commodityData = getBarChartData(commodityName);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(commodityName.toUpperCase());

        // Add data points representing each quality
        for (int i = 0; i < commodityData.size(); i++) {
            series.getData().add(new XYChart.Data<>("Quality " + (i + 1), commodityData.get(i).getQuantity()));
        }

        // Add series to the chart
        barChart_dashboard.getData().add(series);
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
    public class invalidQuantity extends  Exception{
        public invalidQuantity(){
            super();
        }
        public invalidQuantity(String message){
            super((message));
        }
    }
    private void setTruckStatus() throws SQLException {
        boolean isIn = isTruckIn();
        if (isIn) {
            truck_del_status.setText("IN");
            truck_text_Status.setText("IN");
        } else {
            truck_del_status.setText("OUT");
            truck_text_Status.setText("OUT");
        }
    }

    private boolean isTruckIn() throws SQLException {
        String query = "SELECT truck_active FROM truck_status LIMIT 1"; // Limit the query to one row
        try (PreparedStatement statement = connectionDB.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() && resultSet.getBoolean("truck_active");
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
                boolean isStatus = result.getBoolean("truck_active");
                if(isStatus){
                    truckINTime.setText(truckIn);
                    truckOutTime.setText("------");
                }
                else{
                    truckINTime.setText("------");
                    truckOutTime.setText(truckIn);
                }

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
        select_commodity_detail_img.setImage(defaultImgCom);

        logger.info("Images initialized successfully.");
    }

    private Image loadImage(String filename) {
        File file = new File("images/" + filename);
        return new Image(file.toURI().toString());
    }

    private void initializeDropdowns() {
        commodity_Select.setItems(FXCollections.observableArrayList("Bed", "Sofa", "Table"));
        quality_select.setItems(FXCollections.observableArrayList("POOR", "BAD", "AVERAGE", "GOOD", "BEST"));
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

    private ObservableList<warehouseData> list;

    public void loadWarehouseData() {
        list = getWareHouseData();
        MapQuality map = new MapQuality();
        c_Name_Col.setCellValueFactory(new PropertyValueFactory<>("name"));
        quality_Col.setCellValueFactory(cellData -> new SimpleStringProperty(map.getQualitiesName(cellData.getValue().getQuality())));
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
        else if(event.getSource() == orderPending){
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
        MapQuality map = new MapQuality();
        order_id_col.setCellValueFactory(new PropertyValueFactory<>("Order_ID"));
        order_name_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCommodityName().toUpperCase()));
        order_quality_col.setCellValueFactory(cellData -> new SimpleStringProperty(map.getQualitiesName(cellData.getValue().getQuality())));
        orders_quantity_col.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        order_table.setItems(placedList);
        logger.info("Loaded not delivered orders into the table.");
    }



    public void selectCommoditiesList() {
        warehouseData whd = tableView.getSelectionModel().getSelectedItem();
        if (whd == null) {
            return;
        }
        select_commodity_detail_img.setImage(setImageByCommodityName(whd.getName().toLowerCase()));
        quantity_Col.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Filter the list to include only items with the selected commodity name
        int totalQuantity = list.stream()
                .filter(item -> item.getName().equals(whd.getName()))
                .mapToInt(warehouseData::getQuantity)
                .sum();

        int selectedQuantity = whd.getQuantity();
        double progress = (double) selectedQuantity / totalQuantity;
        progressBar.setProgress(progress);
        total_Quantity_com.setText(Integer.toString(totalQuantity));

        MapQuality map = new MapQuality();
        String quality = map.getQualitiesName(whd.getQuality());

        quality_com.setText(quality);
        quantity_com.setText(Integer.toString(selectedQuantity));
        selected_com_update.setText(whd.getName().toUpperCase());
    }

    public void selectOrderList() throws SQLException {
        placedOrders pOrds = order_table.getSelectionModel().getSelectedItem();
        if (pOrds == null) {
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
            if (pOds == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Select order to push!");
                alert.showAndWait();
            }
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
                placedList.remove(pOds);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Loaded");
                alert.setContentText("Order with ID: " + pOds.getOrder_ID() + " has been loaded into the truck");
                alert.showAndWait();
            } else if (pOds.getQuantity() >= remainingQuantity && pOds.getQuantity() <= truckCapcityVal) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Quantity are not sufficient in warehouse");
                alert.showAndWait();
            } else if (pOds.getQuantity() >= truckCapcityVal && pOds.getQuantity() <= remainingQuantity) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Truck Capactiy is full");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Both Quantity and truck Capacity is in sufficient");
                alert.showAndWait();
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

    public void cancelPushedOrders() throws SQLException, CancelPushedOrdersException {
        try {
            if(pushOrders.isEmpty()){
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
            try {
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
                }

                // Update the truck out time
                LocalTime time = LocalTime.now();
                String updateTruckOutTimeQuery = "UPDATE truck_status\n" +
                        "SET truck_out = ?\n" +
                        "WHERE truck_id = 1";
                try (PreparedStatement updateTruckOutTimeStatement = connectionDB.prepareStatement(updateTruckOutTimeQuery)) {
                    updateTruckOutTimeStatement.setObject(1, time);
                    updateTruckOutTimeStatement.executeUpdate();
                }

                String updateTruckStatusQuery = "UPDATE truck_status SET truck_active = '0' WHERE truck_id = 1";
                try(PreparedStatement updateTruckStatusStatement= connectionDB.prepareStatement(updateTruckStatusQuery)){
                    updateTruckStatusStatement.executeUpdate();
                }
                // Update the truck capacity in the database
                String updateTruckCapacityQuery = "UPDATE truck_status\n" +
                        "SET truck_capacity = ?\n" +
                        "WHERE truck_id = 1";
                try (PreparedStatement updateTruckCapacityStatement = connectionDB.prepareStatement(updateTruckCapacityQuery)) {
                    updateTruckCapacityStatement.setInt(1, truckCapcityVal);
                    updateTruckCapacityStatement.executeUpdate();
                }

                // Reload the delivered orders
                loadDeliverOrder();
                truckTimeLoad();
                getOrdersPending();
                loadPieChartData();
                setTruckStatus();
                pushOrders.clear();

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

    public void updateWarehouse() throws WarehouseUpdateException, SQLException {
        try {
            // Set truck capacity to 100
            truckCapcityVal = 100;
            MapQuality map = new MapQuality();
            String c_Name = commodity_Select.getValue() != null ? commodity_Select.getValue().toLowerCase() : null;
            Integer quality = map.getQualitiesValues(quality_select.getValue().toLowerCase());
            int quantity;
            try {
                quantity = Integer.parseInt(quantity_select.getText().trim());
            }
            catch (NumberFormatException e) {
                logger.error("Invalid quantity entered", e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Quantity");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid integer for quantity.");
                alert.showAndWait();
                return;
            }

            // Validate commodity name and quality
            if (c_Name == null || quality == null) {
                logger.error("Commodity name or quality is not selected");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select commodity and quality.");
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
            String updateTruckStatusQuery = "UPDATE truck_status SET truck_active = '1' WHERE truck_id = 1";
            try(PreparedStatement updateTruckStatusStatement= connectionDB.prepareStatement(updateTruckStatusQuery)){
                updateTruckStatusStatement.executeUpdate();
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
            loadPieChartData();
            setTruckStatus();
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

    public void signoutAdmin(ActionEvent e) throws IOException {
        Stage stage = (Stage) signoutBTN_dashboard.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
