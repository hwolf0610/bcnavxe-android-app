package com.crittermap.backcountrynavigator.xe.service.geocoder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class GeocoderResult {
    @SerializedName("address_components")
    private List<GeocoderAddressComponentes> addressComponent;

    public List<GeocoderAddressComponentes> getAddressComponent() {
        return addressComponent;
    }

    public void setAddressComponent(List<GeocoderAddressComponentes> addressComponent) {
        this.addressComponent = addressComponent;
    }
}
