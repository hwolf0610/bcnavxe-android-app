package com.crittermap.backcountrynavigator.xe.data.model.trip_new;

import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCTripInfo extends BaseModel {
    @Column
    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String ownerId;
    @Column
    private boolean isPinned;
    @Column
    private TripStatus tripStatus = TripStatus.NOT_DOWNLOADED;
    @Column
    private long timestamp; //last edited time
    @Column
    private boolean isShowedChecked;
    @Column
    private String trekFolder;
    @Column
    private Blob image;
    @Column
    private long lastSync;

    private transient boolean shareTrek;

    private transient boolean isDownloading;

    private transient TripType tripType = TripType.MY_TRIP;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public boolean isShowedChecked() {
        return isShowedChecked;
    }

    public void setShowedChecked(boolean showedChecked) {
        isShowedChecked = showedChecked;
    }

    public String getTrekFolder() {
        return trekFolder;
    }

    public void setTrekFolder(String trekFolder) {
        this.trekFolder = trekFolder;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }

    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    public boolean isShareTrek() {
        return shareTrek;
    }

    public void setShareTrek(boolean shareTrek) {
        this.shareTrek = shareTrek;
    }

    public enum TripType {
        MY_TRIP, SHARED_TRIP
    }
}
