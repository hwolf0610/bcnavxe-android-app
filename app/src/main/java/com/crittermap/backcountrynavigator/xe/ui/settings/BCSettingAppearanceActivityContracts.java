package com.crittermap.backcountrynavigator.xe.ui.settings;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;

public class BCSettingAppearanceActivityContracts {
    public interface Interactor extends BCBaseSettingsActivityContract.BaseSettingsInteractor {
    }

    public interface Presenter {
        void onSwitchSettingChanged(BCSettings settings);

        void onInitializeData();
    }

    public interface View extends BCBaseSettingsActivityContract.BaseSettingsView {
    }
}
