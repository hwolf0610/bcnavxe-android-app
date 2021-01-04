package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.service.BCResponseStatus;

/**
 * Created by nhatdear on 3/11/18.
 */

public class BCRegisterSuccessEvent {
    private BCResponseStatus responseStatus;
    private BCUser user;
    public BCRegisterSuccessEvent(BCResponseStatus responseStatus, BCUser user) {
        this.responseStatus = responseStatus;
        this.user = user;
    }

    public BCResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public BCUser getUser() {
        return user;
    }
}
