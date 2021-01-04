package com.crittermap.backcountrynavigator.xe.service.trip.geometry;

import com.esri.arcgisruntime.geometry.Envelope;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class BCTripGeometryEnvelope extends BCTripGeometry<Envelope> {
    @Override
    public void deserialize(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        double xmin = jsonObject.get("xmin").getAsDouble();
        double ymin = jsonObject.get("ymin").getAsDouble();
        double xmax = jsonObject.get("xmax").getAsDouble();
        double ymax = jsonObject.get("ymax").getAsDouble();

        this.coordinates = new Envelope(xmin, ymin, xmax, ymax, null);

    }
}
