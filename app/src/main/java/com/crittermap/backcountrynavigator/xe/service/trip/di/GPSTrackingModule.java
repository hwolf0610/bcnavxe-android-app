package com.crittermap.backcountrynavigator.xe.service.trip.di;

import com.crittermap.backcountrynavigator.xe.core.data.di.DataModule;
import com.crittermap.backcountrynavigator.xe.di.ApplicationModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DataModule.class, ApplicationModule.class})
public class GPSTrackingModule {
    @Provides
    @Singleton
    BCGPSTracking provideGPSTracking() {
        return new BCGPSTracking();
    }
}
