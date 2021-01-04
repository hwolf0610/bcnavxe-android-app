package com.crittermap.backcountrynavigator.xe.ui.trips.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;
import com.crittermap.backcountrynavigator.xe.ui.trips.BCDestinationActivity;
import com.crittermap.backcountrynavigator.xe.ui.trips.viewHolder.BCDestinationViewHolder;
import com.crittermap.backcountrynavigator.xe.ui.trips.viewHolder.BCTripPinnedViewHolder;

import java.util.List;

/**
 * Created by henry on 4/16/2018.
 */

public class BCDestinationAdapter extends RecyclerView.Adapter<BCDestinationViewHolder> {
    private List<BCTripInfo> mTrips;
    private Context mContext;

    public BCDestinationAdapter(List<BCTripInfo> trips, Context context) {
        mTrips = trips;
        mContext = context;
    }

    @Override
    public BCDestinationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.destination_item, parent, false);
        return new BCDestinationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BCDestinationViewHolder holder, final int position) {
        final BCTripInfo trip = mTrips.get(position);
        holder.destinationName.setText(trip.getName());
        holder.destinationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof BCDestinationActivity) {
                    ((BCDestinationActivity) mContext).goToTrip(trip);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }
}