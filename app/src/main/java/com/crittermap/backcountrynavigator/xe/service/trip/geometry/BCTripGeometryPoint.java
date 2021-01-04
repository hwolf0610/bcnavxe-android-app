package com.crittermap.backcountrynavigator.xe.service.trip.geometry;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class BCTripGeometryPoint extends BCTripGeometry<Point> {
    @Override
    public void deserialize(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();

        this.coordinates = new Point(x, y, SpatialReferences.getWgs84());
    }
}
