package com.crittermap.backcountrynavigator.xe.ui.trips;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.crittermap.backcountrynavigator.xe.controller.eventbus.OnTripAdapterChanged;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.trips.adapter.BCTripsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo.TripType.SHARED_TRIP;

/**
 * Created by thutrang.dao on 4/6/18.
 */

public class DownloadedTripsFragment extends BaseTripsFragment implements BCTripsAdapter.OnTripItemClicked {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public BCTripsAdapter.OnTripItemClicked getTripItemClickedListener() {
        return this;
    }

    @Override
    public void onClickPin(BCTripInfo tripInfo) {
        pin(tripInfo);
    }

    @Override
    public void onClickUnpin(BCTripInfo tripInfo) {
        unPin(tripInfo);
    }

    @Override
    public void onClickDownload(BCTripInfo tripInfo) {
        download(tripInfo);
    }

    @Override
    public void onClickUpload(BCTripInfo tripInfo) {

    }

    @Override
    public void onClickEdit(BCTripInfo tripInfo) {

    }

    @Override
    public void onClickDelete(BCTripInfo tripInfo) {
        deleteTrip(tripInfo);
    }

    @Override
    public void onClickSync(BCTripInfo tripInfo) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTripAdapterChanged(OnTripAdapterChanged event) {
        filterSharedTrip(trips, event.getArr());

        for (final BCTripInfo trip : trips) {
            BCApiService.getInstance().doDownloadSharedTrip(trip.getId(), new WebServiceCallBack<BCTripInfo>() {
                @Override
                public void onSuccess(BCTripInfo data) {
                    trip.setDownloading(false);
                    trip.setName(data.getName());
                    trip.setImage(data.getImage());
                    trip.setTrekFolder(data.getTrekFolder());
                    trip.setTimestamp(data.getTimestamp());
                    trip.setLastSync(Calendar.getInstance().getTimeInMillis());
                    trip.setShareTrek(data.isShareTrek());
                    BCTripInfoDBHelper.save(trip);

                    tripsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(String errorMessage) {
                    trip.setDownloading(false);
                    tripsAdapter.notifyDataSetChanged();
                }
            });

        }

        tripsAdapter.notifyDataSetChanged();
    }

    private void filterSharedTrip(final ArrayList<BCTripInfo> trips, List<BCTripInfo> arr) {
        trips.clear();
        for (BCTripInfo item : arr) {
            if (!BCUtils.getCurrentUser().getUserName().equals(item.getOwnerId())) {
                item.setTripType(SHARED_TRIP);
                item.setDownloading(true);
                trips.add(item);
            }
        }
    }
}
