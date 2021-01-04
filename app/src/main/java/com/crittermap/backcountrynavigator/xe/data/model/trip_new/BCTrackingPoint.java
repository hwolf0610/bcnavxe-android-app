package com.crittermap.backcountrynavigator.xe.data.model.trip_new;

public class BCTrackingPoint {
    private String id;
    private String jsonPoint;
    private int groupId;

    public BCTrackingPoint() {
    }

    public BCTrackingPoint(String id, String jsonPoint, int groupId) {
        this.id = id;
        this.jsonPoint = jsonPoint;
        this.groupId = groupId;
    }

    public String getJsonPoint() {
        return jsonPoint;
    }

    public void setJsonPoint(String jsonPoint) {
        this.jsonPoint = jsonPoint;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
