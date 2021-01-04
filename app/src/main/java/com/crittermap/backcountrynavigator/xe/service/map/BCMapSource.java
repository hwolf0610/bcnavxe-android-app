package com.crittermap.backcountrynavigator.xe.service.map;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henry on 4/21/2018.
 */

public class BCMapSource {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String mapName;
    private String shortname;
    private String baseurl;
    private String classtype;
    private String copyright;
    private String copyrighturl;
    @SerializedName("__v")
    private long v;
    private String desc;
    private String image;
    private String lat;
    private String lon;
    private String maxzoom;
    private String minzoom;
    private String tileresolvertype;
    private String zoom;
    private List<BCVectorLayer> vectorlayer;
    private List<BCMapLayer> maplayer;
    private String maptype;
    private String visibility;
    private String membershipType;
    private BCLocation location;
    private ArrayList<String> mapsubject;
    private String mapSubjectValue;
    private ArrayList<String> mapSources;

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

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getClasstype() {
        return classtype;
    }

    public void setClasstype(String classtype) {
        this.classtype = classtype;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyrighturl() {
        return copyrighturl;
    }

    public void setCopyrighturl(String copyrighturl) {
        this.copyrighturl = copyrighturl;
    }

    public long getV() {
        return v;
    }

    public void setV(long v) {
        this.v = v;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getMaxzoom() {
        return maxzoom;
    }

    public void setMaxzoom(String maxzoom) {
        this.maxzoom = maxzoom;
    }

    public String getMinzoom() {
        return minzoom;
    }

    public void setMinzoom(String minzoom) {
        this.minzoom = minzoom;
    }

    public String getTileresolvertype() {
        return tileresolvertype;
    }

    public void setTileresolvertype(String tileresolvertype) {
        this.tileresolvertype = tileresolvertype;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    public List<BCVectorLayer> getVectorlayer() {
        return vectorlayer;
    }

    public void setVectorlayer(List<BCVectorLayer> vectorlayer) {
        this.vectorlayer = vectorlayer;
    }

    public List<BCMapLayer> getMaplayer() {
        return maplayer;
    }

    public void setMaplayer(List<BCMapLayer> maplayer) {
        this.maplayer = maplayer;
    }

    public String getMaptype() {
        return maptype;
    }

    public void setMaptype(String maptype) {
        this.maptype = maptype;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public BCLocation getLocation() {
        return location;
    }

    public void setLocation(BCLocation location) {
        this.location = location;
    }

    public ArrayList<String> getMapsubject() {
        return mapsubject;
    }

    public void setMapsubject(ArrayList<String> mapsubject) {
        this.mapsubject = mapsubject;
    }

    public String getMapSubjectValue() {
        return mapSubjectValue;
    }

    public void setMapSubjectValue(String mapSubjectValue) {
        this.mapSubjectValue = mapSubjectValue;
    }

    public ArrayList<String> getMapSources() {
        return mapSources;
    }

    public void setMapSources(ArrayList<String> mapSources) {
        this.mapSources = mapSources;
    }
}
