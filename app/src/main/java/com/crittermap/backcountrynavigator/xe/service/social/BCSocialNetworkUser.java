package com.crittermap.backcountrynavigator.xe.service.social;

import com.google.gson.annotations.SerializedName;

public class BCSocialNetworkUser {
    @SerializedName("username")
    private String userName;
    private BCSocialNetworkProfile profile;

    public BCSocialNetworkUser(String userName, BCSocialNetworkProfile profile) {
        this.userName = userName;
        this.profile = profile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BCSocialNetworkProfile getProfile() {
        return profile;
    }

    public void setProfile(BCSocialNetworkProfile profile) {
        this.profile = profile;
    }
}
