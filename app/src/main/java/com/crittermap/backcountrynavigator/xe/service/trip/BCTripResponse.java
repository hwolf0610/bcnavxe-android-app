package com.crittermap.backcountrynavigator.xe.service.trip;

import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nhatdear on 3/17/18.
 */

public class BCTripResponse {

    @SerializedName("_id")
    private String id;
    private String trekName;
    private String userName;
    private String status;
    @SerializedName("__v")
    private long v;
    private String trekFolder;
    private String type;
    private String basemapType;
    private boolean shareTrek;
    private String access;
    private String createdAt;
    private String updatedAt;
    private String imageUrl;
    private List<BCTripFeature> features;

    public BCTripResponse(BCTripInfo tripInfo) {
        id = tripInfo.getId();
        userName = tripInfo.getOwnerId();
        trekFolder = tripInfo.getTrekFolder();
        type = "FeatureCollections";
//        basemapType = tripInfo.getBaseMapType();
        basemapType = "wwbcnvector"; //TODO insert current map short name when save. Create new col
        imageUrl = ""; //TODO link? base64? blob?
        shareTrek = false; //TODO share?
        trekName = tripInfo.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrekName() {
        return trekName;
    }

    public void setTrekName(String trekName) {
        this.trekName = trekName;
    }

    public long getV() {
        return v;
    }

    public void setV(long v) {
        this.v = v;
    }

    public String getTrekFolder() {
        return trekFolder;
    }

    public void setTrekFolder(String trekFolder) {
        this.trekFolder = trekFolder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBasemapType() {
        return basemapType;
    }

    public void setBasemapType(String basemapType) {
        this.basemapType = basemapType;
    }

    public boolean isShareTrek() {
        return shareTrek;
    }

    public void setShareTrek(boolean shareTrek) {
        this.shareTrek = shareTrek;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return getTrekName();
    }

    public List<BCTripFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<BCTripFeature> features) {
        this.features = features;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
