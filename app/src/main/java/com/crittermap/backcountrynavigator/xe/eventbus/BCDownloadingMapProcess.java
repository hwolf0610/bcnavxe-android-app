package com.crittermap.backcountrynavigator.xe.eventbus;

public class BCDownloadingMapProcess {
    private double progressPercent;
    private String mapName;

    public BCDownloadingMapProcess(int success, int total, int fail, String mapName) {
        this.progressPercent = (double) (success + fail) / (double) total * 100;
        this.mapName = mapName;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public String getMapName() {
        return mapName;
    }
}
