package org.wms;

public class warehouseData {
    private int c_ID;
    private String name;
    private int quality;
    private int quantity;
    public warehouseData(int c_ID, String name, int quality, int quantity){
        this.c_ID = c_ID;
        this.name = name;
        this.quality = quality;
        this.quantity = quantity;
    }
    public int getC_ID(){
        return c_ID;
    }
    public String getName(){
        return name;
    }
    public int getQuality(){
        return quality;
    }
    public int getQuantity(){
        return quantity;
    }
}
