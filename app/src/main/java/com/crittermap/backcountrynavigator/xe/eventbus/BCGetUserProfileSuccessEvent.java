package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.BCUser;

/**
 * Created by henry on 4/8/2018.
 */

public class BCGetUserProfileSuccessEvent {
    private BCUser mUser;

    public BCGetUserProfileSuccessEvent(BCUser user) {
        mUser = user;
    }

    public BCUser getUser() {
        return mUser;
    }
}