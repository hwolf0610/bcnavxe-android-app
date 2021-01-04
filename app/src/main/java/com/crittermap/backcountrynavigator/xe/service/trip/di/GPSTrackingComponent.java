package com.crittermap.backcountrynavigator.xe.service.trip.di;

import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {GPSTrackingModule.class})
public interface GPSTrackingComponent {
    void inject(BCTrackingService service);
}
