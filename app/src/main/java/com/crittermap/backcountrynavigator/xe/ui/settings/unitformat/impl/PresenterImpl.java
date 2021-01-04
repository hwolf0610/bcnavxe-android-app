package com.crittermap.backcountrynavigator.xe.ui.settings.unitformat.impl;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnGetUserSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract.BaseSettingsInteractor.OnUpdateUserSettingsChangedSettingsListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.unitformat.BCSettingsUnitFormatActivityContracts;

public class PresenterImpl implements BCSettingsUnitFormatActivityContracts.Presenter,
        OnUpdateUserSettingsChangedSettingsListener, OnGetUserSettingsListener {

    private BCSettingsUnitFormatActivityContracts.View view;
    private BCSettingsUnitFormatActivityContracts.Interactor interactor;
    private OnUpdateUserSettingsChangedSettingsListener onUpdateUserSettingsChangedSettingsListener;
    private OnGetUserSettingsListener onGetUserSettingsListener;
    public PresenterImpl(BCSettingsUnitFormatActivityContracts.View view,
                         BCSettingsUnitFormatActivityContracts.Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        this.onUpdateUserSettingsChangedSettingsListener = this;
        this.onGetUserSettingsListener = this;
    }

    @Override
    public void onSwitchSettingChanged(BCSettings settings) {
        this.interactor.doChangeSettings(settings, onUpdateUserSettingsChangedSettingsListener);
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
