package com.crittermap.backcountrynavigator.xe.di;

import android.content.Context;

import com.crittermap.backcountrynavigator.xe.service.permission.PermissionService;
import com.crittermap.backcountrynavigator.xe.service.permission.PermissionServiceImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ApplicationModule.class)
public class AndroidPermissionModule {
    @Provides
    @Singleton
    PermissionService providePermissionService(@Named("applicationContext") Context context) {
        return new PermissionServiceImpl(context);
    }
}
