package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl;

import android.support.annotation.NonNull;

import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatFragmentContracts;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;

import java.util.ArrayList;
import java.util.List;

public class BCStatFragmentInteractorImpl implements BCStatFragmentContracts.Interactor {
    @Override
    public void onLoadMinimizedUserStatisticSetting(OnLoadMinimizedUserStatisticListener listener) {
        try {
            BCUserStatistic results = getMinimizedSettings();
            listener.onLoadMinimizedUserStatisticSuccess(results);
        } catch (Exception e) {
            listener.onLoadMinimizedUserStatisticFailed(e.getMessage());
        }
    }

    @Override
    public void onLoadMaximizedUserStatisticSetting(OnLoadMaximizedUserStatisticListener listener) {
        try {
            BCUserStatistic results = getMaximizedSettings();
            listener.onLoadMaximizedUserStatisticSuccess(results);
        } catch (Exception e) {
            listener.onLoadMaximizedUserStatisticFailed(e.getMessage());
        }
    }

    @NonNull
    private BCUserStatistic getMinimizedSettings() {
        BCUserStatistic userStats = BCUtils.getUserStatSettings();
        List<BCStatisticModel> results = new ArrayList<>();
        for (BCStatisticModel statisticModel : userStats.getUserStatsList()) {
            if (statisticModel.isShowOnMinimize()) {
                results.add(statisticModel);
            }
        }
        userStats.setUserStatsList(results);
        return userStats;
    }

    @NonNull
    private BCUserStatistic getMaximizedSettings() {
        BCUserStatistic userStats = BCUtils.getUserStatSettings();
        List<BCStatisticModel> results = new ArrayList<>();
        for (BCStatisticModel statisticModel : userStats.getUserStatsList()) {
            if (statisticModel.isShowOnFull()) {
                results.add(statisticModel);
            }
        }
        userStats.setUserStatsList(results);
        return userStats;
    }
}
