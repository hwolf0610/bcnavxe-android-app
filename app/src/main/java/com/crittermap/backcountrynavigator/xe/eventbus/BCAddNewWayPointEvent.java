package com.crittermap.backcountrynavigator.xe.eventbus;

import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;

public class BCAddNewWayPointEvent {
    private BCTrip trip;
    private BCGeometry geometry;

    public BCAddNewWayPointEvent(BCTrip trip, BCGeometry geometry) {
        this.trip = trip;
        this.geometry = geometry;
    }

    public BCTrip getTrip() {
        return trip;
    }

    public void setTrip(BCTrip trip) {
        this.trip = trip;
    }

    public BCGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(BCGeometry geometry) {
        this.geometry = geometry;
    }
}
