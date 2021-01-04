package com.crittermap.backcountrynavigator.xe.di;

import android.content.Context;
import android.hardware.SensorManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ApplicationModule.class)
public class SensorManagerModule {
    @Provides
    @Singleton
    public SensorManager provideSensorManager(@Named("applicationContext") Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
}
