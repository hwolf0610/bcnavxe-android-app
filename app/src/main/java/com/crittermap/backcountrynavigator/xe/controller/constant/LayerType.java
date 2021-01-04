package com.crittermap.backcountrynavigator.xe.controller.constant;

/**
 * Created by nhat@saveondev.com on 12/28/2017.
 */

public enum LayerType {
    SKETCH("SKETCH LAYER"),
    FEATURE_LAYER("FEATURE_LAYER"),
    SUB_LAYER("SUB MAP"),
    BASE_LAYER("BASE MAP");

    private String value;

    LayerType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
