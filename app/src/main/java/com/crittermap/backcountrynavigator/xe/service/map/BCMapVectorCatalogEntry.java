package com.crittermap.backcountrynavigator.xe.service.map;

import java.util.List;

/**
 * Created by valuedcustomer2 on 7/9/18.
 */

public class BCMapVectorCatalogEntry {

    private String BlockID;
    private String WebMapID;
    private String AreaID;

    List<BCVectorAreaLayer> layers;


    public String getBlockID() {
        return BlockID;
    }

    public void setBlockID(String blockID) {
        BlockID = blockID;
    }

    public String getWebMapID() {
        return WebMapID;
    }

    public void setWebMapID(String webMapID) {
        WebMapID = webMapID;
    }

    public String getAreaID() {
        return AreaID;
    }

    public void setAreaID(String areaID) {
        AreaID = areaID;
    }

    public List<BCVectorAreaLayer> getLayers() {
        return layers;
    }

    public void setLayers(List<BCVectorAreaLayer> layers) {
        this.layers = layers;
    }
}
