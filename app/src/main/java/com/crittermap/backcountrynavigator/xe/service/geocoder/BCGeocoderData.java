package com.crittermap.backcountrynavigator.xe.service.geocoder;

public class BCGeocoderData {
    private String countryCode;
    private String stateCode;

    public BCGeocoderData(String countryCode, String stateCode) {
        this.countryCode = countryCode;
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
}
