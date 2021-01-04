package com.crittermap.backcountrynavigator.xe.eventbus;

import android.widget.ImageView;

public class BCOnLoadWaypointIconEvent {
    ImageView imageView;

    public BCOnLoadWaypointIconEvent(ImageView imageView) {
        this.imageView = imageView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
