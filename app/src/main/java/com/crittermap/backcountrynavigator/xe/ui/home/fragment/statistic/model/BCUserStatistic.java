package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model;

import java.util.List;

public class BCUserStatistic {
    private int id;
    private long startTime;
    private long movingTime;
    private double lastAltitudeGain;
    private double lastAltitudeLoss;
    private Double lastAltitude = null;
    private double lastSpeed;
    private double lastDistance;
    private String lastLocation;
    private UserStats userStats;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //FIXME hack to unique id
    public BCUserStatistic() {
        id = 1;
        userStats = new UserStats(BCStatisticModel.initDefaultStatisticList());
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getMovingTime() {
        return movingTime;
    }

    public void setMovingTime(long movingTime) {
        this.movingTime = movingTime;
    }

    public double getLastAltitudeGain() {
        return lastAltitudeGain;
    }

    public void setLastAltitudeGain(double lastAltitudeGain) {
        this.lastAltitudeGain = lastAltitudeGain;
    }

    public Double getLastAltitude() {
        return lastAltitude;
    }

    public void setLastAltitude(Double lastAltitude) {
        this.lastAltitude = lastAltitude;
    }

    public double getLastAltitudeLoss() {
        return lastAltitudeLoss;
    }

    public void setLastAltitudeLoss(double lastAltitudeLoss) {
        this.lastAltitudeLoss = lastAltitudeLoss;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public UserStats getUserStats() {
        return userStats;
    }

    public void setUserStats(UserStats userStats) {
        this.userStats = userStats;
    }

    public List<BCStatisticModel> getUserStatsList() {
        return userStats.getUserStat();
    }

    public void setUserStatsList(List<BCStatisticModel> userStats) {
        this.userStats.setUserStat(userStats);
    }

    public double getLastSpeed() {
        return lastSpeed;
    }

    public void setLastSpeed(double lastSpeed) {
        this.lastSpeed = lastSpeed;
    }

    public double getLastDistance() {
        return lastDistance;
    }

    public void setLastDistance(double lastDistance) {
        this.lastDistance = lastDistance;
    }

}

