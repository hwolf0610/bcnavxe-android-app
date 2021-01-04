package com.crittermap.backcountrynavigator.xe.ui.trips.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henry on 4/16/2018.
 */

public class BCDestinationViewHolder extends RecyclerView.ViewHolder {

    public @BindView(R.id.destination_layout)
    RelativeLayout destinationLayout;
    public @BindView(R.id.destination_name)
    TextView destinationName;

    public BCDestinationViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}