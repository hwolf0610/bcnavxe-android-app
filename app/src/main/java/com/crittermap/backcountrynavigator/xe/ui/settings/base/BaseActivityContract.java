package com.crittermap.backcountrynavigator.xe.ui.settings.base;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.share.BaseView;
import com.crittermap.backcountrynavigator.xe.share.OnGetUserSettingsSingleListener;
import com.crittermap.backcountrynavigator.xe.share.OnUpdateUserSettingsCompleteListener;

public class BaseActivityContract {

    public interface BaseInteractor {

        void doGetSettingsFromRepository(OnGetUserSettingsSingleListener listener);


        interface OnGetUserSettingsListener extends OnGetUserSettingsSingleListener {
        }
    }

    public interface AppBaseView {

        void initializeData();

        void onGetSettingsFromRepositorySuccess(BCSettings settings);

        void onGetSettingsFromRepositoryFailed(String message);
    }
}
