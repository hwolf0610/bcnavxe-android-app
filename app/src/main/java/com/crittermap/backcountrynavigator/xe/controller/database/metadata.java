package com.crittermap.backcountrynavigator.xe.controller.database;

import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;

/**
 * Created by nhat@saveondev.com on 1/10/2018.
 */

public class metadata {
    private String bounds = "";
    private String center = "";
    private String minzoom = "";
    private String maxzoom = "";
    private String name = "";
    private String description = "";
    private String attribution = "";
    private String legend = "";
    private String template = "";
    private String version = "";

    public metadata() {
    }

    public metadata(BCMap map) {
        minzoom = String.valueOf(map.getMinZoom());
        maxzoom = String.valueOf(map.getMaxZoom());
        center = map.getViewPointJson();
        name = map.getMapName();
        description = map.getDesc();
        template = map.getBaseUrl();
        version = "0";
    }
    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getMinzoom() {
        return minzoom;
    }

    public void setMinzoom(String minzoom) {
        this.minzoom = minzoom;
    }

    public String getMaxzoom() {
        return maxzoom;
    }

    public void setMaxzoom(String maxzoom) {
        this.maxzoom = maxzoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
