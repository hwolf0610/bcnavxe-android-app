package com.crittermap.backcountrynavigator.xe.data.model;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = BCDatabase.class, cachingEnabled = true)
public class BCSettings extends BaseModel implements Cloneable {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private boolean isOffline = false;
    @Column
    private boolean showZoom = true;
    @Column
    private boolean showZoomNumber = true;
    @Column
    private boolean showStats = true;
    @Column
    private boolean showMapSources = true;
    @Column
    private boolean showFullScreenMode = false;
    @Column
    private AREA area = AREA.Hec;
    @Column
    private DISTANCE distance = DISTANCE.Miles;
    @Column
    private COORDINATES coordinates = COORDINATES.Dec;
    @Column
    private boolean allowMapRotation = true;
    @Column
    private COMPASS_STYLE compassStyle = BCSettings.COMPASS_STYLE.ORIENTEERING;
    @Column
    private int gpsSampleRate = 1;
    @Column
    private COMPASS_SENSOR_TYPE compassSensorType = COMPASS_SENSOR_TYPE.ORIENTATION;
    @Column
    private boolean showCompass = false;
    @Column
    private THEME theme = THEME.DAY;
    public BCSettings() {
    }

    public void setTheme(THEME theme) {
        this.theme = theme;
    }

    public THEME getTheme() {
        return theme;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isAllowMapRotation() {
        return allowMapRotation;
    }

    public void setAllowMapRotation(boolean allowMapRotation) {
        this.allowMapRotation = allowMapRotation;
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

    public enum QUICK_ACCESS_OPTIONS {
        SCALE_BAR_NUMBER,
        ZOOM,
        STATS,
        MAP_SOURCES,
        FULL_SCREEN_MODE
    }

    public BCSettings importFromDTO(SettingsDTO settingsDTO) {
        this.setId(settingsDTO.getId());
        this.setOffline(settingsDTO.isOffline());
        this.setShowZoom(settingsDTO.isShowZoom());
        this.setShowZoomNumber(settingsDTO.isShowZoomNumber());
        this.setShowStats(settingsDTO.isShowStats());
        this.setShowMapSources(settingsDTO.isShowMapSources());
        this.setShowFullScreenMode(settingsDTO.isShowFullScreenMode());
        this.setArea(AREA.values()[settingsDTO.getArea().ordinal()]);
        this.setDistance(DISTANCE.values()[settingsDTO.getDistance().ordinal()]);
        this.setCoordinates(COORDINATES.values()[settingsDTO.getCoordinates().ordinal()]);
        this.setCompassStyle(COMPASS_STYLE.values()[settingsDTO.getCompassStyle().ordinal()]);
        this.setCompassSensorType(COMPASS_SENSOR_TYPE.values()[settingsDTO.getCompassSensorType().ordinal()]);
        this.setAllowMapRotation(settingsDTO.isAllowMapRotation());
        this.setGpsSampleRate(settingsDTO.getGpsSampleRate());
        this.setShowCompass(settingsDTO.isShowCompass());
        this.setTheme(THEME.values()[settingsDTO.getTheme().ordinal()]);
        return this;
    }

    public enum BACKUP_OPTION {
        APP_DATA(R.string.txt_app_data),
        APP_SETTINGS(R.string.txt_app_settings),
        ACCOUNT(R.string.txt_account);

        public final int valueResId;

        BACKUP_OPTION(int valueResId) {
            this.valueResId = valueResId;
        }
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

    public enum DATA_STORAGE {
        INTERNAL(R.string.txt_internal),
        SD_CARD(R.string.txt_sd_card);

        public final int valueResId;

        DATA_STORAGE(int valueResId) {
            this.valueResId = valueResId;
        }
    }

    public SettingsDTO convertToDTO() {
        SettingsDTO settingsDTO = new SettingsDTO();
        settingsDTO.setId(this.getId());
        settingsDTO.setOffline(this.isOffline());
        settingsDTO.setShowZoom(this.isShowZoom());
        settingsDTO.setShowZoomNumber(this.isShowZoomNumber());
        settingsDTO.setShowStats(this.isShowStats());
        settingsDTO.setShowMapSources(this.isShowMapSources());
        settingsDTO.setShowFullScreenMode(this.isShowFullScreenMode());
        settingsDTO.setArea(SettingsDTO.AREA.values()[this.getArea().ordinal()]);
        settingsDTO.setDistance(SettingsDTO.DISTANCE.values()[this.getDistance().ordinal()]);
        settingsDTO.setCoordinates(SettingsDTO.COORDINATES.values()[this.getCoordinates().ordinal()]);
        settingsDTO.setCompassStyle(SettingsDTO.COMPASS_STYLE.values()[this.getCompassStyle().ordinal()]);
        settingsDTO.setCompassSensorType(SettingsDTO.COMPASS_SENSOR_TYPE.values()[this.getCompassSensorType().ordinal()]);
        settingsDTO.setAllowMapRotation(this.isAllowMapRotation());
        settingsDTO.setGpsSampleRate(this.getGpsSampleRate());
        settingsDTO.setShowCompass(this.isShowCompass());
        settingsDTO.setTheme(SettingsDTO.THEME.values()[this.getTheme().ordinal()]);
        return settingsDTO;
    }

    public BCSettings clone() throws CloneNotSupportedException {
        return (BCSettings) super.clone();
    }

    public enum THEME {
        DAY(R.string.txt_day),
        NIGHT(R.string.txt_night),
        AUTO(R.string.txt_auto);

        public final int valueResId;

        THEME(int valueResId) {
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

