package com.crittermap.backcountrynavigator.xe.controller.utils;

import com.crittermap.backcountrynavigator.xe.controller.constant.LayerType;


public class BC_Layer {
    private LayerType layerType;
    private String name;
    private Object layer;
    private boolean isShow;

    public BC_Layer(LayerType layerType, Object layer, String name, boolean isShow) {
        this.layerType = layerType;
        this.layer = layer;
        this.isShow = isShow;
        this.name = name;
    }

    public Object getLayer() {
        return layer;
    }

    public void setLayer(Object layer) {
        this.layer = layer;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean show) {
        isShow = show;
    }

    public LayerType getLayerType() {
        return layerType;
    }

    public void setLayerType(LayerType layerType) {
        this.layerType = layerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
