package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatSettingsFragmentContracts;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;

public class BCStatSettingsFragmentInteractorImpl implements BCStatSettingsFragmentContracts.Interactor {
    @Override
    public void onLoadUserStatisticSetting(OnLoadUserStatisticListener listener) {
        try {
            listener.onLoadUserStatisticSuccess(BCUtils.getUserStatSettings().getUserStatsList());
        } catch (Exception e) {
            listener.onLoadUserStatisticFailed(e.getMessage());
        }
    }

    @Override
    public void onChangeUserStatSettings(BCStatisticModel.STATS stat, boolean isShowOnMinimized, boolean isShowOnMax, OnChangeUserStatisticListener listener) {
        try {
            Log.d("ChangeUserStatSettings", stat.getName() + " - " + isShowOnMinimized + " - " + isShowOnMax);
            BCUserStatistic userStats = BCUtils.getUserStatSettings();
            for (BCStatisticModel statisticModel : userStats.getUserStatsList()) {
                if (statisticModel.getStat().equals(stat)) {
                    statisticModel.setShowOnMinimize(isShowOnMinimized);
                    statisticModel.setShowOnFull(isShowOnMax);
                    BCUtils.saveUserStatSettings(userStats);
                    break;
                }
            }
            listener.onSuccess();
        } catch (Exception e) {
            listener.onFailed(e.getMessage());
        }
    }
}
