package com.crittermap.backcountrynavigator.xe.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final String TAG = ViewModelFactory.class.getSimpleName();
    public Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        this.creators = creators;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<ViewModel> creator = null;
        for (Class<? extends ViewModel> key : creators.keySet()) {
            if (modelClass.isAssignableFrom(key)) {
                creator = creators.get(key);
                break;
            }
        }
        if (creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass.getClass().getSimpleName());
        } else {
            try {
                return (T) creator.get();
            } catch (Exception e) {
                Log.e(TAG, "There was an error fetching ViewModel", e);
                throw new RuntimeException(e);
            }
        }
    }
}
