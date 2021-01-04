package com.crittermap.backcountrynavigator.xe.share;

import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;

public interface OnGetUserSettingsSingleListener {
    void onGetUserSettingsSuccess(BCSettings settings);

    void onGetUserSettingsFailed(String message);
}
