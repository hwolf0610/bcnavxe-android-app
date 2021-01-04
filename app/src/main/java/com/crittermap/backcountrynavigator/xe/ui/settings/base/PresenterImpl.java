package com.crittermap.backcountrynavigator.xe.ui.settings.base;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract;

public class PresenterImpl implements BaseActivityContracts.Presenter,
        BaseActivityContract.BaseInteractor.OnGetUserSettingsListener {

    private BaseActivityContracts.View view;
    private BaseActivityContracts.Interactor interactor;
    private BaseActivityContract.BaseInteractor.OnGetUserSettingsListener onGetUserSettingsListener;

    public PresenterImpl(BaseActivityContracts.View view,
                         BaseActivityContracts.Interactor interactor) {
        this.interactor = interactor;
        this.view = view;
        this.onGetUserSettingsListener = this;
    }

    @Override
    public void onGetUserSettingsSuccess(BCSettings settings) {
        view.onGetSettingsFromRepositorySuccess(settings);
    }

    @Override
    public void onGetUserSettingsFailed(String message) {

    }

    @Override
    public void onInitializeData() {
        this.interactor.doGetSettingsFromRepository(onGetUserSettingsListener);
    }
}
