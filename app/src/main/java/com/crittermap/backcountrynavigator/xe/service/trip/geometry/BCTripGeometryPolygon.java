package com.crittermap.backcountrynavigator.xe.service.trip.geometry;

import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Created by thutrang.dao on 4/8/18.
 */

public class BCTripGeometryPolygon extends BCTripGeometry<Polygon> {
    @Override
    public void deserialize(JsonElement jsonElement) {
        PointCollection collection = new PointCollection(SpatialReferences.getWgs84());

        Gson gson = new Gson();
        double[][][] points = gson.fromJson(jsonElement, double[][][].class);
        for (double[][] aListPoint : points) {
            for (double[] pt: aListPoint) {
                collection.add(pt[0], pt[1]);

            }
        }
        this.coordinates = new Polygon(collection);

    }
}
