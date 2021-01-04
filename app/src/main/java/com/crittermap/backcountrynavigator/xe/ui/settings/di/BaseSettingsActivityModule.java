package com.crittermap.backcountrynavigator.xe.ui.settings.di;

import com.crittermap.backcountrynavigator.xe.core.data.di.DataModule;
import com.crittermap.backcountrynavigator.xe.di.ApplicationModule;

import dagger.Module;

@Module(includes = {ApplicationModule.class, DataModule.class})
public class BaseSettingsActivityModule {
}
