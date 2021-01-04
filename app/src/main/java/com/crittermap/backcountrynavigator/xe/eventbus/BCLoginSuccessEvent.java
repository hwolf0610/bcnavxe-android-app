package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.BCUser;

/**
 * Created by nhatdear on 3/11/18.
 */

public class BCLoginSuccessEvent {
    private BCUser user;

    public BCLoginSuccessEvent(BCUser rUser) {
        this.user = rUser;
    }

    public BCUser getUser() {
        return user;
    }
}
