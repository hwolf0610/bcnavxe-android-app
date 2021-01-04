package com.crittermap.backcountrynavigator.xe.service.trip;

import java.util.List;

/**
 * Created by nhatdear on 3/17/18.
 */

public class BCTripUploadResponse {

    private List<BCTripResponse> trip;

    public List<BCTripResponse> getTrip() {
        return trip;
    }

    public void setTrip(List<BCTripResponse> trip) {
        this.trip = trip;
    }
}
