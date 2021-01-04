package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.service.BCResponseStatus;

/**
 * Created by henry on 3/16/2018.
 */

public class BCChangePasswordSuccessEvent {
    private BCResponseStatus responseStatus;

    public BCChangePasswordSuccessEvent(BCResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public BCResponseStatus getResponseStatus() {
        return responseStatus;
    }
}
