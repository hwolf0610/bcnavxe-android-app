package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic;

import com.crittermap.backcountrynavigator.xe.share.BaseViewForFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;

import java.util.List;

public class BCStatSettingsFragmentContracts {
    public interface Interactor {
        void onLoadUserStatisticSetting(OnLoadUserStatisticListener listener);

        void onChangeUserStatSettings(BCStatisticModel.STATS stat, boolean isShowOnMin, boolean isShowOnMax, OnChangeUserStatisticListener listener);

        interface OnLoadUserStatisticListener {
            void onLoadUserStatisticSuccess(List<BCStatisticModel> results);

            void onLoadUserStatisticFailed(String message);
        }

        interface OnChangeUserStatisticListener {
            void onSuccess();

            void onFailed(String message);
        }
    }

    public interface Presenter {
        void loadUserStatisticSetting();

        void onSettingCheckboxClicked(BCStatisticModel.STATS stat, boolean isShowOnMinimized, boolean isShowOnMax);

        void onBtnBackToMaximizedClicked();

        void onBtnBackToMinimizedClicked();
    }

    public interface View extends BaseViewForFragment {
        void displayUserStatisticSettings(List<BCStatisticModel> results);

        void backToMaximized();

        void backToMinimized();

        void showError(String message);
    }
}
