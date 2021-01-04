package com.crittermap.backcountrynavigator.xe.ui.settings.appearance.impl;

import android.annotation.SuppressLint;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnUpdateUserSettingsChangedSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCSettingAppearanceActivityContracts;
import com.crittermap.backcountrynavigator.xe.ui.settings.mapcontrol.BCSettingsMapControlsActivityContracts;

public class PresenterImpl implements BCSettingAppearanceActivityContracts.Presenter,
        OnUpdateUserSettingsChangedSettingsListener, BCBaseSettingsActivityContract.BaseSettingsInteractor.OnGetUserSettingsListener {

    private BCSettingAppearanceActivityContracts.View view;
    private BCSettingAppearanceActivityContracts.Interactor interactor;
    private OnUpdateUserSettingsChangedSettingsListener onChangedSettingsListener;
    private BCBaseSettingsActivityContract.BaseSettingsInteractor.OnGetUserSettingsListener onGetUserSettingsListener;

    public PresenterImpl(BCSettingAppearanceActivityContracts.View view,
                         BCSettingAppearanceActivityContracts.Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        this.onChangedSettingsListener = this;
        this.onGetUserSettingsListener = this;
    }

    @Override
    public void onGetUserSettingsSuccess(BCSettings settings) {
        view.onGetSettingsFromRepositorySuccess(settings);
    }

    @Override
    public void onGetUserSettingsFailed(String message) {
        view.onGetSettingsFromRepositoryFailed(message);
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
    public void onSwitchSettingChanged(BCSettings settings) {
        this.interactor.doChangeSettings(settings, onChangedSettingsListener);
    }

    @Override
    public void onInitializeData() {
        this.interactor.doGetSettingsFromRepository(onGetUserSettingsListener);
    }
}
