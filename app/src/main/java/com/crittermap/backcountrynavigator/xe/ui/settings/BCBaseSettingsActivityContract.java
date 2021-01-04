package com.crittermap.backcountrynavigator.xe.ui.settings;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.share.BaseView;
import com.crittermap.backcountrynavigator.xe.share.OnGetUserSettingsSingleListener;
import com.crittermap.backcountrynavigator.xe.share.OnUpdateUserSettingsCompleteListener;

public class BCBaseSettingsActivityContract {
    public interface BaseSettingsInteractor {
        void doChangeSettings(BCSettings settings, OnUpdateUserSettingsChangedSettingsListener listener);

        void doGetSettingsFromRepository(OnGetUserSettingsSingleListener listener);

        interface OnUpdateUserSettingsChangedSettingsListener extends OnUpdateUserSettingsCompleteListener {
        }

        interface OnGetUserSettingsListener extends OnGetUserSettingsSingleListener {
        }
    }

    public interface BaseSettingsView extends BaseView {
        void onChangeSettingsSuccess();

        void onChangeSettingsFailed(String message);

        void onGetSettingsFromRepositorySuccess(BCSettings settings);

        void onGetSettingsFromRepositoryFailed(String message);
    }
}
