package com.crittermap.backcountrynavigator.xe.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTripDAO;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.data.model.bookmark.BCBookmark;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkData;
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkListResponse;
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkSubmit;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapResponse;
import com.crittermap.backcountrynavigator.xe.service.map.BCVectorMapCatalogResponse;
import com.crittermap.backcountrynavigator.xe.service.membership.BCMembershipData;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripResponse;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripUploadResponse;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.singleton.BCSingleton;
import com.raizlabs.android.dbflow.data.Blob;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nhatdear on 3/10/18.
 */

public class BCApiService implements BCIApiService {

    private static final BCApiService ourInstance = new BCApiService();
    private static final int RESPONSE_CODE_SUCCESS = 1;
    private static final int TIME_OUT = 120;
    //TODO improve time out here, split time out for download/upload
    private static final String TAG = BCApiService.class.getSimpleName();

    public static BCApiService getInstance() {
        return ourInstance;
    }

    private BCIApiService bciApiService;

    private BCApiService() {
        try {
            Context context = BCSingleton.getInstance().getContext();
            // Loading CAs from an InputStream
            CertificateFactory cf = null;
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
            bciApiService = retrofit.create(BCIApiService.class);
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
            bciApiService = retrofit.create(BCIApiService.class);
        }
    }

    public BCUser doLogin(BCUser bcUser) throws IOException {
        BCUserService user = new BCUserService(bcUser);
        Call<BCUserService> login = this.login(user);
        BCUserService rUser = login.execute().body();

        if (rUser == null) return null;

        return new BCUser(rUser);
    }

    public BCResponseStatus doRegister(BCUserService bcUser) throws IOException {
        Call<BCResponseStatus> register = this.register(bcUser);
        return register.execute().body();
    }

    public BCResponseStatus doLogout(BCUserService bcUser) throws IOException {
        Call<BCResponseStatus> logout = this.logout(bcUser);
        return logout.execute().body();
    }

    public BCResponseStatus doForgotPassword(BCUserService bcUser) throws IOException {
        Call<BCResponseStatus> forgotPassword = this.forgotPassword(bcUser);
        return forgotPassword.execute().body();
    }

    public BCResponseStatus doChangePassword(BCUserService bcUser) throws IOException {
        Call<BCResponseStatus> changePassword = this.changePassword(bcUser);
        return changePassword.execute().body();
    }

    public BCMembership doGetMembership(String userName) throws IOException {
        Call<BCBaseResponse<BCMembershipData>> getMembership = this.getMembership(userName);
        BCBaseResponse<BCMembershipData> response = getMembership.execute().body();
        if (isSuccess(response) && response.getPayload() != null) {
            return new BCMembership(response.getPayload());
        }
        return null;
    }


    public void doLoadTripsByUser(String userName, final WebServiceCallBack<List<BCTripInfo>> webServiceCallBack) {
        try {
            Call<BCBaseResponse<List<BCTripResponse>>> loadTrips = this.loadTripsByUser(userName);
            loadTrips.enqueue(new Callback<BCBaseResponse<List<BCTripResponse>>>() {
                @Override
                public void onResponse(@NonNull Call<BCBaseResponse<List<BCTripResponse>>> call, @NonNull Response<BCBaseResponse<List<BCTripResponse>>> response) {
                    if (response.isSuccessful()) {
                        webServiceCallBack.onSuccess(TripUtils.mapAll(response.body().getPayload()));
                    } else {
                        webServiceCallBack.onFailed(response.message());
                        Logger.e(TAG, response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BCBaseResponse<List<BCTripResponse>>> call, @NonNull Throwable t) {
                    webServiceCallBack.onFailed(t.getMessage());
                    Logger.e(TAG, t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            webServiceCallBack.onFailed(e.getMessage());
            Logger.e(TAG, e.getMessage());
        }
    }

    public List<BCTripResponse> doLoadTripsByUser(String userName) throws Exception {
        Call<BCBaseResponse<List<BCTripResponse>>> loadTrips = this.loadTripsByUser(userName);
        BCBaseResponse<List<BCTripResponse>> response = loadTrips.execute().body();
        if (isSuccess(response)) {
            return response.getPayload();
        }

        return Collections.emptyList();
    }

    public void doDownloadTrip(final String tripId, final WebServiceCallBack<File> callback) {
        Call<ResponseBody> downloadTrip = this.downloadTrip(tripId + ".sqlite");
        downloadTrip.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        File file = BC_Helper.writeResponseBodyToStorage(response, tripId);
                        BCTripInfo bcTripInfo = BCTripInfoDBHelper.get(tripId);
                        BCTrip trip = (new BCTripDAO(tripId)).findById(tripId);
                        if (trip != null) {
                            bcTripInfo.setImage(new Blob(trip.getImage()));
                        }
                        BCTripInfoDBHelper.save(bcTripInfo);

                        callback.onSuccess(file);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        callback.onFailed(e.getMessage());
                    }
                } else {
                    callback.onFailed(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailed(t.getMessage());
            }
        });
    }

    public void doDownloadSharedTrip(final String tripId, final WebServiceCallBack<BCTripInfo> callback) {
        try {
            Call<BCBaseResponse<List<BCTripResponse>>> sharedTrip = loadTrip(tripId);
            sharedTrip.enqueue(new Callback<BCBaseResponse<List<BCTripResponse>>>() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onResponse(@NonNull Call<BCBaseResponse<List<BCTripResponse>>> call2, @NonNull final Response<BCBaseResponse<List<BCTripResponse>>> response2) {
                    if (response2.isSuccessful() && response2.body() != null) {
                        final BCTripResponse tripResponse = response2.body().getPayload().get(0);
                        final BCTripInfo tripInfo = TripUtils.map(tripResponse);
                        if (BCUtils.getCurrentUser() != null && tripInfo.getOwnerId().equals(BCUtils.getCurrentUser().getUserId())) {
                            //TODO need enhance
                            callback.onFailed("Cannot display own trip as shared trip");
                        } else if (!tripResponse.isShareTrek()) {
                            callback.onSuccess(tripInfo);
                        } else {
                            //check if exist
                            try {
                                BCTripInfo localTripInfo = BCTripInfoDBHelper.get(tripId);
                                BCTrip bcTrip = (new BCTripDAO(tripId)).findById(tripId);
                                if (bcTrip == null || (localTripInfo.getTimestamp() < tripInfo.getTimestamp())) {
                                    unsafeDownloadTrip(tripInfo, tripId, callback);
                                } else {
                                    callback.onSuccess(tripInfo);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                Logger.e(TAG, e.getMessage());
                                callback.onFailed(e.getMessage());
                            }
                        }
                    } else {
                        callback.onFailed(response2.message());
                    }
                }

                @Override
                public void onFailure(Call<BCBaseResponse<List<BCTripResponse>>> call2, Throwable t2) {
                    callback.onFailed(t2.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, e.getMessage());
            callback.onFailed(e.getMessage());
        }
    }

    private void unsafeDownloadTrip(final BCTripInfo tripInfo, final String tripId, final WebServiceCallBack<BCTripInfo> callback) {
        //start to download shared trip
        Call<ResponseBody> downloadTrip = downloadTrip(tripId + ".sqlite");
        downloadTrip.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    BC_Helper.writeResponseBodyToStorage(response, tripId);
                    callback.onSuccess(tripInfo);
                } else {
                    callback.onFailed(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailed(t.getMessage());
            }
        });
    }

    public void doUploadTrip(final String tripDbFilePath, String userName, BCTripResponse trip, final WebServiceCallBack callback) {

        try {
            MultipartBody.Part imagePart = prepareFilePart(tripDbFilePath);
            MultipartBody.Part userNamePart = MultipartBody.Part.createFormData("userName", userName);
            MultipartBody.Part trekFolder = MultipartBody.Part.createFormData("trekFolder", trip.getTrekFolder());
            MultipartBody.Part type = MultipartBody.Part.createFormData("type", trip.getType());
            MultipartBody.Part basemapType = MultipartBody.Part.createFormData("basemapType", trip.getBasemapType());
            MultipartBody.Part imageUrl = MultipartBody.Part.createFormData("imageUrl", "");
            MultipartBody.Part shareTrek = MultipartBody.Part.createFormData("shareTrek", String.valueOf(trip.isShareTrek()));
            MultipartBody.Part trekName = MultipartBody.Part.createFormData("trekName", trip.getName());
            MultipartBody.Part id = MultipartBody.Part.createFormData("id", trip.getId());

            Call<BCBaseResponse<BCTripUploadResponse>> uploadTrip = this.uploadTrip(imagePart, userNamePart, id, trekName, trekFolder, type, basemapType, imageUrl, shareTrek);
            uploadTrip.enqueue(new Callback<BCBaseResponse<BCTripUploadResponse>>() {
                @Override
                public void onResponse(Call<BCBaseResponse<BCTripUploadResponse>> call, Response<BCBaseResponse<BCTripUploadResponse>> response) {
                    if (response.isSuccessful() && response.body().getResponseCode() == RESPONSE_CODE_SUCCESS) {
                        BCTripUploadResponse tripResponse = response.body().getPayload();
                        if (tripResponse.getTrip().size() > 0) {
                            BCTripResponse bcTripResponse = tripResponse.getTrip().get(0);
                            final BCTripInfo tripInfo = TripUtils.map(bcTripResponse);
                            BCTripInfoDBHelper.save(tripInfo);
                            BCTrip bcTrip = new BCTrip(tripInfo);
                            BCTripDAO dao = new BCTripDAO(bcTrip.getId());
                            dao.insertOrUpdate(bcTrip);
                            callback.onSuccess(null);
                            Logger.e("BCApiService", tripDbFilePath);
                        } else {
                            callback.onFailed("No trip uploaded");
                        }
                    } else {
                        callback.onFailed(response.raw().message());
                    }
                }

                @Override
                public void onFailure(Call<BCBaseResponse<BCTripUploadResponse>> call, Throwable t) {
                    callback.onFailed(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailed(e.getMessage());
        }
    }

    private MultipartBody.Part prepareFilePart(String filePath) {
        try {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        } catch (Exception e) {
            return null;
        }
    }

    public String doUploadProfilePicture(String username, String filePath) {
        try {
            String imageBase64 = BCUtils.encodeBase64Image(filePath);
            Call<BCBaseResponse> uploadPicture = this.uploadPicture(username,
                    new BCPictureData("data:image/jpeg;base64," + imageBase64));
            BCBaseResponse response = uploadPicture.execute().body();
            if (response != null && response.getResponseCode() == 1) {
                Logger.e("BCApiService", "doUploadProfilePicture success");
                return filePath;
            }
        } catch (Exception ex) {
            Logger.e(TAG, ex.getMessage());
        }
        return filePath;
    }

    public Bitmap doGetProfilePicture(String username) throws IOException {
        Call<BCBaseResponse<String>> getProfilePicture = this.getProfilePicture(username);
        BCBaseResponse<String> response = getProfilePicture.execute().body();
        if (isSuccess(response)) {
            byte[] decodedString = Base64.decode(response.getPayload()
                    .replace("data:image/png;base64,", "")
                    .replace("data:image/jpeg;base64,", ""), Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
    }

    public BCUser doGetUserProfile(String userName) throws IOException {
        Call<BCBaseResponse<BCUserService>> getUserProfile = this.getUserProfile(userName);
        BCBaseResponse<BCUserService> response = getUserProfile.execute().body();
        if (isSuccess(response)) {
            return new BCUser(response.getPayload());
        }
        return null;
    }

    public BCUser doUpdateUserProfile(String userName, BCUserService bcUserService) throws
            IOException {
        Call<BCBaseResponse<BCUserService>> updateUserProfile = this.updateUserProfile(userName, bcUserService);
        BCBaseResponse<BCUserService> response = updateUserProfile.execute().body();
        if (isSuccess(response)) {
            return new BCUser(response.getPayload());
        }
        return null;
    }

    public void doUpdateUserProfile(String userName, BCUserService bcUserService, final WebServiceCallBack<BCUser> webServiceCallBack) {
        Call<BCBaseResponse<BCUserService>> updateUserProfile = this.updateUserProfile(userName, bcUserService);
        updateUserProfile.enqueue(new Callback<BCBaseResponse<BCUserService>>() {
            @Override
            public void onResponse(Call<BCBaseResponse<BCUserService>> call, Response<BCBaseResponse<BCUserService>> response) {
                if (response.isSuccessful() && response.body().getResponseCode() == RESPONSE_CODE_SUCCESS) {
                    webServiceCallBack.onSuccess(new BCUser(response.body().getPayload()));
                }
            }

            @Override
            public void onFailure(Call<BCBaseResponse<BCUserService>> call, Throwable t) {
                webServiceCallBack.onFailed(t.getMessage());
            }
        });
    }

    public BCMapResponse doLoadMapsByUser(String userName) throws IOException {
        Call<BCMapResponse> loadMaps = this.loadMapsByUser(userName);
        BCMapResponse response = loadMaps.execute().body();
        if (response != null) {
            return response;
        }
        return null;
    }

    public void doLoadVectorMapCatalogAsync(String shortname, final WebServiceCallBack<BCVectorMapCatalogResponse> callBack) {
        Call<BCBaseResponse<List<BCVectorMapCatalogResponse>>> vectorcatalog = this.getVectorMapCatalogByShortName(shortname);
        vectorcatalog.enqueue(new Callback<BCBaseResponse<List<BCVectorMapCatalogResponse>>>() {
            @Override
            public void onResponse(Call<BCBaseResponse<List<BCVectorMapCatalogResponse>>> call, Response<BCBaseResponse<List<BCVectorMapCatalogResponse>>> response) {
                if (response.isSuccessful())
                    callBack.onSuccess(response.body().getPayload().get(0));
                else {
                    Log.w(TAG, "doLoadVectorMapCatalogAsync unsuccessful: " + response);
                    callBack.onFailed(response.message());
                }
            }

            @Override
            public void onFailure(Call<BCBaseResponse<List<BCVectorMapCatalogResponse>>> call, Throwable t) {
                callBack.onFailed(t.getMessage());
                Log.w(TAG, "VectorMapCatalog Call Fail: " + t.getMessage(), t);
            }


        });

    }


    public void doLoadMapsByUserAsync(String userName, final WebServiceCallBack<BCMapResponse> callBack) {
        Call<BCMapResponse> loadMaps = this.loadMapsByUser(userName);
        loadMaps.enqueue(new Callback<BCMapResponse>() {
            @Override
            public void onResponse(Call<BCMapResponse> call, Response<BCMapResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        callBack.onSuccess(response.body());
                    } else {
                        callBack.onFailed(response.message());
                    }
                } catch (Exception ex) {
                    callBack.onFailed(ex.getMessage());
                    Logger.e(TAG, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<BCMapResponse> call, Throwable ex) {
                callBack.onFailed(ex.getMessage());
            }
        });
    }


    //region BOOKMARK
    public void doAddBookmark(BCBookmark bookmark, final WebServiceCallBack<BCResponseStatus> callBack) {
        //TODO @Trang to check
        BCBookmarkData bookmarkData = new BCBookmarkData(bookmark);
        BCBookmarkSubmit input = new BCBookmarkSubmit();
        input.setBookmarkInfo(bookmarkData);
        Call<BCResponseStatus> api = this.addBookmark(input);
        api.enqueue(new Callback<BCResponseStatus>() {
            @Override
            public void onResponse(Call<BCResponseStatus> call, Response<BCResponseStatus> response) {
                try {
                    if (response.isSuccessful() && "Bookmark added successfully!".equals(response.body().getStatus())) {
                        callBack.onSuccess(response.body());
                    } else {
                        callBack.onFailed(response.body().getStatus());
                    }
                } catch (Exception ex) {
                    callBack.onFailed(ex.getMessage());
                    Logger.e(TAG, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<BCResponseStatus> call, Throwable t) {
                callBack.onFailed(t.getMessage());
            }
        });
    }

    public void doLoadBookmarkByUser(String username, final WebServiceCallBack<BCBookmarkListResponse> callBack) {
        Call<BCBookmarkListResponse> api = this.loadBookMarkByUser(username);
        api.enqueue(new Callback<BCBookmarkListResponse>() {
            @Override
            public void onResponse(Call<BCBookmarkListResponse> call, Response<BCBookmarkListResponse> response) {
                if (response.isSuccessful()) {
                    callBack.onSuccess(response.body());
                } else {
                    callBack.onFailed("unexpected error");
                }
            }

            @Override
            public void onFailure(Call<BCBookmarkListResponse> call, Throwable t) {
                callBack.onFailed(t.getMessage());
            }
        });
    }
    //endregion

    public void doGetUserFavoriteMap(String username, final WebServiceCallBack<ArrayList<String>> callBack) {
        try {
            Call<BCBaseResponse<ArrayList<String>>> api = this.loadUserFavoriteMaps(username);
            api.enqueue(new Callback<BCBaseResponse<ArrayList<String>>>() {
                @Override
                public void onResponse(Call<BCBaseResponse<ArrayList<String>>> call, Response<BCBaseResponse<ArrayList<String>>> response) {
                    if (response.isSuccessful() && response.body().getResponseCode() == RESPONSE_CODE_SUCCESS) {
                        callBack.onSuccess(response.body().getPayload());
                    } else {
                        callBack.onFailed("unexpected error");
                    }
                }

                @Override
                public void onFailure(Call<BCBaseResponse<ArrayList<String>>> call, Throwable t) {
                    callBack.onFailed(t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callBack.onFailed(e.getMessage());
        }
    }

    private boolean isSuccess(BCBaseResponse response) {
        return response != null && response.getResponseCode() == RESPONSE_CODE_SUCCESS && response.getPayload() != null;
    }

    //region OVERRIDE
    @Override
    public Call<BCUserService> login(BCUserService user) {
        return bciApiService.login(user);
    }

    @Override
    public Call<BCResponseStatus> register(BCUserService user) {
        return bciApiService.register(user);
    }

    @Override
    public Call<BCResponseStatus> logout(BCUserService user) {
        return bciApiService.logout(user);
    }

    @Override
    public Call<BCResponseStatus> forgotPassword(BCUserService user) {
        return bciApiService.forgotPassword(user);
    }

    @Override
    public Call<BCResponseStatus> changePassword(BCUserService user) {
        return bciApiService.changePassword(user);
    }

    @Override
    public Call<BCBaseResponse<BCMembershipData>> getMembership(String userName) {
        return bciApiService.getMembership(userName);
    }

    @Override
    public Call<BCBaseResponse<List<BCTripResponse>>> loadTripsByUser(String username) {
        return bciApiService.loadTripsByUser(username);
    }

    @Override
    public Call<BCBaseResponse<List<BCTripResponse>>> loadTrip(String tripId) {
        return bciApiService.loadTrip(tripId);
    }

    @Override
    public Call<ResponseBody> downloadTrip(String tripId) {
        return bciApiService.downloadTrip(tripId);
    }

    @Override
    public Call<BCBaseResponse<BCTripUploadResponse>> uploadTrip(MultipartBody.Part imagePart, MultipartBody.Part userNamePart, MultipartBody.Part id, MultipartBody.Part trekName, MultipartBody.Part trekFolder, MultipartBody.Part type, MultipartBody.Part basemapType, MultipartBody.Part imageUrl, MultipartBody.Part shareTrek) {
        return bciApiService.uploadTrip(imagePart, userNamePart, id, trekName, trekFolder, type, basemapType, imageUrl, shareTrek);
    }

    @Override
    public Call<BCBaseResponse> uploadPicture(String username, BCPictureData imageBase64) {
        return bciApiService.uploadPicture(username, imageBase64);
    }

    @Override
    public Call<BCBaseResponse<String>> getProfilePicture(String username) {
        return bciApiService.getProfilePicture(username);
    }

    @Override
    public Call<BCBaseResponse<BCUserService>> getUserProfile(String username) {
        return bciApiService.getUserProfile(username);
    }

    @Override
    public Call<BCBaseResponse<BCUserService>> updateUserProfile(String username, BCUserService user) {
        return bciApiService.updateUserProfile(username, user);
    }

    @Override
    public Call<BCBaseResponse<List<BCVectorMapCatalogResponse>>> getVectorMapCatalogByShortName(String shortName) {
        return bciApiService.getVectorMapCatalogByShortName(shortName);
    }

    @Override
    public Call<BCMapResponse> loadMapsByUser(String username) {
        return bciApiService.loadMapsByUser(username);
    }

    @Override
    public Call<BCBookmarkListResponse> loadBookMarkByUser(String username) {
        return bciApiService.loadBookMarkByUser(username);
    }

    @Override
    public Call<BCResponseStatus> addBookmark(BCBookmarkSubmit bookmarkData) {
        return bciApiService.addBookmark(bookmarkData);
    }

    @Override
    public Call<BCBaseResponse<ArrayList<String>>> loadUserFavoriteMaps(String username) {
        return bciApiService.loadUserFavoriteMaps(username);
    }
    //endregion
}
