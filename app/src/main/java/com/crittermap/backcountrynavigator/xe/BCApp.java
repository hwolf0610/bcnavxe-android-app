package com.crittermap.backcountrynavigator.xe;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.crittermap.backcountrynavigator.xe.di.ApplicationComponent;
import com.crittermap.backcountrynavigator.xe.di.ApplicationModule;
import com.crittermap.backcountrynavigator.xe.di.DaggerApplicationComponent;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.singleton.BCSingleton;
import com.raizlabs.android.dbflow.config.DataGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.fabric.sdk.android.Fabric;

public class BCApp extends DaggerApplication {

    private static final String TAG = BCApp.class.getSimpleName();
    private static BCApp mApp;
    private ApplicationModule applicationModule;
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        //TODO need to migrate all data into data
        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseHolder(DataGeneratedDatabaseHolder.class)
                .build());

        //FIXME not to use BCSingleton anymore
        BCSingleton singleton = BCSingleton.getInstance();
        singleton.setContext(getApplicationContext());

        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);
    }

    public static BCApp getInstance() {
        return mApp;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public File getDatabasePath(String name) {
        return super.getDatabasePath(name);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public String getServerUrl() {
        return BCConstant.API_URL;
    }

    public ApplicationModule getApplicationModule() {
        if (applicationModule == null) {
            applicationModule = new ApplicationModule(getServerUrl(), this, getAppVersionString());
        }
        return applicationModule;
    }
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        Log.e(TAG, "Initialising App Injector");
        applicationComponent = (ApplicationComponent) DaggerApplicationComponent.builder()
                .applicationModule(getApplicationModule()).create(this);
        return applicationComponent;
    }

    public String getAppVersionString() {
        String packageName = this.getPackageName();
        try {
            return this.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }
}
