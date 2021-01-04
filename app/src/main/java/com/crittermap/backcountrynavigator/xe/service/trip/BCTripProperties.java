package com.crittermap.backcountrynavigator.xe.service.trip;

/**
 * Created by henry on 3/22/2018.
 */

public class BCTripProperties {
    private BCTripColor color;
    private int width;
    private int opacity;

    public BCTripColor getColor() {
        return color;
    }

    public void setColor(BCTripColor color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }
}
