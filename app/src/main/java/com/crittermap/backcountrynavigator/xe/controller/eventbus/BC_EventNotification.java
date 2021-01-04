package com.crittermap.backcountrynavigator.xe.controller.eventbus;

/**
 * Created by nhat@saveondev.com on 1/14/18.
 */

public class BC_EventNotification {
    private String action;

    public BC_EventNotification(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
