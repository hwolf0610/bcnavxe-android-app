package com.crittermap.backcountrynavigator.xe.service.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionServiceImpl implements PermissionService {
    private String[] permissionsForSensors
            = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private Context applicationContext;

    public PermissionServiceImpl(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean hasPermissionForSensors() {
        return hasAccessCoarseLocation() && hasAccessFineLocation();
    }

    @Override
    public void getPermissionForSensors(Activity activity, int requestPermissionCode) {
        ActivityCompat.requestPermissions(activity, permissionsForSensors, requestPermissionCode);
    }

    private boolean hasAccessCoarseLocation() {
        return ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasAccessFineLocation() {
        return ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
