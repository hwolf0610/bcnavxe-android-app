package com.crittermap.backcountrynavigator.xe.service.trip.di;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class BCGPSTracking implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = BCGPSTracking.class.getSimpleName();
    private static BCGPSTracking instance;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFuseLocationClient;
    private LocationChangedCallback mLocationChangedCallback;

    public BCGPSTracking() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(1f);

        mFuseLocationClient = LocationServices.getFusedLocationProviderClient(BCApp.getInstance());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e("BCGPSTracking", "mLocationCallback");
                if (mLocationChangedCallback != null && locationResult != null) {
                    mLocationChangedCallback.onLocationChanged(locationResult.getLastLocation());
                }
            }
        };
    }


    public static BCGPSTracking getInstance() {
        if (instance == null) {
            instance = new BCGPSTracking();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdate(BCSettings settings) {
        if (hasLocationPermission()) {
            return;
        }
        Log.e("BCGPSTracking", "startLocationUpdate with settings: $settings");
        mLocationRequest.setInterval(settings.getGpsSampleRate() * 1000);
        mLocationRequest.setFastestInterval(settings.getGpsSampleRate() * 1000);
        mFuseLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(OnSuccessListener<Location> onSuccessListener, OnFailureListener onFailureListener) {
        if (hasLocationPermission()) {
            return;
        }

        mFuseLocationClient.getLastLocation()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(BCApp.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(BCApp.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
    }

    public void setLocationCallback(LocationChangedCallback callback) {
        this.mLocationChangedCallback = callback;
    }

    public void stopLocationUpdate() {
        mFuseLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    public interface LocationChangedCallback {
        void onLocationChanged(Location location);
    }

}
