package com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.service.permission.PermissionService;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.functions.Consumer;

@SuppressWarnings("deprecation")
public class CompassViewModel extends ViewModel implements SensorEventListener {
    private static final int MATRIX_SIZE = 9;
    private static boolean bReportedMissingOrientation = false;
    private static boolean bReportedSensors = false;
    private final String TAG = CompassViewModel.class.getSimpleName();
    private float[] mR = new float[MATRIX_SIZE];
    private float[] mOrientation = new float[3];
    private float mFilterFactor = 0.2f;
    private float mFilterFactorInv = 1 - mFilterFactor;
    private boolean useOrientation = false;
    private boolean mIsReady = false;
    private boolean bDevicesHatesLongSensorArrays = false;
    private float[] mTruncatedRotationVector = new float[4];
    private float compassBearing;

    private MutableLiveData<Float> angleLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> permissionsLiveData = new MutableLiveData<>();
    private MutableLiveData<BCSettings> settingsMutableLiveData = new MutableLiveData<>();

    private float[] mMagneticValues = new float[]{0.5f, 0f, 0f};
    private float[] mAccelerometerValues = new float[]{0f, 0f, 9.8f};
    private float mAzimuth;

    private SensorManager mSensorManager;
    private GetUserSettingsUseCase getUserSettingsUseCase;
    private int AndroidVersion;
    private PermissionService permissionService;

    @Inject
    CompassViewModel(SensorManager sensorManager,
                     GetUserSettingsUseCase getUserSettingsUseCase,
                     @Named("androidVersion") int androidVersion,
                     PermissionService permissionService) {
        this.mSensorManager = sensorManager;
        this.getUserSettingsUseCase = getUserSettingsUseCase;
        this.AndroidVersion = androidVersion;
        this.permissionService = permissionService;
    }

    MutableLiveData<Float> getAngleLiveData() {
        return angleLiveData;
    }

    MutableLiveData<Boolean> getPermissionsLiveData() {
        return permissionsLiveData;
    }

    MutableLiveData<BCSettings> getSettingsMutableLiveData() {
        return settingsMutableLiveData;
    }

    void fetchUserSettings() {
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
                        pause();
                    }
                });
    }

    void checkPermissions() {
        try {
            permissionsLiveData.postValue(permissionService.hasPermissionForSensors());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void pause() {
        try {
            unregisteredSensor();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    void resume() {
        try {
            BCSettings settings = settingsMutableLiveData.getValue();
            if (settings == null) {
                throw new Exception("Not get user settings yet");
            }
            if (!settings.isShowCompass()) {
                pause();
                return;
            }

            BCSettings.COMPASS_SENSOR_TYPE sensorType = settings.getCompassSensorType();

            Sensor orientSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            if (orientSensor == null && !bReportedMissingOrientation) {
                bReportedMissingOrientation = true;
            }

            this.useOrientation = AndroidVersion < 9;

            Sensor mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            boolean bHasMagnetic = (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null);

            int sensorFrequency = SensorManager.SENSOR_DELAY_UI;

            boolean bSuccess = registerSensors(sensorType, mRotationSensor, sensorFrequency);

            logging(bSuccess, bHasMagnetic);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean registerSensors(BCSettings.COMPASS_SENSOR_TYPE sensorType, Sensor mRotationSensor, int sensorFrequency) {
        Sensor accelSensor;
        Sensor magneticSensor;
        Sensor orientationSensor;
        Sensor gravitySensor;
        boolean bSuccess = false;
        switch (sensorType) {
            case MAGNETIC_ACCELEROMETER:
                accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                if (accelSensor != null && magneticSensor != null) {

                    bSuccess = mSensorManager.registerListener(this, magneticSensor,
                            sensorFrequency);

                    bSuccess &= mSensorManager.registerListener(this, accelSensor,
                            sensorFrequency);
                } else {
                    bSuccess = false;
                }
                break;
            case ORIENTATION:
                orientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

                if (orientationSensor != null) {
                    bSuccess &= mSensorManager.registerListener(this, orientationSensor,
                            sensorFrequency);
                } else {
                    bSuccess = false;
                }
                break;
            case MAGNETIC_GRAVITY:
                gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                if (gravitySensor != null && magneticSensor != null) {

                    bSuccess = mSensorManager.registerListener(this, magneticSensor,
                            sensorFrequency);

                    bSuccess &= mSensorManager.registerListener(this, gravitySensor,
                            sensorFrequency);
                } else {
                    bSuccess = false;
                }
                break;
            case ROTATION_VECTOR:
                bSuccess &= mSensorManager.registerListener(this, mRotationSensor,
                        sensorFrequency);
                break;
            default:
                if (mRotationSensor != null) {
                    bSuccess &= mSensorManager.registerListener(this, mRotationSensor,
                            sensorFrequency);
                }
        }
        return bSuccess;
    }

    private void logging(boolean bSuccess, boolean bHasMagnetic) {
        if (!bReportedSensors) {
            try {
                boolean bHasOrientation = (mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null);
                boolean bHasGravity = (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null);
                boolean bHasAccelerometer = (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null);
                boolean bRotation = (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null);

                String label = "Orient: " + bHasOrientation
                        + " - Mag: " + bHasMagnetic
                        + " - Gravity: " + bHasGravity
                        + " - Acc: " + bHasAccelerometer
                        + " - Rotation: " + bRotation;

                if (!bSuccess) {
                    Log.d(TAG, label);
                }

                bReportedSensors = true;
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    private void unregisteredSensor() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    private void smoothMatrix(float[] inv, float prevv[], float outv[]) {
        outv[0] = inv[0] * mFilterFactor + prevv[0] * (mFilterFactorInv);
        outv[1] = inv[1] * mFilterFactor + prevv[1] * (mFilterFactorInv);
        outv[2] = inv[2] * mFilterFactor + prevv[2] * (mFilterFactorInv);
    }

    private float updateOrientationData(float[] orientation) {
        mAzimuth = orientation[0] % 360;
        return mAzimuth;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] mValues = event.values;
        long nowtimestamp = System.nanoTime();

        if (nowtimestamp - event.timestamp > 1000000000)
            return;
        float newf = compassBearing;

        if (!this.useOrientation) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                if (this.bDevicesHatesLongSensorArrays) {
                    System.arraycopy(event.values, 0, mTruncatedRotationVector, 0, 4);
                    SensorManager.getRotationMatrixFromVector(mR, mTruncatedRotationVector);
                } else {
                    try {
                        SensorManager.getRotationMatrixFromVector(mR, event.values);
                    } catch (IllegalArgumentException ex) {
                        System.arraycopy(event.values, 0, mTruncatedRotationVector, 0, 4);
                        SensorManager.getRotationMatrixFromVector(mR, mTruncatedRotationVector);
                        this.bDevicesHatesLongSensorArrays = true;
                    }
                }

                SensorManager.getOrientation(mR, mOrientation);

                mAzimuth = mOrientation[0];

                newf = (float) (mAzimuth * 180f / Math.PI);
            } else {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        smoothMatrix(event.values, mAccelerometerValues, mAccelerometerValues);
                        mIsReady = true;
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        smoothMatrix(event.values, mMagneticValues, mMagneticValues);
                        mIsReady = true;
                        break;
                    case Sensor.TYPE_GRAVITY:
                        smoothMatrix(event.values, mAccelerometerValues, mAccelerometerValues);
                        mIsReady = true;
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        newf = updateOrientationData(event.values);
                        mIsReady = false;
                        break;
                }

                if (mIsReady && mMagneticValues != null && mAccelerometerValues != null) {
                    mIsReady = false;
                    SensorManager.getRotationMatrix(mR, null, mAccelerometerValues, mMagneticValues);
                    SensorManager.getOrientation(mR, mOrientation);

                    mAzimuth = mOrientation[0];
                    newf = (float) (mAzimuth * 180f / Math.PI);

                }
            }

        } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            Log.d(TAG, "Orientation");
            newf = mValues[0];
        }

        float diff = Math.abs(newf - compassBearing);

        if (diff >= 1.0f) {
            compassBearing = newf;
            Log.d(TAG, "Orientation changed with Angle: " + newf);
            angleLiveData.postValue(newf);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void getPermissions(Activity activity, int requestPermissionCode) {
        permissionService.getPermissionForSensors(activity, requestPermissionCode);
    }
}
