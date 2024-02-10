package org.wms;

public class placedOrders {
    private String Order_ID;
    private String commodityName;
    private int quality;
    private int quantity;

    public placedOrders(String Order_ID, String commodityName, int quality, int quantity) {
        this.Order_ID = Order_ID;
        this.commodityName = commodityName;
        this.quality = quality;
        this.quantity = quantity;
    }

    public String getOrder_ID() {
        return Order_ID;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public int getQuality() {
        return quality;
    }

    public int getQuantity() {
        return quantity;
    }
}
