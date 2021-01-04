package com.crittermap.backcountrynavigator.xe.di;

import com.crittermap.backcountrynavigator.xe.BCApp;
import com.crittermap.backcountrynavigator.xe.core.data.di.DataModule;
import com.crittermap.backcountrynavigator.xe.ui.home.di.HomeActivityBinderModule;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass.di.CompassFragmentBinderModule;
import com.crittermap.backcountrynavigator.xe.ui.settings.base.BaseUseCaseImpl;
import com.crittermap.backcountrynavigator.xe.ui.splashScreen.di.SplashScreenBinderModule;
import com.crittermap.backcountrynavigator.xe.ui.trips.di.TripActivityBinderModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        AndroidSupportInjectionModule.class,
        ActivityBuilder.class,
        BaseViewModelComponentsModule.class,
        HomeActivityBinderModule.class,
        TripActivityBinderModule.class,
        SplashScreenBinderModule.class,
        DataModule.class,
        AndroidPermissionModule.class,
        SensorManagerModule.class,
        CompassFragmentBinderModule.class
})
public interface ApplicationComponent extends AndroidInjector<BCApp> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<BCApp> {
        public abstract Builder applicationModule(ApplicationModule module);

        @Override
        public abstract ApplicationComponent build();
    }

    void inject(BaseUseCaseImpl baseUseCase);
}
