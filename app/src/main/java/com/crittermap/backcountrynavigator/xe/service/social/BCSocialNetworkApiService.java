package com.crittermap.backcountrynavigator.xe.service.social;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.service.BCBaseResponse;
import com.crittermap.backcountrynavigator.xe.service.BCUserService;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.singleton.BCSingleton;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nhatdear on 3/10/18.
 */

public class BCSocialNetworkApiService implements BCISocialNetworkApiService {

    private static final BCSocialNetworkApiService ourInstance = new BCSocialNetworkApiService();
    private static final int RESPONSE_CODE_SUCCESS = 1;
    private static final int TIME_OUT = 120;
    private static final String TAG = BCSocialNetworkApiService.class.getSimpleName();
    private BCISocialNetworkApiService socialNetworkApiService;

    private BCSocialNetworkApiService() {
        try {
            Context context = BCSingleton.getInstance().getContext();
            // Loading CAs from an InputStream
            CertificateFactory cf;
            cf = CertificateFactory.getInstance("X.509");

            Certificate ca;
            // I'm using Java7. If you used Java6 close it manually with finally.
            try (InputStream cert = context.getResources().openRawResource(R.raw.bcnaxe)) {
                ca = cf.generateCertificate(cert);
            }

            // Creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Creating a TrustManager that trusts the CAs in our KeyStore.
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(BCConstant.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            socialNetworkApiService = retrofit.create(BCISocialNetworkApiService.class);
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(BCConstant.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            socialNetworkApiService = retrofit.create(BCISocialNetworkApiService.class);
        }
    }

    public static BCSocialNetworkApiService getInstance() {
        return ourInstance;
    }

    public void postSocialNetworkUser(BCSocialNetworkUser user, final WebServiceCallBack<BCUser> callback) {
        Log.d(TAG, "postSocialNetworkUser: " + user);
        try {
            Call<BCBaseResponse<BCUserService>> postSocialNetworkUser = this.postSocialNetworkUser(user);
            postSocialNetworkUser.enqueue(new Callback<BCBaseResponse<BCUserService>>() {
                @Override
                public void onResponse(@NonNull Call<BCBaseResponse<BCUserService>> call, @NonNull Response<BCBaseResponse<BCUserService>> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "postSocialNetworkUser - Response successful: " + response);
                        BCUserService userService = response.body().getPayload();
                        if (userService != null) {
                            callback.onSuccess(new BCUser(userService));
                        } else {
                            Log.d(TAG, "postSocialNetworkUser - Unknown error!!!");
                            callback.onFailed("Unknown error!!!");
                        }
                    } else {
                        Log.d(TAG, "postSocialNetworkUser - " + response.message());
                        callback.onFailed(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BCBaseResponse<BCUserService>> call, @NonNull Throwable t) {
                    Log.d(TAG, "postSocialNetworkUser - " + t.getMessage());
                    callback.onFailed(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailed(e.getMessage());
        }
    }

    @Override
    public Call<BCBaseResponse<BCUserService>> postSocialNetworkUser(BCSocialNetworkUser user) {
        return socialNetworkApiService.postSocialNetworkUser(user);
    }
}
