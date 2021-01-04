package com.crittermap.backcountrynavigator.xe.data.model.trip_new;


public class BCTrip {
    private String id;
    private String name;
    private String startLoc;
    private String endLoc;
    private String desc;
    private String longDesc;
    private String unicodeDesc;
    private String sharedType;
    private String sharedLink;
    private String ownerId;
    private String membershipType;
    private long timestamp;
    private String folder;
    private byte[] image;
    private int tripZoom;

    public BCTrip() {
    }

    public BCTrip(BCTripInfo tripInfo) {
        this.id = tripInfo.getId();
        this.name = tripInfo.getName();
        this.folder = tripInfo.getTrekFolder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(String startLoc) {
        this.startLoc = startLoc;
    }

    public String getEndLoc() {
        return endLoc;
    }

    public void setEndLoc(String endLoc) {
        this.endLoc = endLoc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getUnicodeDesc() {
        return unicodeDesc;
    }

    public void setUnicodeDesc(String unicodeDesc) {
        this.unicodeDesc = unicodeDesc;
    }

    public String getSharedType() {
        return sharedType;
    }

    public void setSharedType(String sharedType) {
        this.sharedType = sharedType;
    }

    public String getSharedLink() {
        return sharedLink;
    }

    public void setSharedLink(String sharedLink) {
        this.sharedLink = sharedLink;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getTripZoom() {
        return tripZoom;
    }

    public void setTripZoom(int tripZoom) {
        this.tripZoom = tripZoom;
    }
}
