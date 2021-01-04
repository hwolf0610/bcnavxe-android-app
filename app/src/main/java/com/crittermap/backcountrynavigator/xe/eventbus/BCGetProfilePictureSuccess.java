package com.crittermap.backcountrynavigator.xe.eventbus;

import android.graphics.Bitmap;

/**
 * Created by thutrang.dao on 4/9/18.
 */

public class BCGetProfilePictureSuccess {
    private Bitmap bitmap;

    public BCGetProfilePictureSuccess(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
