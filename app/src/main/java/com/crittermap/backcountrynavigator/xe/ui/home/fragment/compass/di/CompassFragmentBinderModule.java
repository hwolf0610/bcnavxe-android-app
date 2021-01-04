package com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass.di;

import android.arch.lifecycle.ViewModel;

import com.crittermap.backcountrynavigator.xe.di.FragmentScope;
import com.crittermap.backcountrynavigator.xe.di.ViewModelKey;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass.CompassFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass.CompassViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class CompassFragmentBinderModule {
    @Binds
    @IntoMap
    @ViewModelKey(CompassViewModel.class)
    abstract ViewModel bindCompassViewModel(CompassViewModel compassViewModel);

    @FragmentScope
    @ContributesAndroidInjector
    public abstract CompassFragment contributeCompassFragment();
}
