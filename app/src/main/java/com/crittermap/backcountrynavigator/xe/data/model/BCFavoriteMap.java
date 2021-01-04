package com.crittermap.backcountrynavigator.xe.data.model;

/**
 * Created by henry on 3/10/2018.
 */

public class BCFavoriteMap {
    private String mapId;
    private int userId;
    private String favoriteBaseMap;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFavoriteBaseMap() {
        return favoriteBaseMap;
    }

    public void setFavoriteBaseMap(String favoriteBaseMap) {
        this.favoriteBaseMap = favoriteBaseMap;
    }
}
