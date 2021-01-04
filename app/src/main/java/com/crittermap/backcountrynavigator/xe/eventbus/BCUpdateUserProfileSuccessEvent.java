package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.BCUser;

/**
 * Created by henry on 4/9/2018.
 */

public class BCUpdateUserProfileSuccessEvent {
    private BCUser mUser;

    public BCUpdateUserProfileSuccessEvent(BCUser user) {
        mUser = user;
    }

    public BCUser getUser() {
        return mUser;
    }
}
