package org.wms.utils;

public class gatekeeperData {
    private String cname;
    private String quality;
    private String quantity;
    private int id;
    public gatekeeperData(String cname, String quality, String quantity){
        this.cname = cname;
        this.quality = quality;
        this.quantity = quantity;
    }
    public gatekeeperData(String cname, String quality, String quantity, int id){
        this.cname = cname;
        this.quality = quality;
        this.quantity = quantity;
        this.id = id;
    }
    public String getCname(){
        return cname;
    }
    public String getQuality(){
        return  quality;
    }
    public String getQuantity(){
        return  quantity;
    }
    public int getId(){
        return id;
    }
}
