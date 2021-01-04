package com.crittermap.backcountrynavigator.xe.service.trip;

public class BCTrackingStatusChangedEvent {

    private TrackingStatus trackingStatus;

    public BCTrackingStatusChangedEvent(TrackingStatus trackingStatus) {
        this.trackingStatus = trackingStatus;
    }

    public TrackingStatus getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(TrackingStatus trackingStatus) {
        this.trackingStatus = trackingStatus;
    }


    public enum TrackingStatus {
        TRACKING,
        PAUSE,
        STOP
    }
}
