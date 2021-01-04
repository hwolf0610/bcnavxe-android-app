package com.crittermap.backcountrynavigator.xe.common.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class BC_MapViewModel extends ViewModel {
    private MutableLiveData<String> mMapName;

    public MutableLiveData<String> getCurrentName() {
        if (mMapName == null) {
            mMapName = new MutableLiveData<>();
        }
        return mMapName;
    }
}
