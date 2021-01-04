package com.crittermap.backcountrynavigator.xe.ui.settings.di;

import com.crittermap.backcountrynavigator.xe.di.ApplicationModule;
import com.crittermap.backcountrynavigator.xe.ui.settings.impl.BaseSettingsUseCaseImpl;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = BaseSettingsActivityModule.class)
public interface BaseSettingsActivityComponent {
    @Component.Builder
    abstract class Builder {
        public abstract BaseSettingsActivityComponent.Builder applicationModule(ApplicationModule module);

        public abstract BaseSettingsActivityComponent build();
    }
    void inject(BaseSettingsUseCaseImpl baseSettingsUseCase);
}
