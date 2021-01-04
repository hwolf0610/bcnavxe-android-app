package com.crittermap.backcountrynavigator.xe.service;

import com.crittermap.backcountrynavigator.xe.data.model.BCUser;

import java.util.ArrayList;

/**
 * Created by henry on 3/10/2018.
 */

public class BCUserService {
    private String user;
    private String username;
    private String userName;
    private String password;
    private String status;
    private String userRole;
    private String token;
    private ArrayList<String> favoriteBasemap;
    private String customerId;
    private String image;
    private String email;
    private String firstname;
    private String lastname;
    private ChangePassword changePassword;

    public BCUserService() {
    }

    public BCUserService(BCUser bcUser) {
        this.setUsername(bcUser.getUserName());
        this.setPassword(bcUser.getPassword());
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<String> getFavoriteBasemap() {
        return favoriteBasemap;
    }

    public void setFavoriteBasemap(ArrayList<String> favoriteBasemap) {
        this.favoriteBasemap = favoriteBasemap;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public static class ChangePassword {
        private String NewPassword;
        private String OldPassword;

        public ChangePassword(String newPassword, String oldPassword) {
            this.setNewPassword(newPassword);
            this.setOldPassword(oldPassword);
        }

        public String getNewPassword() {
            return NewPassword;
        }

        public void setNewPassword(String newPassword) {
            NewPassword = newPassword;
        }

        public String getOldPassword() {
            return OldPassword;
        }

        public void setOldPassword(String oldPassword) {
            OldPassword = oldPassword;
        }
    }

    public ChangePassword getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(ChangePassword changePassword) {
        this.changePassword = changePassword;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
