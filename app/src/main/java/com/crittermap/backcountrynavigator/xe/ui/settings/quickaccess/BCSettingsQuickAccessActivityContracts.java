package com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract;

public class BCSettingsQuickAccessActivityContracts {
    public interface Interactor extends BCBaseSettingsActivityContract.BaseSettingsInteractor {
    }

    public interface Presenter {
        void onSwitchSettingChanged(BCSettings setting);

        void onInitializeData();
    }

    public interface View extends BCBaseSettingsActivityContract.BaseSettingsView {
    }
}
