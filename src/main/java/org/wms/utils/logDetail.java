package org.wms.utils;

import javafx.beans.property.SimpleStringProperty;

public class logDetail {
    private final SimpleStringProperty truckID;
    private final SimpleStringProperty orderID;
    private final SimpleStringProperty taskDone;
    private final SimpleStringProperty truckIn;
    private final SimpleStringProperty truckOut;
    private final SimpleStringProperty day;

    public logDetail(String truckID, String orderID, String taskDone, String truckIn, String truckOut, String day) {
        this.truckID = new SimpleStringProperty(truckID);
        this.orderID = new SimpleStringProperty(orderID);
        this.taskDone = new SimpleStringProperty(taskDone);
        this.truckIn = new SimpleStringProperty(truckIn);
        this.truckOut = new SimpleStringProperty(truckOut);
        this.day = new SimpleStringProperty(day);
    }

    public String getTruckID() {
        return truckID.get();
    }

    public String getOrderID() {
        if(orderID.get() == null){
            return "-----";
        }
        return orderID.get();
    }

    public String getTaskDone() {
        return taskDone.get();
    }

    public String getTruckIn() {
        if(truckIn.get() == null){
            return "-----";
        }
        return truckIn.get();
    }

    public String getTruckOut() {
        if(truckOut.get() == null){
            return "-----";
        }
        return truckOut.get();
    }
    public String getDay(){
        return day.get();
    }
}