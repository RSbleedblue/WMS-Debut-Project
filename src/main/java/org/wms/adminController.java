package org.wms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private AnchorPane heroSectionleft;

    @FXML
    private Button homeBtn;

    @FXML
    private PieChart pieChartBed;

    @FXML
    private PieChart pieChartSofa;

    @FXML
    private PieChart pieChartTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File adminLoc = new File("images/admin.jpg");
        File addLoc = new File("images/add.png");
        File dashboardLoc = new File("images/dashboard.png");

        Image adminImg = new Image(adminLoc.toURI().toString());
        Image addImg = new Image(addLoc.toURI().toString());
        Image dashboardImg = new Image(dashboardLoc.toURI().toString());

        addIcon.setImage(addImg);
        adminIcon.setImage(adminImg);
        dashboardIcon.setImage(dashboardImg);

        ObservableList<warehouseData> bedData = getWareHouseData("bed");
        ObservableList<warehouseData> sofaData = getWareHouseData("sofa");
        ObservableList<warehouseData> tableData = getWareHouseData("table");
        bedVisual(bedData);
        sofaVisual(sofaData);
        tableVisual(tableData);

    }

    public ObservableList<warehouseData> getWareHouseData(String commodityName) {
        String query = "SELECT * FROM commodities WHERE name = ?";
        ObservableList<warehouseData> bedPieChartData = FXCollections.observableArrayList();
        try {
            PreparedStatement prepare = connectionDB.prepareStatement(query);
            prepare.setString(1, commodityName);
            ResultSet result = prepare.executeQuery();
            while (result.next()) {
                int c_id = Integer.parseInt(result.getString("c_ID"));
                String name = result.getString("name");
                int quality = Integer.parseInt(result.getString("quality"));
                int quantity = Integer.parseInt(result.getString("quantity"));
                warehouseData whd = new warehouseData(c_id, name, quality, quantity);
                bedPieChartData.add(whd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bedPieChartData;
    }

    private void bedVisual(ObservableList<warehouseData> bedData) {
        pieChartBed.setTitle("BED");
        for (int i = 0; i < bedData.size(); i++) {
            PieChart.Data data = new PieChart.Data("Quality "+ bedData.get(i).getQuality(), bedData.get(i).getQuantity());
            pieChartBed.getData().add(data);
        }
    }


    private void sofaVisual(ObservableList<warehouseData> sofaData) {
        pieChartSofa.setTitle("SOFA");
        for (int i = 0; i < sofaData.size(); i++) {
            PieChart.Data data = new PieChart.Data("Quality " + sofaData.get(i).getQuality(), sofaData.get(i).getQuantity());
            pieChartTable.getData().add(data);
        }
    }

    private void tableVisual(ObservableList<warehouseData> tableData) {
        pieChartTable.setTitle("TABLE");
        for (int i = 0; i < tableData.size(); i++) {
            PieChart.Data data = new PieChart.Data("Quality " + tableData.get(i).getQuality(), tableData.get(i).getQuantity());
            pieChartSofa.getData().add(data);
        }
    }
    public void setHomeBtn(ActionEvent event){
        if(event.getSource() == "homeBtn"){
            addCommoditySection.setVisible(false);
            dashboardSection.setVisible(true);
        }
        else if(event.getSource() == "addCommodityBtn"){
            addCommoditySection.setVisible(true);
            dashboardSection.setVisible(false);
        }
    }
}
