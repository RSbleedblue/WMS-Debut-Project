package org.wms.utils;

public class gatekeeperData {
    private String cname;
    private String quality;
    private String quantity;
    public gatekeeperData(String cname, String quality, String quantity){
        this.cname = cname;
        this.quality = quality;
        this.quantity = quantity;
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
}
