package com.crittermap.backcountrynavigator.xe.di;

import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;

@Module
abstract class ActivityBuilder {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
