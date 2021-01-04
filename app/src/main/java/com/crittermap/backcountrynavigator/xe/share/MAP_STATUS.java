package com.crittermap.backcountrynavigator.xe.share;

/**
 * Created by henry on 3/25/2018.
 */

public enum MAP_STATUS {
    NOT_DOWNLOAD("Not_download"),
    DOWNLOADED("Downloaded");

    private String value;

    MAP_STATUS(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
