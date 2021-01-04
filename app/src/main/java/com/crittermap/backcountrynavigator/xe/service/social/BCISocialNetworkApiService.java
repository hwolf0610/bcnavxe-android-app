package com.crittermap.backcountrynavigator.xe.service.social;

import com.crittermap.backcountrynavigator.xe.service.BCBaseResponse;
import com.crittermap.backcountrynavigator.xe.service.BCUserService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface BCISocialNetworkApiService {
    @POST("api/mobile/users/mobileSocialUser")
    Call<BCBaseResponse<BCUserService>> postSocialNetworkUser(@Body BCSocialNetworkUser user);
}
