package com.crittermap.backcountrynavigator.xe.ui.settings.powersaving;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract;

public class BCSettingsPowerSavingActivityContracts {
    public interface Interactor extends BCBaseSettingsActivityContract.BaseSettingsInteractor {
    }

    public interface Presenter {
        void onSettingsChanged(BCSettings settings);

        void onInitializeData();
    }

    public interface View extends BCBaseSettingsActivityContract.BaseSettingsView {
        void lockSaveGPSButton();

        void unlockSaveGPSButton();
    }
}
