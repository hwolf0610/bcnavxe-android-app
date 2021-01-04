package com.crittermap.backcountrynavigator.xe.core.data.settings;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.data.ISettingsRepository;
import com.raizlabs.android.dbflow.sql.language.Select;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class SettingsRepositoryImpl implements ISettingsRepository {
    private final String TAG = SettingsRepositoryImpl.class.getSimpleName();

    @Override
    public Single<SettingsDTO> get() {
        return Single.create(new SingleOnSubscribe<SettingsDTO>() {
            @Override
            public void subscribe(SingleEmitter<SettingsDTO> emitter) {
                Settings settings = new Select()
                        .from(Settings.class)
                        .querySingle();
                if (settings == null) {
                    settings = new Settings();
                    settings.save();
                }
                Log.d(TAG, "Fetch settings successfully");
                emitter.onSuccess(settings.convertToDTO());
            }
        });
    }

    @Override
    public SettingsDTO getSettingDTO() {
        Settings settings = null;
        try {
            settings = new Select()
                    .from(Settings.class)
                    .querySingle();
            if (settings == null) {
                settings = new Settings();
                settings.save();
            }
            return settings.convertToDTO();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Maybe<SettingsDTO> findById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Single<SettingsDTO> updateOrInsert(final SettingsDTO settingsDTO) {
        return Single.create(new SingleOnSubscribe<Settings>() {
            @Override
            public void subscribe(SingleEmitter<Settings> emitter) {
                Settings settings = new Settings();
                settings.importFromDTO(settingsDTO);
                settings.save();
                Log.d(TAG, "Update or insert successfully");
                emitter.onSuccess(settings);
            }
        }).flatMap(new Function<Settings, SingleSource<? extends SettingsDTO>>() {
            @Override
            public SingleSource<? extends SettingsDTO> apply(Settings settings) {
                final SettingsDTO settingsDTO = settings.convertToDTO();
                return new SingleSource<SettingsDTO>() {
                    @Override
                    public void subscribe(SingleObserver<? super SettingsDTO> observer) {
                        observer.onSuccess(settingsDTO);
                    }
                };
            }
        });
    }
}
