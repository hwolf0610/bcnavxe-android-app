package com.crittermap.backcountrynavigator.xe.service.trip.geometry;

import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Created by thutrang.dao on 4/8/18.
 */

public class BCTripGeometryMultipoint extends BCTripGeometry<Multipoint> {

    @Override
    public void deserialize(JsonElement jsonElement) {
        PointCollection collection = new PointCollection(SpatialReferences.getWgs84());

        Gson gson = new Gson();
        double[][] points = gson.fromJson(jsonElement, double[][].class);
        for (double[] aListPoint : points) {
            collection.add(aListPoint[0], aListPoint[1]);
        }
        this.coordinates = new Multipoint(collection);
    }
}
