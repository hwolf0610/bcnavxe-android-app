package com.crittermap.backcountrynavigator.xe.ui.splashScreen.di;

import com.crittermap.backcountrynavigator.xe.di.ActivityScope;
import com.crittermap.backcountrynavigator.xe.ui.splashScreen.BCSplashScreenActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SplashScreenBinderModule {
    @ActivityScope
    @ContributesAndroidInjector
    public abstract BCSplashScreenActivity contributeBCSplashScreenActivity();
}
