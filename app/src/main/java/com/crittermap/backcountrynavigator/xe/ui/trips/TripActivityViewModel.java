package com.crittermap.backcountrynavigator.xe.ui.trips;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class TripActivityViewModel extends ViewModel {
    private static final String TAG = TripActivityViewModel.class.getSimpleName();
    private GetUserSettingsUseCase getUserSettingsUseCase;
    private MutableLiveData<BCSettings> settingsMutableLiveData = new MutableLiveData<>();

    @Inject
    public TripActivityViewModel(GetUserSettingsUseCase getUserSettingsUseCase) {
        this.getUserSettingsUseCase = getUserSettingsUseCase;
    }

    public void fetchUserSettings() {
        getUserSettingsUseCase.execute(
                new Consumer<SettingsDTO>() {
                    @Override
                    public void accept(SettingsDTO settingsDTO) {
                        BCSettings settings = new BCSettings();
                        settings.importFromDTO(settingsDTO);
                        settingsMutableLiveData.postValue(settings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage());
                        settingsMutableLiveData.postValue(null);
                    }
                });
    }

    public MutableLiveData<BCSettings> getSettingsMutableLiveData() {
        return settingsMutableLiveData;
    }
}
