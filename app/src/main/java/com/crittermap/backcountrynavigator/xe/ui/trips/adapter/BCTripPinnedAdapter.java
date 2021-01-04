package com.crittermap.backcountrynavigator.xe.ui.trips.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.ui.trips.viewHolder.BCTripPinnedViewHolder;

import java.util.List;

/**
 * Created by henry on 4/6/2018.
 */

public class BCTripPinnedAdapter extends RecyclerView.Adapter<BCTripPinnedViewHolder> {
    private List<BCTripInfo> mTrips;
    private Context mContext;
    private CompoundButton.OnCheckedChangeListener listener;
    private OnNavigatePinnedTripListener navigatePinnedTripListener;

    public BCTripPinnedAdapter(List<BCTripInfo> trips, Context context, CompoundButton.OnCheckedChangeListener listener) {
        mTrips = trips;
        mContext = context;
        this.listener = listener;
    }

    @Override
    public BCTripPinnedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_pinned_item, parent, false);
        return new BCTripPinnedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BCTripPinnedViewHolder holder, final int position) {
        final BCTripInfo trip = mTrips.get(position);
        holder.tripPinnedTextView.setText(trip.getName());
        holder.checkBox.setTag(trip.getId());
        holder.checkBox.setChecked(trip.isShowedChecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onCheckedChanged(buttonView, isChecked);
                    trip.setShowedChecked(isChecked);
                    BCTripInfoDBHelper.update(trip);
                }
                handleViewTrip(isChecked, holder);
            }
        });

        handleViewTrip(trip.isShowedChecked(), holder);
        holder.viewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigatePinnedTripListener != null) {
                    navigatePinnedTripListener.onClick(trip);
                }
            }
        });
    }

    private void handleViewTrip(boolean isShowed, BCTripPinnedViewHolder holder) {
        if (isShowed) {
            holder.viewTrip.setVisibility(View.VISIBLE);
        } else {
            holder.viewTrip.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public void setNavigatePinnedTripListener(OnNavigatePinnedTripListener navigatePinnedTripListener) {
        this.navigatePinnedTripListener = navigatePinnedTripListener;
    }

    public interface OnNavigatePinnedTripListener {
        void onClick(BCTripInfo tripInfo);
    }

}