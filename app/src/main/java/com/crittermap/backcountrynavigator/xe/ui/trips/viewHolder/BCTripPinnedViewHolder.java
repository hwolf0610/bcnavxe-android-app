package com.crittermap.backcountrynavigator.xe.ui.trips.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by henry on 4/6/2018.
 */

public class BCTripPinnedViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.checkBox)
    public CheckBox checkBox;
    @BindView(R.id.textView)
    public TextView tripPinnedTextView;
    @BindView(R.id.iv_view)
    public ImageView viewTrip;


    public BCTripPinnedViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.textView)
    public void onTripPinnedLayoutClicked() {
        checkBox.toggle();
    }
}