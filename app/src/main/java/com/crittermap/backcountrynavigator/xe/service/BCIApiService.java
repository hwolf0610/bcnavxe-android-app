package com.crittermap.backcountrynavigator.xe.service;

import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkListResponse;
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkSubmit;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapResponse;
import com.crittermap.backcountrynavigator.xe.service.map.BCVectorMapCatalogResponse;
import com.crittermap.backcountrynavigator.xe.service.membership.BCMembershipData;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripResponse;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripUploadResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface BCIApiService {
    @POST("api/login")
    Call<BCUserService> login(@Body BCUserService user);

    @POST("api/registerUser")
    Call<BCResponseStatus> register(@Body BCUserService user);

    @POST("api/logout")
    Call<BCResponseStatus> logout(@Body BCUserService user);

    @POST("api/forgotPassword")
    Call<BCResponseStatus> forgotPassword(@Body BCUserService user);

    @POST("api/changePassword")
    Call<BCResponseStatus> changePassword(@Body BCUserService user);

    @GET("api/mobile/membership")
    Call<BCBaseResponse<BCMembershipData>> getMembership(@Query("userName") String userId);

    @GET("api/mobile/trips")
    Call<BCBaseResponse<List<BCTripResponse>>> loadTripsByUser(@Query("userName") String username);

    @GET("api/mobile/trips/{tripId}")
    Call<BCBaseResponse<List<BCTripResponse>>> loadTrip(@Path("tripId") String tripId);

    @GET("api/mobile/file")
    Call<ResponseBody> downloadTrip(@Query("fileName") String tripId);

    @Multipart
    @POST("api/mobile/file")
    Call<BCBaseResponse<BCTripUploadResponse>> uploadTrip(@Part MultipartBody.Part file, @Part MultipartBody.Part userName, @Part MultipartBody.Part id, @Part MultipartBody.Part trekName, @Part MultipartBody.Part trekFolder, @Part MultipartBody.Part type, @Part MultipartBody.Part basenameType, @Part MultipartBody.Part imageUrl, @Part MultipartBody.Part shareTrek);

    @PUT("api/mobile/users/picture")
    Call<BCBaseResponse> uploadPicture(@Query("userName") String username, @Body BCPictureData image);

    @GET("api/mobile/users/picture")
    Call<BCBaseResponse<String>> getProfilePicture(@Query("userName") String username);

    @GET("api/mobile/users/profile/{userName}")
    Call<BCBaseResponse<BCUserService>> getUserProfile(@Path("userName") String username);

    @PUT("api/mobile/users/profile/{userName}")
    Call<BCBaseResponse<BCUserService>> updateUserProfile(@Path("userName") String username, @Body BCUserService user);

    @GET("/api/mobile/import/getVectorMapCatalogs/{shortName}")
    Call<BCBaseResponse<List<BCVectorMapCatalogResponse>>> getVectorMapCatalogByShortName(@Path("shortName") String shortName);

    @GET("api/getMapSources/{userName}")
    Call<BCMapResponse> loadMapsByUser(@Path("userName") String username);

    //region BOOKMARK
    @GET("api/getBookMarkedMap/{userName}")
    Call<BCBookmarkListResponse> loadBookMarkByUser(@Path("userName") String username);

    @POST("api/saveBookMarkInfo")
    Call<BCResponseStatus> addBookmark(@Body BCBookmarkSubmit data);
    //end

    @GET("api/mobile/maps")
    Call<BCBaseResponse<ArrayList<String>>> loadUserFavoriteMaps(@Query("userName") String username);
}
