package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model;

import java.util.List;

public class UserStats {
    private List<BCStatisticModel> userStat;

    public UserStats(List<BCStatisticModel> userStat) {
        this.userStat = userStat;
    }

    public List<BCStatisticModel> getUserStat() {
        return userStat;
    }

    public void setUserStat(List<BCStatisticModel> userStat) {
        this.userStat = userStat;
    }
}
