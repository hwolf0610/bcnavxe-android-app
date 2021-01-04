package com.crittermap.backcountrynavigator.xe.service.trip;

import com.crittermap.backcountrynavigator.xe.service.trip.geometry.BCTripGeometry;

/**
 * Created by henry on 3/22/2018.
 */

public class BCTripFeature {
    private String type;
    private BCTripProperties properties;
    private BCTripGeometry geometry;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BCTripProperties getProperties() {
        return properties;
    }

    public void setProperties(BCTripProperties properties) {
        this.properties = properties;
    }

    public BCTripGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(BCTripGeometry geometry) {
        this.geometry = geometry;
    }
}
