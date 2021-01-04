package com.crittermap.backcountrynavigator.xe.service.geocoder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BCIGeocoderAPIService {
    @GET("maps/api/geocode/json")
    Call<GeocoderResponse> reverseGeocoding(@Query("latlng") String latLong, @Query("key") String apiKey);
}
