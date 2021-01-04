package com.crittermap.backcountrynavigator.xe.di;

import android.arch.lifecycle.ViewModel;

import java.lang.annotation.Retention;

import dagger.MapKey;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@MapKey
public @interface ViewModelKey {
    Class<? extends ViewModel> value();
}
