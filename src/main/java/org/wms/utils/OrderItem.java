package org.wms.utils;

public class OrderItem {
    private String commodity;
    private int quality;
    private int quantity;

    public OrderItem(String commodity, int quality, int quantity) {
        this.commodity = commodity;
        this.quality = quality;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
