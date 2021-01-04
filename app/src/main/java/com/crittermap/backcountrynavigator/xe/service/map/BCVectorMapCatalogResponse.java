package com.crittermap.backcountrynavigator.xe.service.map;

import java.util.List;

/**
 * Created by valuedcustomer2 on 7/9/18.
 */

public class BCVectorMapCatalogResponse {

    private String shortname;

    private List<BCMapVectorCatalogEntry> areas;


    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }


    public List<BCMapVectorCatalogEntry> getAreas() {
        return areas;
    }

    public void setAreas(List<BCMapVectorCatalogEntry> areas) {
        this.areas = areas;
    }
}
