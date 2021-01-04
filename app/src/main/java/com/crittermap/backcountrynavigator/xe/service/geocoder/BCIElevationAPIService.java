package com.crittermap.backcountrynavigator.xe.service.geocoder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BCIElevationAPIService {
    @GET("maps/api/elevation/json")
    Call<ElevationResponse> getAttitude(@Query("locations") String latLong, @Query("key") String apiKey);
}
