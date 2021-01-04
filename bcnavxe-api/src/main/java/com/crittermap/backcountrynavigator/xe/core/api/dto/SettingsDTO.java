package com.crittermap.backcountrynavigator.xe.core.api.dto;

import com.crittermap.backcountrynavigator.xe.core.api.R;

public class SettingsDTO {
    private int id;

    private boolean isOffline;

    private boolean showZoom;

    private boolean showZoomNumber;

    private boolean showStats;

    private boolean showMapSources;

    private boolean showFullScreenMode;

    private AREA area;

    private DISTANCE distance;

    private THEME theme;

    private COORDINATES coordinates;

    private boolean allowMapRotation = true;

    private COMPASS_STYLE compassStyle;

    private COMPASS_SENSOR_TYPE compassSensorType;

    private int gpsSampleRate = 1;

    private boolean showCompass = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public boolean isShowZoom() {
        return showZoom;
    }

    public void setShowZoom(boolean showZoom) {
        this.showZoom = showZoom;
    }

    public boolean isShowZoomNumber() {
        return showZoomNumber;
    }

    public void setShowZoomNumber(boolean showZoomNumber) {
        this.showZoomNumber = showZoomNumber;
    }

    public boolean isShowStats() {
        return showStats;
    }

    public void setShowStats(boolean showStats) {
        this.showStats = showStats;
    }

    public boolean isShowMapSources() {
        return showMapSources;
    }

    public void setShowMapSources(boolean showMapSources) {
        this.showMapSources = showMapSources;
    }

    public boolean isShowFullScreenMode() {
        return showFullScreenMode;
    }

    public void setShowFullScreenMode(boolean showFullScreenMode) {
        this.showFullScreenMode = showFullScreenMode;
    }

    public boolean isAllowMapRotation() {
        return allowMapRotation;
    }

    public void setAllowMapRotation(boolean allowMapRotation) {
        this.allowMapRotation = allowMapRotation;
    }

    public AREA getArea() {
        return area;
    }

    public void setArea(AREA area) {
        this.area = area;
    }

    public DISTANCE getDistance() {
        return distance;
    }

    public void setDistance(DISTANCE distance) {
        this.distance = distance;
    }

    public COORDINATES getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(COORDINATES coordinates) {
        this.coordinates = coordinates;
    }

    public void setTheme(THEME theme) {
        this.theme = theme;
    }

    public THEME getTheme() {
        return theme;
    }

    public COMPASS_STYLE getCompassStyle() {
        return compassStyle;
    }

    public void setCompassStyle(COMPASS_STYLE compassStyle) {
        this.compassStyle = compassStyle;
    }

    public int getGpsSampleRate() {
        return gpsSampleRate;
    }

    public void setGpsSampleRate(int gpsSampleRate) {
        this.gpsSampleRate = gpsSampleRate;
    }

    public COMPASS_SENSOR_TYPE getCompassSensorType() {
        return compassSensorType;
    }

    public void setCompassSensorType(COMPASS_SENSOR_TYPE compassSensorType) {
        this.compassSensorType = compassSensorType;
    }

    public boolean isShowCompass() {
        return showCompass;
    }

    public void setShowCompass(boolean showCompass) {
        this.showCompass = showCompass;
    }

    public enum AREA {
        Hec(R.string.txt_unit_hec),
        Acr(R.string.txt_unit_acr);

        public final int valueResId;

        AREA(int valueResId) {
            this.valueResId = valueResId;
        }
    }

    public enum DISTANCE {
        Miles(R.string.txt_unit_miles),
        Km(R.string.txt_unit_km);

        public final int valueResId;

        DISTANCE(int valueResId) {
            this.valueResId = valueResId;
        }
    }

    public enum THEME {
        DAY(R.string.txt_day),
        NIGHT(R.string.txt_night),
        AUTO(R.string.txt_auto);


        public  int valueResId;

        THEME(int valueResId) {
            this.valueResId = valueResId;
        }
    }

    public enum COORDINATES {
        Dec(R.string.txt_unit_dec),
        Deg(R.string.txt_unit_deg),
        Dms(R.string.txt_unit_dms),
        UTM(R.string.txt_unit_utm),
        MGRS(R.string.txt_unit_mgrs);

        public final int valueResId;

        COORDINATES(int valueResId) {
            this.valueResId = valueResId;
        }
    }

    public enum COMPASS_STYLE {
        ORIENTEERING(R.string.txt_compass_style_orienteering),
        BEARING(R.string.txt_compass_style_bearing);

        public final int valueResId;

        COMPASS_STYLE(int valueResId) {
            this.valueResId = valueResId;
        }
    }

    public enum COMPASS_SENSOR_TYPE {
        ROTATION_VECTOR(R.string.txt_rotation_vector),
        MAGNETIC_ACCELEROMETER(R.string.txt_magnetic_accele),
        MAGNETIC_GRAVITY(R.string.txt_magnetic_graviy),
        ORIENTATION(R.string.txt_orientation);

        public final int valueResId;

        COMPASS_SENSOR_TYPE(int valueResId) {
            this.valueResId = valueResId;
        }
    }
}
