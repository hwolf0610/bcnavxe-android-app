package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl;

import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatFragmentContracts;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;

import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl.STAT_MODE.EXPAND;
import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl.STAT_MODE.MINI;

public class BCStatFragmentPresenterImpl implements BCStatFragmentContracts.Presenter,
        BCStatFragmentContracts.Interactor.OnLoadMinimizedUserStatisticListener,
        BCStatFragmentContracts.Interactor.OnLoadMaximizedUserStatisticListener {
    private BCStatFragmentContracts.Interactor interactor;
    private BCStatFragmentContracts.View view;
    private BCStatFragmentContracts.Interactor.OnLoadMinimizedUserStatisticListener onLoadUserStatisticListener;
    private BCStatFragmentContracts.Interactor.OnLoadMaximizedUserStatisticListener onLoadMaximizedUserStatisticListener;

    public BCStatFragmentPresenterImpl(BCStatFragmentContracts.View view, BCStatFragmentContracts.Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        onLoadUserStatisticListener = this;
        onLoadMaximizedUserStatisticListener = this;
    }

    @Override
    public void loadMinimizedUserStatisticSetting() {
        interactor.onLoadMinimizedUserStatisticSetting(onLoadUserStatisticListener);
    }

    @Override
    public void loadMaximizedUserStatisticSetting() {
        interactor.onLoadMaximizedUserStatisticSetting(onLoadMaximizedUserStatisticListener);
    }

    @Override
    public void onBtnExpandClicked() {
        STAT_MODE statMode = view.getStatMode();
        if (statMode == MINI) {
            loadMaximizedUserStatisticSetting();
        } else if (statMode == EXPAND) {
            loadMinimizedUserStatisticSetting();
        }
    }

    @Override
    public void onBtnSettingsClicked() {
        view.openSettings();
    }

    @Override
    public void onLoadMinimizedUserStatisticSuccess(BCUserStatistic results) {
        view.displayMinimizedUserStatisticSettings(results);
    }

    @Override
    public void onLoadMinimizedUserStatisticFailed(String message) {
        view.showError(message);
    }

    @Override
    public void onLoadMaximizedUserStatisticSuccess(BCUserStatistic results) {
        view.displayMaximizedUserStatisticSettings(results);
    }

    @Override
    public void onLoadMaximizedUserStatisticFailed(String message) {
        view.showError(message);
    }

    public enum STAT_MODE {
        MINI,
        EXPAND
    }
}
