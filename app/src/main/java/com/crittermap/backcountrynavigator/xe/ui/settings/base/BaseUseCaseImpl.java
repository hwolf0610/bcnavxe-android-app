package com.crittermap.backcountrynavigator.xe.ui.settings.base;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.share.OnGetUserSettingsSingleListener;

import javax.inject.Inject;

public class BaseUseCaseImpl implements BaseActivityContract.BaseInteractor {

    @Inject
    public GetUserSettingsUseCase getUserSettingsUseCase;

    protected BaseUseCaseImpl() {
        BCApp.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void doGetSettingsFromRepository(final OnGetUserSettingsSingleListener listener) {
        SettingsDTO settingsDTO = getUserSettingsUseCase.fetchSettingsDTO();
        if (settingsDTO == null) {
            listener.onGetUserSettingsFailed("Unable to load theme!");
        } else {
            BCSettings settings = new BCSettings();
            settings.importFromDTO(settingsDTO);
            listener.onGetUserSettingsSuccess(settings);
        }
    }
}
