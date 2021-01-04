package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic;

import com.crittermap.backcountrynavigator.xe.share.BaseViewForFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;

public class BCStatFragmentContracts {
    public interface Interactor {
        void onLoadMinimizedUserStatisticSetting(OnLoadMinimizedUserStatisticListener listener);

        void onLoadMaximizedUserStatisticSetting(OnLoadMaximizedUserStatisticListener listener);

        interface OnLoadMinimizedUserStatisticListener {
            void onLoadMinimizedUserStatisticSuccess(BCUserStatistic results);

            void onLoadMinimizedUserStatisticFailed(String message);
        }

        interface OnLoadMaximizedUserStatisticListener {
            void onLoadMaximizedUserStatisticSuccess(BCUserStatistic results);

            void onLoadMaximizedUserStatisticFailed(String message);
        }
    }

    public interface Presenter {
        void loadMinimizedUserStatisticSetting();

        void loadMaximizedUserStatisticSetting();

        void onBtnExpandClicked();

        void onBtnSettingsClicked();
    }

    public interface View extends BaseViewForFragment {
        void displayMinimizedUserStatisticSettings(BCUserStatistic results);

        void displayMaximizedUserStatisticSettings(BCUserStatistic results);

        void openSettings();

        void showError(String message);

        BCStatFragmentPresenterImpl.STAT_MODE getStatMode();
    }
}
