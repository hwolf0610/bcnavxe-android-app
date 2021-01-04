package com.crittermap.backcountrynavigator.xe.service.bookmark;

import com.crittermap.backcountrynavigator.xe.data.model.bookmark.BCBookmark;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BCBookmarkData implements Serializable {
    @SerializedName("_id")
    private String id;
    private String bookmarkName;
    private String userName;
    private String basemapType;
    private String createAt;
    private String lat;
    @SerializedName("long")
    private String lon;
    private String updatedAt;
    private String zoom;
    @SerializedName("__v")
    private Long v;

    public BCBookmarkData(BCBookmark payload) {
        id = payload.getId();
        bookmarkName = payload.getName();
        userName = payload.getUserName();
        basemapType = payload.getBasemapType();
        createAt = payload.getCreateAt();
        lat = payload.getLat();
        lon = payload.getLon();
        updatedAt = payload.getUpdatedAt();
        zoom = payload.getZoom();
        v = payload.getV();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookmarkName() {
        return bookmarkName;
    }

    public void setBookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBasemapType() {
        return basemapType;
    }

    public void setBasemapType(String basemapType) {
        this.basemapType = basemapType;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    public Long getV() {
        return v;
    }

    public void setV(Long v) {
        this.v = v;
    }
}
