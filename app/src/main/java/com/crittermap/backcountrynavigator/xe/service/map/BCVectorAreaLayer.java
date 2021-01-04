package com.crittermap.backcountrynavigator.xe.service.map;

/**
 * Created by valuedcustomer2 on 7/9/18.
 */

public class BCVectorAreaLayer {

    private long Size;
    private String layername;
    private String ID;

    public long getSize() {
        return Size;
    }

    public void setSize(long size) {
        Size = size;
    }

    public String getLayername() {
        return layername;
    }

    public void setLayerName(String layerName) {
        this.layername = layerName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
