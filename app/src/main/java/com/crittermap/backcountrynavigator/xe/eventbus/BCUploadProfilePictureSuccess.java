package com.crittermap.backcountrynavigator.xe.eventbus;

/**
 * Created by thutrang.dao on 4/10/18.
 */

public class BCUploadProfilePictureSuccess {
    private String filePath;

    public BCUploadProfilePictureSuccess(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
