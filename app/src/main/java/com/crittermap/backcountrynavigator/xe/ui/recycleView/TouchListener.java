package com.crittermap.backcountrynavigator.xe.ui.recycleView;

import android.view.View;

/**
 * Created by henry on 3/21/2018.
 */

public interface TouchListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}