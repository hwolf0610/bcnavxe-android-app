package com.crittermap.backcountrynavigator.xe.ui.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

/**
 * Created by thutrang.dao on 4/18/18.
 */

public class BCSpinner extends AppCompatSpinner {
    public BCSpinner(Context context) {
        super(context);
    }

    public BCSpinner(Context context, int mode) {
        super(context, mode);
    }

    public BCSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BCSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == getSelectedItemPosition() && getOnItemSelectedListener() != null) {
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        if (position == getSelectedItemPosition() && getOnItemSelectedListener() != null) {
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}
