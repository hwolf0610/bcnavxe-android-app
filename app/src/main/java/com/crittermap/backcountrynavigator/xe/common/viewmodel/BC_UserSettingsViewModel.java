package com.crittermap.backcountrynavigator.xe.common.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.data.settings.Settings;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.others.UpdateUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class BC_UserSettingsViewModel extends ViewModel {
    private static final String TAG = BC_UserSettingsViewModel.class.getSimpleName();

    private GetUserSettingsUseCase getUserSettingsUseCase;

    private UpdateUserSettingsUseCase updateUserSettingsUseCase;

    private MutableLiveData<BCSettings> settingData;

    @Inject
    BC_UserSettingsViewModel(GetUserSettingsUseCase getUserSettingsUseCase, UpdateUserSettingsUseCase updateUserSettingsUseCase) {
        this.getUserSettingsUseCase = getUserSettingsUseCase;
        this.updateUserSettingsUseCase = updateUserSettingsUseCase;
    }

    public MutableLiveData<BCSettings> getCurrentUserSettings() {
        if (settingData == null) {
            settingData = new MutableLiveData<>();
        }
        return settingData;
    }

    public void fetchUserSettingsData() {
        getUserSettingsUseCase.execute(new Consumer<SettingsDTO>() {
            @Override
            public void accept(SettingsDTO settingsDTO) {
                Log.d(TAG, "fetchUserSettingsData success");
                BCSettings settings = new BCSettings();
                settings.importFromDTO(settingsDTO);
                settingData.postValue(settings);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.e(TAG, "fetchUserSettingsData failed: " + throwable.getMessage());
                fetchDefaultUserSettingsData();
            }
        });
    }

    private void updateUserSettingsData(final BCSettings settings) {
        updateUserSettingsUseCase.execute(settings.convertToDTO(),
                new Consumer() {
                    @Override
                    public void accept(Object o) {
                        settingData.postValue(settings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "updateUserSettingsData failed: " + throwable.getMessage());
                        fetchDefaultUserSettingsData();
                    }
                });
    }

    private void fetchDefaultUserSettingsData() {
        BCSettings settings = new BCSettings();
        settingData.postValue(settings);
    }

    public void setOffline(boolean isOffline) {
        BCSettings settings = settingData.getValue();
        if (settings != null) {
            settings.setOffline(isOffline);
            updateUserSettingsData(settings);
        }
    }

    public void setShowCompass(boolean isShow, final Runnable onSuccess, Consumer<Throwable> onError) {
        final BCSettings settings = settingData.getValue();
        if (settings != null) {
            settings.setShowCompass(isShow);
            updateUserSettingsUseCase.execute(
                    settings.convertToDTO(),
                    new Consumer() {
                        @Override
                        public void accept(Object o) {
                            onSuccess.run();
                        }
                    },
                    onError);
        }
    }
}
