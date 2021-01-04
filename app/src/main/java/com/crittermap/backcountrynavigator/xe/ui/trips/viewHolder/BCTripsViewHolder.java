package com.crittermap.backcountrynavigator.xe.ui.trips.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henry on 3/21/2018.
 */

public class BCTripsViewHolder extends RecyclerView.ViewHolder {
    public @BindView(R.id.tv_content)
    TextView tripName;
    public @BindView(R.id.trip_icon)
    ImageView tripIcon;
    public @BindView(R.id.trip_action)
    ImageView tripAction;
    public @BindView(R.id.tripStatus)
    ImageView tripStatus;
    @BindView(R.id.localIcon)
    public ImageView localIcon;
    @BindView(R.id.tv_last_modified)
    public TextView lastModified;
    @BindView(R.id.trip_loading)
    public ProgressBar progressBar;
    @BindView(R.id.trip_item)
    public RelativeLayout tripItem;

    public BCTripsViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}