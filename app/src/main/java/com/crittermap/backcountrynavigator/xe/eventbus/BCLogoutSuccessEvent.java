package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.service.BCResponseStatus;

/**
 * Created by henryhai on 3/16/18.
 */

public class BCLogoutSuccessEvent {

    private BCResponseStatus responseStatus;
    public BCLogoutSuccessEvent(BCResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public BCResponseStatus getResponseStatus() {
        return responseStatus;
    }
}
