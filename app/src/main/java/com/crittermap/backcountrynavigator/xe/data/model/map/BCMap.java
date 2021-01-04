package com.crittermap.backcountrynavigator.xe.data.model.map;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.share.MAP_STATUS;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by henryhai on 3/24/18.
 */

@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCMap extends BaseModel {
    @Column
    @PrimaryKey
    private String id;
    @Column
    private String mapName;
    @Column
    private String mapTag;
    @Column
    private String desc;
    @Column
    private MAP_STATUS mapStatus;
    @Column
    private boolean isFavoriteMap;
    @Column
    private boolean isPinned;
    @Column
    private int minZoom;
    @Column
    private int maxZoom;
    @Column
    private String baseUrl;
    @Column
    private String copyRight;
    @Column
    private String classType;
    @Column
    private int zoom;
    @Column
    private String copyRightUrl;
    @Column
    private String image;
    @Column
    private String lat;
    @Column
    private String lon;
    @Column
    private String tileResolverType;
    @Column
    private String viewPointJson;
    @Column
    private String location;
    @Column
    private String shortName;
    @Column
    private String visibility;
    @Column
    private String mapType;
    @Column
    private String mapLayers;
    @Column
    private String membershipType;

    @Column
    private long lastUsedTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapTag() {
        return mapTag;
    }

    public void setMapTag(String mapTag) {
        this.mapTag = mapTag;
    }

    public MAP_STATUS getMapStatus() {
        return mapStatus;
    }

    public void setMapStatus(MAP_STATUS mapStatus) {
        this.mapStatus = mapStatus;
    }

    public boolean isFavoriteMap() {
        return isFavoriteMap;
    }

    public void setFavoriteMap(boolean favoriteMap) {
        isFavoriteMap = favoriteMap;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public int getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(int minZoom) {
        this.minZoom = minZoom;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCopyRight() {
        return copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public String getCopyRightUrl() {
        return copyRightUrl;
    }

    public void setCopyRightUrl(String copyRightUrl) {
        this.copyRightUrl = copyRightUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getTileResolverType() {
        return tileResolverType;
    }

    public void setTileResolverType(String tileResolverType) {
        this.tileResolverType = tileResolverType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getViewPointJson() {
        return viewPointJson;
    }

    public void setViewPointJson(String viewPointJson) {
        this.viewPointJson = viewPointJson;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImageUrl() {
        return String.format("https://crittermap-bc865.firebaseapp.com/images/%s", this.image);
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getMapLayers() {
        return mapLayers;
    }

    public void setMapLayers(String mapLayers) {
        this.mapLayers = mapLayers;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }
}
