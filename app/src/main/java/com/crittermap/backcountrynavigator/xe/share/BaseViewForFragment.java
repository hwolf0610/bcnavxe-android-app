package com.crittermap.backcountrynavigator.xe.share;

import android.view.View;

public interface BaseViewForFragment {
    void initializeData();

    void initializeAdapters();

    void initializeViews(View view);

    void initializeEvents(View view);
}
