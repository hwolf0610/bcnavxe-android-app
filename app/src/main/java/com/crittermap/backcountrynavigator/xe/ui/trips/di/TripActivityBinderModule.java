package com.crittermap.backcountrynavigator.xe.ui.trips.di;

import android.arch.lifecycle.ViewModel;

import com.crittermap.backcountrynavigator.xe.di.ActivityScope;
import com.crittermap.backcountrynavigator.xe.di.ViewModelKey;
import com.crittermap.backcountrynavigator.xe.ui.trips.BCTripsActivity;
import com.crittermap.backcountrynavigator.xe.ui.trips.TripActivityViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class TripActivityBinderModule {
    @Binds
    @IntoMap
    @ViewModelKey(TripActivityViewModel.class)
    abstract ViewModel bindTripActivityViewModel(TripActivityViewModel tripActivityViewModel);

    @ActivityScope
    @ContributesAndroidInjector
    public abstract BCTripsActivity contributeBCTripActivity();
}
