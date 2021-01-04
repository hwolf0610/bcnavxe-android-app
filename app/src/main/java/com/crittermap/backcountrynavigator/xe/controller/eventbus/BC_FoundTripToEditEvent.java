package com.crittermap.backcountrynavigator.xe.controller.eventbus;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;

/**
 * Created by LanhNguyen on 4/18/2018.
 */

public class BC_FoundTripToEditEvent {
    private String tripId;
    private String tripName;
    private GraphicsOverlay graphicsOverlay;
    private Point tapPoint;
    public BC_FoundTripToEditEvent(String tripId, String tripName, GraphicsOverlay graphicsOverlay) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.graphicsOverlay = graphicsOverlay;
    }

    public BC_FoundTripToEditEvent(String tripId, String tripName, GraphicsOverlay graphicsOverlay, Point tapPoint) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.graphicsOverlay = graphicsOverlay;
        this.tapPoint = tapPoint;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public GraphicsOverlay getGraphicsOverlay() {
        return graphicsOverlay;
    }

    public void setGraphicsOverlay(GraphicsOverlay graphicsOverlay) {
        this.graphicsOverlay = graphicsOverlay;
    }

    public String getTripName() {
        return tripName;
    }
}
