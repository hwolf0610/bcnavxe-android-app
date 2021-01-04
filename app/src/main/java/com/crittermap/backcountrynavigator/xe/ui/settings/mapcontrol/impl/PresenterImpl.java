package com.crittermap.backcountrynavigator.xe.ui.settings.mapcontrol.impl;

import android.annotation.SuppressLint;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnUpdateUserSettingsChangedSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.mapcontrol.BCSettingsMapControlsActivityContracts;

public class PresenterImpl implements BCSettingsMapControlsActivityContracts.Presenter,
        OnUpdateUserSettingsChangedSettingsListener, BCBaseSettingsActivityContract.BaseSettingsInteractor.OnGetUserSettingsListener {

    private BCSettingsMapControlsActivityContracts.View view;
    private BCSettingsMapControlsActivityContracts.Interactor interactor;
    private OnUpdateUserSettingsChangedSettingsListener onChangedSettingsListener;
    private BCBaseSettingsActivityContract.BaseSettingsInteractor.OnGetUserSettingsListener onGetUserSettingsListener;

    public PresenterImpl(BCSettingsMapControlsActivityContracts.View view,
                         BCSettingsMapControlsActivityContracts.Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        this.onChangedSettingsListener = this;
        this.onGetUserSettingsListener = this;
    }

    @Override
    public void onSettingsChanged(BCSettings settings) {
        this.interactor.doChangeSettings(settings, onChangedSettingsListener);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onInitializeData() {
        this.interactor.doGetSettingsFromRepository(onGetUserSettingsListener);
    }

    @Override
    public void onUpdateUserSettingsSuccess() {
        view.onChangeSettingsSuccess();
    }

    @Override
    public void onUpdateUserSettingsFailed(String message) {
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
