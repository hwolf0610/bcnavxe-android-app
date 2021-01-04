package com.crittermap.backcountrynavigator.xe.ui.settings.unitformat;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract;

public class BCSettingsUnitFormatActivityContracts {
    public interface Interactor extends BCBaseSettingsActivityContract.BaseSettingsInteractor {
    }

    public interface Presenter {
        void onSwitchSettingChanged(BCSettings settings);

        void onInitializeData();
    }

    public interface View extends BCBaseSettingsActivityContract.BaseSettingsView {
    }
}
