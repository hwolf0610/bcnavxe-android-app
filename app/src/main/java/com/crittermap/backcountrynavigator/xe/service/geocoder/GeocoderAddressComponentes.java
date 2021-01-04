package com.crittermap.backcountrynavigator.xe.service.geocoder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class GeocoderAddressComponentes {
    @SerializedName("long_name")
    private String longName;
    @SerializedName("short_name")
    private String shortName;
    private List<String> types;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
