package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;

/**
 * Created by thutrang.dao on 4/14/18.
 */

public class BCEventPinTripChanged {
    private final BCTripInfo bcTripInfo;
    private DrawTripEvent action;
    private BCTripInfo newTripInfo;

    public BCEventPinTripChanged(BCTripInfo bcTripInfo, DrawTripEvent action) {
        this.bcTripInfo = bcTripInfo;
        this.action = action;
    }

    public BCTripInfo getBcTripInfo() {
        return bcTripInfo;
    }



    public DrawTripEvent getAction() {
        return action;
    }

    public void setAction(DrawTripEvent action) {
        this.action = action;
    }

    public BCTripInfo getNewTripInfo() {
        return newTripInfo;
    }

    public void setNewTripInfo(BCTripInfo newTripInfo) {
        this.newTripInfo = newTripInfo;
    }

    public enum DrawTripEvent {
        PIN, UNPIN, REDRAW, REPLACE
    }
}
