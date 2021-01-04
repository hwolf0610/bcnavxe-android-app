package com.crittermap.backcountrynavigator.xe.service.map;

import java.util.List;

/**
 * Created by henry on 4/21/2018.
 */

public class BCMapResponse {
    private List<BCMapSource> mapSources;

    public List<BCMapSource> getMapSources() {
        return mapSources;
    }

    public void setMapSources(List<BCMapSource> mapSources) {
        this.mapSources = mapSources;
    }
}
