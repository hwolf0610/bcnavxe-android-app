package com.crittermap.backcountrynavigator.xe.service.social;

import com.google.gson.annotations.SerializedName;

public class BCSocialNetworkProfile {
    private String id;
    @SerializedName("firstname")
    private String firstName;
    @SerializedName("lastname")
    private String lastName;
    private String provider;
    private String token;

    public BCSocialNetworkProfile(String id, String firstName, String lastName, String provider, String token) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.provider = provider;
        this.token = token;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
