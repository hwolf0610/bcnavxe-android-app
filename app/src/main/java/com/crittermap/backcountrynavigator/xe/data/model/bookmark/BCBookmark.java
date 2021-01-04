package com.crittermap.backcountrynavigator.xe.data.model.bookmark;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkData;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCBookmark extends BaseModel {
    @Column
    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String userName;
    @Column
    private String basemapType;
    @Column
    private String createAt;
    @Column
    private String lat;
    @Column
    private String lon;
    @Column
    private String updatedAt;
    @Column
    private String zoom;
    @Column
    private Long v;

    public BCBookmark(BCBookmarkData payload) {
        id = payload.getId();
        name = payload.getBookmarkName();
        userName = payload.getUserName();
        basemapType = payload.getBasemapType();
        createAt = payload.getCreateAt();
        lat = payload.getLat();
        lon = payload.getLon();
        updatedAt = payload.getUpdatedAt();
        zoom = payload.getZoom();
        v = payload.getV();
    }

    public BCBookmark() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
