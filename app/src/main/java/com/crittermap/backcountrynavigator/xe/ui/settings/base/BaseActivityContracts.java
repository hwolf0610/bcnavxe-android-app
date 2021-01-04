package com.crittermap.backcountrynavigator.xe.ui.settings.base;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;

public class BaseActivityContracts {

    public interface Interactor extends BaseActivityContract.BaseInteractor {
    }

    public interface Presenter {
        void onInitializeData();
    }

    public interface View extends BaseActivityContract.AppBaseView {
        void onGetSettingsFromRepositorySuccess(BCSettings settings);
        void onGetSettingsFromRepositoryFailed(String message);
    }
}
