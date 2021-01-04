package com.crittermap.backcountrynavigator.xe.ui.settings.impl;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.others.UpdateUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.share.OnGetUserSettingsSingleListener;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivityContract;
import com.crittermap.backcountrynavigator.xe.ui.settings.di.BaseSettingsActivityComponent;
import com.crittermap.backcountrynavigator.xe.ui.settings.di.DaggerBaseSettingsActivityComponent;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class BaseSettingsUseCaseImpl implements BCBaseSettingsActivityContract.BaseSettingsInteractor {
    @Inject
    public UpdateUserSettingsUseCase updateUserSettingsUseCase;

    @Inject
    public GetUserSettingsUseCase getUserSettingsUseCase;

    protected BaseSettingsUseCaseImpl() {
        BaseSettingsActivityComponent component
                = DaggerBaseSettingsActivityComponent
                .builder()
                .applicationModule(BCApp.getInstance().getApplicationModule())
                .build();
        component.inject(this);
    }

    @Override
    public void doChangeSettings(BCSettings settings, final OnUpdateUserSettingsChangedSettingsListener listener) {
        updateUserSettingsUseCase
                .execute(settings.convertToDTO(),
                        new Consumer<SettingsDTO>() {
                            @Override
                            public void accept(SettingsDTO o) {
                                listener.onUpdateUserSettingsSuccess();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                listener.onUpdateUserSettingsFailed(throwable.getMessage());
                            }
                        });
    }

    @Override
    public void doGetSettingsFromRepository(final OnGetUserSettingsSingleListener listener) {
        getUserSettingsUseCase.execute(
                new Consumer<SettingsDTO>() {
                    @Override
                    public void accept(SettingsDTO settingsDTO) {
                        BCSettings settings = new BCSettings();
                        settings.importFromDTO(settingsDTO);
                        listener.onGetUserSettingsSuccess(settings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        listener.onGetUserSettingsFailed(throwable.getMessage());
                    }
                });
    }
}