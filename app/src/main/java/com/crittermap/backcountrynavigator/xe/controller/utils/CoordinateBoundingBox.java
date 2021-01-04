package com.crittermap.backcountrynavigator.xe.controller.utils;

/**
 * Created by nhat@saveondev.com on 12/22/17.
 */

public class CoordinateBoundingBox {
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;

    public CoordinateBoundingBox(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }
}
