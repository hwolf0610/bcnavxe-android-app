package com.crittermap.backcountrynavigator.xe.service.permission;

import android.app.Activity;

public interface PermissionService {
    boolean hasPermissionForSensors();

    void getPermissionForSensors(Activity activity, int requestPermissionCode);
}
