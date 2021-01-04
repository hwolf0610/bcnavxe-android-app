package com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatSettingsFragmentContracts;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCStatisticModel;

import java.util.List;

public class BCStatSettingsFragmentPresenterImpl implements BCStatSettingsFragmentContracts.Presenter,
        BCStatSettingsFragmentContracts.Interactor.OnLoadUserStatisticListener, BCStatSettingsFragmentContracts.Interactor.OnChangeUserStatisticListener {
    private static final String TAG = BCStatSettingsFragmentPresenterImpl.class.getSimpleName();
    private BCStatSettingsFragmentContracts.Interactor interactor;
    private BCStatSettingsFragmentContracts.View view;
    private BCStatSettingsFragmentContracts.Interactor.OnLoadUserStatisticListener onLoadUserStatisticListener;
    private BCStatSettingsFragmentContracts.Interactor.OnChangeUserStatisticListener onChangeUserStatisticListener;

    public BCStatSettingsFragmentPresenterImpl(BCStatSettingsFragmentContracts.View view, BCStatSettingsFragmentContracts.Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        onLoadUserStatisticListener = this;
        onChangeUserStatisticListener = this;
    }

    @Override
    public void onLoadUserStatisticSuccess(List<BCStatisticModel> results) {
        view.displayUserStatisticSettings(results);
    }

    @Override
    public void onLoadUserStatisticFailed(String message) {
        view.showError(message);
    }

    @Override
    public void loadUserStatisticSetting() {
        interactor.onLoadUserStatisticSetting(onLoadUserStatisticListener);
    }

    @Override
    public void onSettingCheckboxClicked(BCStatisticModel.STATS stat, boolean isShowOnMinimized, boolean isShowOnMax) {
        interactor.onChangeUserStatSettings(stat, isShowOnMinimized, isShowOnMax, onChangeUserStatisticListener);
    }

    @Override
    public void onBtnBackToMaximizedClicked() {
        view.backToMaximized();
    }

    @Override
    public void onBtnBackToMinimizedClicked() {
        view.backToMinimized();
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "Change user stats settings success");
    }

    @Override
    public void onFailed(String message) {
        view.showError(message);
    }
}
