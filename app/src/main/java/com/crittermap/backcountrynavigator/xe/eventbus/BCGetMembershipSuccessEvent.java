package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;


public class BCGetMembershipSuccessEvent {
    private BCMembership membership;

    public BCGetMembershipSuccessEvent(BCMembership membership) {
        this.membership = membership;
    }

    public BCMembership getMembership() {
        return membership;
    }
}
