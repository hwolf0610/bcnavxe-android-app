package com.crittermap.backcountrynavigator.xe.service.geocoder;

import android.support.annotation.NonNull;

import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.crittermap.backcountrynavigator.xe.share.BCConstant.TIME_OUT;

public class BCElevationAPIService {
    private static String TAG = BCElevationAPIService.class.getSimpleName();
    private BCIElevationAPIService geocoderAPIService;
    private static BCElevationAPIService instance;

    private BCElevationAPIService() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BCConstant.GEOCODER_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        geocoderAPIService = retrofit.create(BCIElevationAPIService.class);
    }

    public static BCElevationAPIService getInstance() {
        if (instance == null) {
            instance = new BCElevationAPIService();
        }
        return instance;
    }

    public void getAttitude(double latitude, double longitude, final WebServiceCallBack<Double> callBack) {
        String latLong = String.format("%s,%s", String.valueOf(latitude), String.valueOf(longitude));

        Call<ElevationResponse> request = geocoderAPIService.getAttitude(latLong, BCConstant.GEOCODER_API_KEY);
        request.enqueue(new Callback<ElevationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ElevationResponse> call, @NonNull Response<ElevationResponse> response) {
                if (response.isSuccessful() && response.body() != null && "OK".equals(response.body().getStatus())) {
                    List<ElevationResult> elevationResults = response.body().getResults();
                    if (elevationResults.size() > 0) {
                        callBack.onSuccess(elevationResults.get(0).getElevation());
                    } else {
                        callBack.onSuccess((double) 0);
                    }
                } else {
                    callBack.onSuccess((double) 0);
                    Logger.e(TAG, "Get Attitude failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ElevationResponse> call, @NonNull Throwable t) {
                callBack.onSuccess((double) 0);
                Logger.e(TAG, t.getMessage());
            }
        });

    }
}
