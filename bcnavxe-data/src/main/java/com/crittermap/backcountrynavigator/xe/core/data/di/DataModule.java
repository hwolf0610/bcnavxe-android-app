package com.crittermap.backcountrynavigator.xe.core.data.di;

import android.content.Context;

import com.crittermap.backcountrynavigator.xe.core.data.ApplicationSharedPrefRepository;
import com.crittermap.backcountrynavigator.xe.core.data.ISettingsRepository;
import com.crittermap.backcountrynavigator.xe.core.data.application.ApplicationSharedPrefRepositoryImpl;
import com.crittermap.backcountrynavigator.xe.core.data.settings.SettingsRepositoryImpl;
import com.google.gson.Gson;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {
    @Provides
    @Singleton
    ISettingsRepository provideSettingsRepository() {
        return new SettingsRepositoryImpl();
    }

    @Provides
    @Singleton
    ApplicationSharedPrefRepository provideApplicationSharedPrefRepository(@Named("applicationContext") Context applicationContext, Gson gson) {
        return new ApplicationSharedPrefRepositoryImpl(applicationContext, gson);
    }
}
