package com.crittermap.backcountrynavigator.xe.ui.settings.powersaving.impl;

import android.annotation.SuppressLint;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnGetUserSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnUpdateUserSettingsChangedSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.powersaving.BCSettingsPowerSavingActivityContracts;

public class PresenterImpl implements BCSettingsPowerSavingActivityContracts.Presenter,
        OnUpdateUserSettingsChangedSettingsListener, OnGetUserSettingsListener {

    private BCSettingsPowerSavingActivityContracts.View view;
    private BCSettingsPowerSavingActivityContracts.Interactor interactor;
    private OnUpdateUserSettingsChangedSettingsListener onChangedSettingsListener;
    private OnGetUserSettingsListener onGetUserSettingsListener;

    public PresenterImpl(BCSettingsPowerSavingActivityContracts.View view,
                         BCSettingsPowerSavingActivityContracts.Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        this.onChangedSettingsListener = this;
        this.onGetUserSettingsListener = this;
    }

    @Override
    public void onSettingsChanged(BCSettings settings) {
        this.view.lockSaveGPSButton();
        this.interactor.doChangeSettings(settings, onChangedSettingsListener);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onInitializeData() {
        this.interactor.doGetSettingsFromRepository(onGetUserSettingsListener);
    }

    @Override
    public void onUpdateUserSettingsSuccess() {
        view.unlockSaveGPSButton();
        view.onChangeSettingsSuccess();
    }

    @Override
    public void onUpdateUserSettingsFailed(String message) {
        view.unlockSaveGPSButton();
        view.onChangeSettingsFailed(message);
    }

    @Override
    public void onGetUserSettingsSuccess(BCSettings settings) {
        view.onGetSettingsFromRepositorySuccess(settings);
    }

    @Override
    public void onGetUserSettingsFailed(String message) {
        view.onGetSettingsFromRepositoryFailed(message);
    }
}
