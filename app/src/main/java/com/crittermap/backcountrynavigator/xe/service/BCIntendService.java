package com.crittermap.backcountrynavigator.xe.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.eventbus.BCChangePasswordSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCForgotPasswordSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCGetProfilePictureSuccess;
import com.crittermap.backcountrynavigator.xe.eventbus.BCGetUserProfileSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCLoginFailEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCLoginSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCLogoutSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCRegisterSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCSystemErrorEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCUpdateUserProfilePictureFailedEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCUpdateUserProfileSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCUploadProfilePictureSuccess;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

public class BCIntendService extends IntentService {
    private static final String ACTION_LOGIN =
            "com.crittermap.backcountrynavigator.xe.service.action.LOGIN";
    private static final String ACTION_REGISTER =
            "com.crittermap.backcountrynavigator.xe.service.action.REGISTER";
    private static final String ACTION_LOGOUT =
            "com.crittermap.backcountrynavigator.xe.service.action.LOGOUT";
    private static final String ACTION_FORGOT_PASSWORD =
            "com.crittermap.backcountrynavigator.xe.service.action.FORGOT_PASSWORD";
    private static final String ACTION_CHANGE_PASSWORD =
            "com.crittermap.backcountrynavigator.xe.service.action.CHANGE_PASSWORD";
    private static final String ACTION_UPLOAD_PROFILE_PICTURE =
            "com.crittermap.backcountrynavigator.xe.service.action.UPLOAD_PROFILE_PICTURE";
    private static final String ACTION_GET_PROFILE_PICTURE =
            "com.crittermap.backcountrynavigator.xe.service.action.GET_PROFILE_PICTURE";
    private static final String ACTION_GET_USER_PROFILE =
            "com.crittermap.backcountrynavigator.xe.service.action.USER_PROFILE";
    private static final String ACTION_UPDATE_USER_PROFILE =
            "com.crittermap.backcountrynavigator.xe.service.action.UPDATE_USER_PROFILE";
    private static final String ACTION_UPDATE_USER_FAVOURITE_MAP =
            "com.crittermap.backcountrynavigator.xe.service.action.UPDATE_USER_FAVOURITE_MAP";
    private static final String EXTRA_USERNAME =
            "com.crittermap.backcountrynavigator.xe.service.extra.USERNAME";
    private static final String EXTRA_FIRST_NAME =
            "com.crittermap.backcountrynavigator.xe.service.extra.FIRST_NAME";
    private static final String EXTRA_LAST_NAME =
            "com.crittermap.backcountrynavigator.xe.service.extra.LAST_NAME";
    private static final String EXTRA_PASSWORD =
            "com.crittermap.backcountrynavigator.xe.service.extra.PASSWORD";
    private static final String EXTRA_USER_REGISTER =
            "com.crittermap.backcountrynavigator.xe.service.extra.USER_REGISTER";
    private static final String EXTRA_IMAGE =
            "com.crittermap.backcountrynavigator.xe.service.extra.IMAGE";
    private static final String EXTRA_FAVOURITE_MAP = "com.crittermap.backcountrynavigator.xe.service.extra.EXTRA_FAVOURITE_MAP";

    public BCIntendService() {
        super("BCIntendService");
    }

    public static void startActionLogin(Context context, String username, String password) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        context.startService(intent);
    }

    public static void startActionRegister(Context context, String userServiceString) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_REGISTER);
        intent.putExtra(EXTRA_USER_REGISTER, userServiceString);
        context.startService(intent);
    }

    public static void startActionLogout(Context context) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_LOGOUT);
        String username = BCUtils.getCurrentUser().getUserName();
        Gson gson = new Gson();
        BCUserService bcUserService = new BCUserService();
        bcUserService.setUsername(username);
        intent.putExtra(EXTRA_USERNAME, gson.toJson(bcUserService));
        context.startService(intent);
    }

    public static void startActionForgotPassword(Context context, String username) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_FORGOT_PASSWORD);
        intent.putExtra(EXTRA_USERNAME, username);
        context.startService(intent);
    }

    public static void startActionChangePassword(Context context, String userServiceString) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_CHANGE_PASSWORD);
        intent.putExtra(EXTRA_USER_REGISTER, userServiceString);
        context.startService(intent);
    }

    public static void startActionUpdateProfilePicture(Context context, String userName, String imageUri) {
        if (TextUtils.isEmpty(userName)) return;
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_UPLOAD_PROFILE_PICTURE);
        intent.putExtra(EXTRA_USERNAME, userName);
        intent.putExtra(EXTRA_IMAGE, imageUri);
        context.startService(intent);
    }

    public static void startActionGetUserProfile(Context context, String userName) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_GET_USER_PROFILE);
        intent.putExtra(EXTRA_USERNAME, userName);
        context.startService(intent);
    }

    public static void startActionUpdateUserProfile(Context context, String userName, String firstName, String lastName) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_UPDATE_USER_PROFILE);
        intent.putExtra(EXTRA_USERNAME, userName);
        intent.putExtra(EXTRA_FIRST_NAME, firstName);
        intent.putExtra(EXTRA_LAST_NAME, lastName);
        context.startService(intent);
    }

    public static void startActionUpdateUserProfile(Context context, ArrayList<String> favouriteMaps) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_UPDATE_USER_FAVOURITE_MAP);
        intent.putStringArrayListExtra(EXTRA_FAVOURITE_MAP, favouriteMaps);
        context.startService(intent);
    }

    public static void startActionGetProfilePicture(Context context, String userName) {
        Intent intent = new Intent(context, BCIntendService.class);
        intent.setAction(ACTION_GET_PROFILE_PICTURE);
        intent.putExtra(EXTRA_USERNAME, userName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            String username = intent.getStringExtra(EXTRA_USERNAME);
            String firstName = intent.getStringExtra(EXTRA_FIRST_NAME);
            String lastName = intent.getStringExtra(EXTRA_LAST_NAME);
            String password = intent.getStringExtra(EXTRA_PASSWORD);
            String userRegisterStr = intent.getStringExtra(EXTRA_USER_REGISTER);
            ArrayList<String> favouriteMaps = intent.getStringArrayListExtra(EXTRA_FAVOURITE_MAP);
            assert action != null;
            switch (action) {
                case ACTION_LOGIN:
                    handleActionLogin(username, password);
                    break;
                case ACTION_REGISTER:
                    handleActionRegister(userRegisterStr);
                    break;
                case ACTION_LOGOUT:
                    handleActionLogout(username);
                    break;
                case ACTION_FORGOT_PASSWORD:
                    handleActionForgotPassword(username);
                    break;
                case ACTION_CHANGE_PASSWORD:
                    handleActionChangePassword(userRegisterStr);
                    break;
                case ACTION_UPLOAD_PROFILE_PICTURE:
                    handleActionUpdateProfilePicture(username
                            , intent.getStringExtra(EXTRA_IMAGE));
                    break;
                case ACTION_GET_PROFILE_PICTURE:
                    handleActionGetProfilePicture(username);
                    break;
                case ACTION_GET_USER_PROFILE:
                    handleActionGetUserProfile(username);
                    break;
                case ACTION_UPDATE_USER_PROFILE:
                    handleActionUpdateUserProfile(username, firstName, lastName);
                    break;
                case ACTION_UPDATE_USER_FAVOURITE_MAP:
                    handleActionUpdateUserFavouriteMap(favouriteMaps);
                    break;
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLogin(String username, String password) {
        BCApiService apiService = BCApiService.getInstance();

        BCUser user = new BCUser();
        user.setUserName(username);
        user.setPassword(password);

        try {
            BCUser rUser = apiService.doLogin(user);
            EventBus.getDefault().post(new BCLoginSuccessEvent(rUser));
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new BCLoginFailEvent());
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRegister(String userStr) {
        Gson gson = new Gson();
        BCUserService bcUser = gson.fromJson(userStr, BCUserService.class);
        BCApiService apiService = BCApiService.getInstance();
        try {
            BCResponseStatus responseStatus = apiService.doRegister(bcUser);
            BCUser user = new BCUser();
            user.setFirstName(bcUser.getFirstname());
            user.setUserName(bcUser.getEmail());
            EventBus.getDefault().post(new BCRegisterSuccessEvent(responseStatus, user));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleActionLogout(String userStr) {
        Gson gson = new Gson();
        BCUserService bcUser = gson.fromJson(userStr, BCUserService.class);
        BCApiService apiService = BCApiService.getInstance();
        try {
            BCResponseStatus responseStatus = apiService.doLogout(bcUser);
            EventBus.getDefault().post(new BCLogoutSuccessEvent(responseStatus));
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new BCLogoutSuccessEvent(null));
        }
    }

    private void handleActionForgotPassword(String userStr) {
        Gson gson = new Gson();
        BCUserService bcUser = gson.fromJson(userStr, BCUserService.class);
        BCApiService apiService = BCApiService.getInstance();
        try {
            BCResponseStatus responseStatus = apiService.doForgotPassword(bcUser);
            EventBus.getDefault().post(new BCForgotPasswordSuccessEvent(responseStatus));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleActionChangePassword(String userStr) {
        Gson gson = new Gson();
        BCUserService bcUser = gson.fromJson(userStr, BCUserService.class);
        BCApiService apiService = BCApiService.getInstance();
        try {
            BCResponseStatus responseStatus = apiService.doChangePassword(bcUser);
            EventBus.getDefault().post(new BCChangePasswordSuccessEvent(responseStatus));
        } catch (IOException e) {
            EventBus.getDefault().post(new BCSystemErrorEvent());
            e.printStackTrace();
        }
    }

    private void handleActionUpdateProfilePicture(String username, String filePath) {
        BCApiService apiService = BCApiService.getInstance();
        try {
            String returnFilePath = apiService.doUploadProfilePicture(username, filePath);
            EventBus.getDefault().post(new BCUploadProfilePictureSuccess(returnFilePath));
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new BCUpdateUserProfilePictureFailedEvent("Upload picture failed"));
        }
    }

    private void handleActionGetProfilePicture(String username) {
        BCApiService apiService = BCApiService.getInstance();
        try {
            Bitmap bitmap = apiService.doGetProfilePicture(username);
            EventBus.getDefault().post(new BCGetProfilePictureSuccess(bitmap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleActionUpdateUserProfile(String username, String firstName, String lastName) {
        BCUser bcUser = BCUtils.getCurrentUser();
        BCApiService apiService = BCApiService.getInstance();
        BCUserService userService = new BCUserService();
        userService.setFirstname(firstName);
        userService.setLastName(lastName);
        userService.setFavoriteBasemap(bcUser.getFavoriteBasemap());
        try {
            BCUser responseStatus = apiService.doUpdateUserProfile(username, userService);
            EventBus.getDefault().post(new BCUpdateUserProfileSuccessEvent(responseStatus));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleActionUpdateUserFavouriteMap(ArrayList<String> favouriteMaps) {
        BCUser user = BCUtils.getCurrentUser();
        BCApiService apiService = BCApiService.getInstance();
        BCUserService userService = new BCUserService();
        userService.setFirstname(user.getFirstName());
        userService.setLastName(user.getLastName());
        userService.setFavoriteBasemap(favouriteMaps);
        try {
            BCUser responseStatus = apiService.doUpdateUserProfile(user.getUserName(), userService);
            EventBus.getDefault().post(new BCUpdateUserProfileSuccessEvent(responseStatus));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleActionGetUserProfile(String userStr) {
        BCUser user = BCUtils.getCurrentUser();
        BCApiService apiService = BCApiService.getInstance();
        try {
            BCUser responseStatus = apiService.doGetUserProfile(userStr);
            if (responseStatus != null) {
                user.setFirstName(responseStatus.getFirstName());
                user.setLastName(responseStatus.getLastName());
                EventBus.getDefault().post(new BCGetUserProfileSuccessEvent(user));
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new BCSystemErrorEvent());
        }
    }
}
