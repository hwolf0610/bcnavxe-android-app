package com.crittermap.backcountrynavigator.xe.share;

/**
 * Created by nhatdear on 3/17/18.
 */

public enum TripStatus {
    NOT_DOWNLOADED("notDownloaded"), // Trip has not been ever downloaded by user yet.
    NOT_UPLOADED("notUploaded"), // User created trip but has not been ever uploaded to server yet.
    OUTDATE_LOCAL("outdatedLocal"), // Trip has been updated from server, need to be synced with local.
    OUTDATED_REMOTE("outdatedRemote"), // Trip has been updated from local, need to be synced with server.
    CONFLICTED("conflicted"), // Trip has been updated from local and server, user can select to choose the version to be synced.
    PRISTINE("pristine"); // Trip is pristine from both server and local

    private String value;

    TripStatus(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static TripStatus fromString(String string) {
        for (TripStatus tripStatus : TripStatus.values()) {
            if (tripStatus.value.equals(string)) return tripStatus;
        }
        return NOT_DOWNLOADED;
    }
}
