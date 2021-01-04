package com.crittermap.backcountrynavigator.xe.data.model;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.service.BCUserService;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;

/**
 * Created by henry on 3/10/2018.
 */

@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCUser extends BaseModel {
    @Column
    @PrimaryKey
    private String userId;
    @Column
    private String customerId;
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String address;
    @Column
    private String userRole;
    @Column
    private String token;
    @Column
    private String passID;
    @Column
    private String googleId;
    @Column
    private String googleToken;
    @Column
    private String googleName;
    @Column
    private String googleEmail;
    @Column
    private String facebookId;
    @Column
    private String facebookToken;
    @Column
    private String facebookName;
    @Column
    private String image;
    private BCMembership memberShip;
    private ArrayList<String> favoriteBasemap;

    public BCUser(BCUserService bcUserService) {
        userName = bcUserService.getUsername();
        if (userName == null) {
            userName = bcUserService.getUserName();
        }
        password = bcUserService.getPassword();
        firstName = bcUserService.getFirstname();
        lastName = bcUserService.getLastName();
        favoriteBasemap = bcUserService.getFavoriteBasemap();
        customerId = bcUserService.getCustomerId();
        image = bcUserService.getImage();
        token = bcUserService.getToken();
    }

    public BCUser() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPassID() {
        return passID;
    }

    public void setPassID(String passID) {
        this.passID = passID;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getGoogleName() {
        return googleName;
    }

    public void setGoogleName(String googleName) {
        this.googleName = googleName;
    }

    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getFavoriteBasemap() {
        return favoriteBasemap;
    }

    public void setFavoriteBasemap(ArrayList<String> favoriteBasemap) {
        this.favoriteBasemap = favoriteBasemap;
    }

    public BCMembership getMemberShip() {
        return memberShip;
    }

    public void setMemberShip(BCMembership memberShip) {
        this.memberShip = memberShip;
    }

    // Username in here is email :)
    public static BCUser findUser(String username) {
        return new Select().from(BCUser.class).where(BCUser_Table.userName.eq(username)).querySingle();
    }

    public static BCUser updateUser(BCUser user) {
        BCUser updateUser = findUser(user.getUserName());
        if (updateUser != null) {
            updateUser.setFirstName(user.getFirstName());
            updateUser.setFavoriteBasemap(user.getFavoriteBasemap());
            updateUser.setCustomerId(user.getCustomerId());
            updateUser.setImage(user.getImage());
            updateUser.save();
        } else { // Case not exist => create new?
            //user.setUserId(UUID.randomUUID().toString());
            //user.save();
        }
        return updateUser;
    }
}
