package com.crittermap.backcountrynavigator.xe.service.geocoder;

public class GeocoderUtils {

    public static String getCountryCode(GeocoderResponse response) {
        if (response == null) return "";
        for (GeocoderResult result : response.getResults()) {
            for (GeocoderAddressComponentes addressComponentes : result.getAddressComponent()) {
                if (addressComponentes.getTypes().contains("country")) {
                    return addressComponentes.getShortName();
                }
            }
        }
        return "";
    }

    public static String getStateCode(GeocoderResponse response) {
        if (response == null) return "";
        for (GeocoderResult result : response.getResults()) {
            for (GeocoderAddressComponentes addressComponentes : result.getAddressComponent()) {
                if (addressComponentes.getTypes().contains("administrative_area_level_1")) {
                    return addressComponentes.getShortName();
                }
            }
        }

        return "";
    }
}
