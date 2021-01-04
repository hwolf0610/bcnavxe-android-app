package com.crittermap.backcountrynavigator.xe.data.model.trip_new;

import com.esri.arcgisruntime.mapping.view.Graphic;

import java.io.Serializable;

public class BCGeometry implements Serializable {

    private String id;
    private String tripId;
    private String tripSegmentId;
    private double longitude;
    private double latitude;
    private String gTileId;
    private int minLevel;
    private int maxLevel;
    private String type;
    private String name;
    private String desc;
    private String longDesc;
    private String unicodeDesc;
    private String geoJSON;
    private String mediaLink;
    private long timestamp;
    private int color;
    private String imageUrl;
    private int width;
    private String lineStyle;
    private String fillStyle;
    private Graphic graphic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripSegmentId() {
        return tripSegmentId;
    }

    public void setTripSegmentId(String tripSegmentId) {
        this.tripSegmentId = tripSegmentId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getgTileId() {
        return gTileId;
    }

    public void setgTileId(String gTileId) {
        this.gTileId = gTileId;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getUnicodeDesc() {
        return unicodeDesc;
    }

    public void setUnicodeDesc(String unicodeDesc) {
        this.unicodeDesc = unicodeDesc;
    }

    public String getGeoJSON() {
        return geoJSON;
    }

    public void setGeoJSON(String geoJSON) {
        this.geoJSON = geoJSON;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(String lineStyle) {
        this.lineStyle = lineStyle;
    }

    public String getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(String fillStyle) {
        this.fillStyle = fillStyle;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }
}
