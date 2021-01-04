package com.crittermap.backcountrynavigator.xe.core.data;

import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;

import io.reactivex.Single;

public interface ISettingsRepository extends Repository<SettingsDTO> {
    Single<SettingsDTO> get();
    SettingsDTO getSettingDTO();
}
