package com.crittermap.backcountrynavigator.xe.ui.home.di;

import android.arch.lifecycle.ViewModel;

import com.crittermap.backcountrynavigator.xe.common.viewmodel.BC_UserSettingsViewModel;
import com.crittermap.backcountrynavigator.xe.di.ActivityScope;
import com.crittermap.backcountrynavigator.xe.di.FragmentScope;
import com.crittermap.backcountrynavigator.xe.di.ViewModelKey;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatFragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class HomeActivityBinderModule {
    @Binds
    @IntoMap
    @ViewModelKey(BC_UserSettingsViewModel.class)
    abstract ViewModel bindUserSettingsViewModel(BC_UserSettingsViewModel userSettingsViewModel);

    @ActivityScope
    @ContributesAndroidInjector
    public abstract BCHomeActivity contributeBCHomeActivity();

    @FragmentScope
    @ContributesAndroidInjector
    public abstract BCStatFragment contributeBCStatFragment();
}
