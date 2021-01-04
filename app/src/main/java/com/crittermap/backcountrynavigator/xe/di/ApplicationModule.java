package com.crittermap.backcountrynavigator.xe.di;

import android.content.Context;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.crittermap.backcountrynavigator.xe.core.domain.JobExecutor;
import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;
import com.crittermap.backcountrynavigator.xe.share.FragmentHelper;
import com.google.gson.Gson;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Module
public class ApplicationModule {
    private String serverUrl;
    private BCApp application;
    private String appVersionString;
    private Gson gson;

    public ApplicationModule(String serverUrl, BCApp application, String appVersionString) {
        this.serverUrl = serverUrl;
        this.application = application;
        this.appVersionString = appVersionString;
        this.gson = new Gson();
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return gson;
    }

    @Provides
    @Singleton
    String providesServerUrl() {
        return serverUrl;
    }

    @Provides
    @Singleton
    @Named("applicationContext")
    Context providesContext() {
        return application;
    }

    @Provides
    @Singleton
    @Named("androidVersion")
    Integer provideAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    @Provides
    @Singleton
    @Named("appVersion")
    String provideAppVersion() {
        return appVersionString;
    }

    @Provides
    @Singleton
    @Named("appVersionInt")
    int provideAppVersionInt() {
        return Integer.parseInt(appVersionString.replaceAll("\\.", ""));
    }

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor() {
        return new JobExecutor();
    }

    @Provides
    @Singleton
    PostExecutionThread providesPostExecutionThread() {
        return new ExecutionThread();
    }

    @Provides
    @Singleton
    FragmentHelper providesFragmentHelper() {
        return new FragmentHelper();
    }

    class ExecutionThread extends PostExecutionThread {
        @Override
        public Scheduler getScheduler() {
            return AndroidSchedulers.mainThread();
        }
    }
}
