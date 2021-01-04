package com.crittermap.backcountrynavigator.xe.service.trip.geometry;

import com.google.gson.JsonElement;

/**
 * Created by thutrang.dao on 4/8/18.
 */

public abstract class BCTripGeometry<T> {
    private String type;

    protected T coordinates;

    public abstract void deserialize(JsonElement jsonElement);

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(T coordinates) {
        this.coordinates = coordinates;
    }
}
