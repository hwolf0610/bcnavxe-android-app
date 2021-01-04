package com.crittermap.backcountrynavigator.xe.service.geocoder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ElevationResult {
    @SerializedName("elevation")
    private Double elevation;

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }
}
