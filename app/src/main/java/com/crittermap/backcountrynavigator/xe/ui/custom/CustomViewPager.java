package com.crittermap.backcountrynavigator.xe.ui.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by thutrang.dao on 3/28/18.
 */

public class CustomViewPager extends ViewPager {
    private boolean isPagingEnabled = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        isPagingEnabled = pagingEnabled;
    }
}