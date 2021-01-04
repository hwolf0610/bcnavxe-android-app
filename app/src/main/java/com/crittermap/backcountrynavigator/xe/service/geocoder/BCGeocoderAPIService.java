package com.crittermap.backcountrynavigator.xe.service.geocoder;

import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.crittermap.backcountrynavigator.xe.share.BCConstant.TIME_OUT;

public class BCGeocoderAPIService {

    private BCIGeocoderAPIService geocoderAPIService;
    private static BCGeocoderAPIService instance;

    private BCGeocoderAPIService() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BCConstant.GEOCODER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        geocoderAPIService = retrofit.create(BCIGeocoderAPIService.class);
    }

    public static BCGeocoderAPIService getInstance() {
        if (instance == null) {
            instance = new BCGeocoderAPIService();
        }
        return instance;
    }

    public void reverseGeocoding(double latitude, double longitude, final WebServiceCallBack<BCGeocoderData> callBack) {
        String latLong = String.format("%s,%s", String.valueOf(latitude), String.valueOf(longitude));

        Call<GeocoderResponse> request = geocoderAPIService.reverseGeocoding(latLong, BCConstant.GEOCODER_API_KEY);
        request.enqueue(new Callback<GeocoderResponse>() {
            @Override
            public void onResponse(Call<GeocoderResponse> call, Response<GeocoderResponse> response) {
                if (response.isSuccessful() && response.body() != null && "OK".equals(response.body().getStatus())) {
                    String countryCode = GeocoderUtils.getCountryCode(response.body());
                    String stateCode = GeocoderUtils.getStateCode(response.body());
                    callBack.onSuccess(new BCGeocoderData(countryCode, stateCode));
                } else {
                    callBack.onFailed("Error");
                    Logger.e("BCGeocoderAPIService", "reverseGeocoding failed");
                }
            }

            @Override
            public void onFailure(Call<GeocoderResponse> call, Throwable t) {
                callBack.onFailed("Error");
                Logger.e("BCGeocoderAPIService", t.getMessage());

            }
        });

    }
}
