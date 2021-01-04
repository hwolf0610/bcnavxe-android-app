package com.crittermap.backcountrynavigator.xe.controller.eventbus;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;

public class BC_FoundGeometryToEditEvent {
    private Graphic graphic;
    private Point tapPoint;

    public BC_FoundGeometryToEditEvent(Graphic graphic) {
        this.graphic = graphic;
    }

    public BC_FoundGeometryToEditEvent(Graphic graphic, Point tapPoint) {
        this.graphic = graphic;
        this.tapPoint = tapPoint;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public Point getTapPoint() {
        return tapPoint;
    }
}
