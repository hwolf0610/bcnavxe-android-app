package com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess.impl;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnGetUserSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnUpdateUserSettingsChangedSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess.BCSettingsQuickAccessActivityContracts;
import com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess.BCSettingsQuickAccessActivityContracts.Interactor;
import com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess.BCSettingsQuickAccessActivityContracts.View;

public class PresenterImpl implements BCSettingsQuickAccessActivityContracts.Presenter,
        OnUpdateUserSettingsChangedSettingsListener, OnGetUserSettingsListener {

    private View view;
    private Interactor interactor;
    private OnUpdateUserSettingsChangedSettingsListener onChangedSettingsListener;
    private OnGetUserSettingsListener onGetUserSettingsListener;

    public PresenterImpl(View view,
                         Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        this.onChangedSettingsListener = this;
        this.onGetUserSettingsListener = this;
    }

    @Override
    public void onSwitchSettingChanged(BCSettings setting) {
        this.interactor.doChangeSettings(setting, onChangedSettingsListener);
    }

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
