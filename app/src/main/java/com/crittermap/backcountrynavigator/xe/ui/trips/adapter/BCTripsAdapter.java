package com.crittermap.backcountrynavigator.xe.ui.trips.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.crittermap.backcountrynavigator.xe.ui.trips.viewHolder.BCTripsViewHolder;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo.TripType.SHARED_TRIP;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.CONFLICTED;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.NOT_DOWNLOADED;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.NOT_UPLOADED;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.OUTDATED_REMOTE;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.OUTDATE_LOCAL;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.PRISTINE;

/**
 * Created by nhatdear on 3/17/18.
 */

public class BCTripsAdapter extends RecyclerView.Adapter<BCTripsViewHolder> {
    private List<BCTripInfo> mTrips;
    private Context mContext;
    private OnTripItemClicked mListener;
    private Map<TripStatus, List<Integer>> statusActionMapping = new HashMap<>();
    private List<Integer> tripActions = Arrays.asList(R.id.item_trip_action_download,
            R.id.item_trip_action_upload,
            R.id.item_trip_action_sync,
            R.id.item_trip_action_delete);

    public BCTripsAdapter(List<BCTripInfo> trips, Context context, OnTripItemClicked listener) {
        mTrips = trips;
        mContext = context;
        mListener = listener;
        statusActionMapping.put(NOT_DOWNLOADED, Collections.singletonList(R.id.item_trip_action_download));
        statusActionMapping.put(NOT_UPLOADED, Arrays.asList(
                R.id.item_trip_action_upload,
                R.id.item_trip_action_delete));
        statusActionMapping.put(OUTDATE_LOCAL, Arrays.asList(
                R.id.item_trip_action_sync,
                R.id.item_trip_action_delete));
        statusActionMapping.put(OUTDATED_REMOTE, Arrays.asList(
                R.id.item_trip_action_sync,
                R.id.item_trip_action_delete));
        statusActionMapping.put(PRISTINE, Collections.singletonList(
                R.id.item_trip_action_delete));
        statusActionMapping.put(CONFLICTED, Arrays.asList(
                R.id.item_trip_action_sync,
                R.id.item_trip_action_delete));


    }

    @Override
    public BCTripsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new BCTripsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BCTripsViewHolder holder, final int position) {
        BCTripInfo trip = mTrips.get(position);
        holder.tripName.setText(trip.getName());
        if (trip.getTripType() == SHARED_TRIP && trip.isDownloading()) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.tripAction.setVisibility(View.GONE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.tripAction.setVisibility(View.VISIBLE);
            holder.tripAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(holder, position);
                }
            });
            holder.tripItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(holder, position);
                }
            });
        }
        renderRowIcon(holder, trip);

        String dateString = DateFormat
                .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
                .format(trip.getTimestamp());
        holder.lastModified.setText(String.format(mContext.getString(R.string.txt_trip_last_modified), dateString));
        Glide.with(mContext)
                .load(trip.getImage() == null ? null : trip.getImage().getBlob())
                .apply(new RequestOptions().centerCrop())
                .into(holder.tripIcon);
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    private void renderRowIcon(BCTripsViewHolder holder, BCTripInfo trip) {
        holder.localIcon.setVisibility(trip.getTripStatus() == PRISTINE ? View.VISIBLE : View.GONE);
        switch (trip.getTripStatus()) {
            case NOT_DOWNLOADED:
                holder.tripStatus.setImageResource(R.drawable.ic_cloud);
                break;
            case NOT_UPLOADED:
                holder.tripStatus.setImageResource(R.drawable.ic_local);
                break;
            case PRISTINE:
                holder.tripStatus.setImageResource(R.drawable.ic_local);
                break;
            default:
                holder.tripStatus.setImageResource(R.drawable.ic_sync);
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(final BCTripsViewHolder holder, final int position) {
        final PopupMenu popup = new PopupMenu(mContext, holder.tripAction, Gravity.LEFT);
        popup.inflate(R.menu.menu_trip_action);
        final BCTripInfo trip = mTrips.get(position);

        for (int itemId : tripActions) {
            if (statusActionMapping.get(trip.getTripStatus()).contains(itemId)) {
                popup.getMenu().findItem(itemId).setVisible(true);
            } else {
                popup.getMenu().findItem(itemId).setVisible(false);
            }
        }

        popup.getMenu().findItem(R.id.item_trip_action_pin).setVisible(!trip.isPinned());
        popup.getMenu().findItem(R.id.item_trip_action_unpin).setVisible(trip.isPinned());

        popup.getMenu().findItem(R.id.item_trip_action_edit).setVisible(isAllowEdit(trip));
        popup.setOnMenuItemClickListener(generateOnMenuItemClickOnPopup(position, trip, holder));
        MenuPopupHelper menuHelper = new MenuPopupHelper(mContext, (MenuBuilder) popup.getMenu(), holder.tripAction);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    private boolean isAllowEdit(BCTripInfo tripInfo) {
        return tripInfo.getTripType() != SHARED_TRIP && tripInfo.getTripStatus() != NOT_DOWNLOADED;
    }

    private PopupMenu.OnMenuItemClickListener generateOnMenuItemClickOnPopup(final int position, final BCTripInfo trip, final BCTripsViewHolder holder) {
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_trip_action_unpin:
                        mListener.onClickUnpin(trip);
                        break;
                    case R.id.item_trip_action_pin:
                        mListener.onClickPin(trip);
                        break;
                    case R.id.item_trip_action_edit:
                        mListener.onClickEdit(trip);
                        break;
                    case R.id.item_trip_action_upload:
                        mListener.onClickUpload(trip);
                        break;
                    case R.id.item_trip_action_download:
                        mListener.onClickDownload(trip);
                        break;
                    case R.id.item_trip_action_delete:
                        mListener.onClickDelete(trip);
                        break;
                    case R.id.item_trip_action_sync:
                        mListener.onClickSync(trip);
                    default:
                }
                return true;
            }
        };
    }


    public interface OnTripItemClicked {
        void onClickPin(BCTripInfo tripInfo);

        void onClickUnpin(BCTripInfo tripInfo);

        void onClickDownload(BCTripInfo tripInfo);

        void onClickUpload(BCTripInfo tripInfo);

        void onClickEdit(BCTripInfo tripInfo);

        void onClickDelete(BCTripInfo tripInfo);

        void onClickSync(BCTripInfo tripInfo);
    }
}
