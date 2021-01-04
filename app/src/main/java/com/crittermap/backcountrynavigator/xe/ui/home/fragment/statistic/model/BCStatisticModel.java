package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model;

import com.crittermap.backcountrynavigator.xe.R;

import java.io.Serializable;
import java.util.ArrayList;

public class BCStatisticModel implements Serializable {
    private STATS stat;
    private boolean showOnMinimize;
    private boolean showOnFull;
    private String content;

    private BCStatisticModel(STATS stat, boolean showOnMinimize, boolean showOnFull) {
        this.stat = stat;
        this.showOnMinimize = showOnMinimize;
        this.showOnFull = showOnFull;
    }

    public static ArrayList<BCStatisticModel> initDefaultStatisticList() {
        ArrayList<BCStatisticModel> statisticModels = new ArrayList<>();
        statisticModels.add(new BCStatisticModel(STATS.SPEED, false, false));
        statisticModels.add(new BCStatisticModel(STATS.ACCURACY, false, false));
        statisticModels.add(new BCStatisticModel(STATS.HEADING, false, false));
        statisticModels.add(new BCStatisticModel(STATS.ATTITUDE, false, false));
        statisticModels.add(new BCStatisticModel(STATS.MOVING_TIME, false, false));
        statisticModels.add(new BCStatisticModel(STATS.ATTITUDE_GAIN_LOSS, false, false));
        statisticModels.add(new BCStatisticModel(STATS.AVG_SPEED, false, false));
        statisticModels.add(new BCStatisticModel(STATS.MAX_SPEED, false, false));
        statisticModels.add(new BCStatisticModel(STATS.MIN_ATTITUDE, false, false));
        statisticModels.add(new BCStatisticModel(STATS.MAX_ATTITUDE, false, false));
        return statisticModels;
    }

    public STATS getStat() {
        return stat;
    }

    public void setStat(STATS stat) {
        this.stat = stat;
    }

    public boolean isShowOnMinimize() {
        return showOnMinimize;
    }

    public void setShowOnMinimize(boolean showOnMinimize) {
        this.showOnMinimize = showOnMinimize;
    }

    public boolean isShowOnFull() {
        return showOnFull;
    }

    public void setShowOnFull(boolean showOnFull) {
        this.showOnFull = showOnFull;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTypeIconId() {
        switch (this.stat) {
            case SPEED:
                return R.drawable.ic_speed;
            case HEADING:
                return R.drawable.ic_head;
            case ACCURACY:
                return R.drawable.ic_accuracy;
            case ATTITUDE:
                return R.drawable.ic_latitude;
            case ATTITUDE_GAIN_LOSS:
                return R.drawable.ic_latitude;
            case AVG_SPEED:
                return R.drawable.ic_avg_speed;
            case MOVING_TIME:
                return R.drawable.ic_moving_time;
            case MAX_SPEED:
                return R.drawable.ic_maxspeed;
            case MAX_ATTITUDE:
                return R.drawable.ic_max_laltitude;
            case MIN_ATTITUDE:
                return R.drawable.ic_min_laltitude;
            default:
                return R.drawable.ic_accuracy;
        }
    }

    public enum STATS {
        SPEED("Speed"),
        ACCURACY("Accuracy"),
        HEADING("Heading"),
        ATTITUDE("Attitude"),
        MOVING_TIME("Moving time"),
        ATTITUDE_GAIN_LOSS("Altitude gain/loss"),
        AVG_SPEED("Avg speed"),
        MAX_SPEED("Max speed"),
        MAX_ATTITUDE("Max attitude"),
        MIN_ATTITUDE("Min attitude");

        private String name;

        STATS(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }
    }
}
