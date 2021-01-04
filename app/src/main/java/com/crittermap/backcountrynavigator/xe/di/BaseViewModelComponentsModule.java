package com.crittermap.backcountrynavigator.xe.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BaseViewModelComponentsModule {
    @Provides
    @Singleton
    ProvidesProgress provideProvidesProgress() {
        return new ProvidesProgressImpl();
    }
}
