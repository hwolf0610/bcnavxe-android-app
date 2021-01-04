package com.crittermap.backcountrynavigator.xe.ui.trips;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripIntentService;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripResponse;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.crittermap.backcountrynavigator.xe.ui.trips.adapter.BCTripsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged.DrawTripEvent.REDRAW;
import static com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged.DrawTripEvent.UNPIN;
import static com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper.BCDialogType.CONFIRM_DOWNLOAD_TRIP;
import static com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper.BCDialogType.MEMBERSHIP_REQUIRE;
import static com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper.BCDialogType.PIN_TRIP_NOT_ALLOW;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.NOT_DOWNLOADED;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.PRISTINE;


public abstract class BaseTripsFragment extends Fragment {

    BCTripsAdapter tripsAdapter;
    ArrayList<BCTripInfo> trips = new ArrayList<>();
    protected IBaseTripFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (IBaseTripFragmentListener) getContext();
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trips, container, false);
        tripsAdapter = new BCTripsAdapter(trips, getContext(), getTripItemClickedListener());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_trips);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(tripsAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }


    protected void dismissProgress() {
        listener.dismissProgress();
    }

    protected void showProgress(String message) {
        listener.showProgress(message);
    }

    public void updateTripInfoStatus(String id, TripStatus status) {
        for (BCTripInfo tripInfo : trips) {
            if (tripInfo.getId().equals(id)) {
                tripInfo.setTripStatus(status);
                tripInfo.setLastSync(Calendar.getInstance().getTimeInMillis());
                BCTripInfoDBHelper.update(tripInfo);
                tripsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadTripSuccess(final BCTripIntentService.BCDownloadTripSuccessEvent loadTripSuccessEvent) {
        Logger.i("Trip", "onDownloadTripSuccess");
        if (loadTripSuccessEvent != null) {
            BCTripResponse response = loadTripSuccessEvent.tripResponse;
            try {
                BC_TripsDBHelper.createInstance(response.getId()).dropDatabse();
                BC_TripsDBHelper.saveTripResponse(response);

                updateTripInfoStatus(response.getId(), PRISTINE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        dismissProgress();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadTripFailed(final BCTripIntentService.BCDownloadTripFailedEvent event) {
        Logger.e("Trip", "onDownloadTripFailed");
        dismissProgress();
    }

    protected void deleteTrip(BCTripInfo tripInfo) {
        switch (tripInfo.getTripStatus()) {
            case NOT_UPLOADED:
                BCTripInfoDBHelper.delete(tripInfo);
                trips.remove(tripInfo);
                BC_TripsDBHelper.createInstance(tripInfo.getId()).dropDatabse();
                break;
            default:
                tripInfo.setTripStatus(NOT_DOWNLOADED);
                tripInfo.setPinned(false);
                tripInfo.setShowedChecked(false);
                BCTripInfoDBHelper.update(tripInfo);
                BC_TripsDBHelper.createInstance(tripInfo.getId()).dropDatabse();
                break;
        }
        tripsAdapter.notifyDataSetChanged();
        EventBus.getDefault().postSticky(new BCEventPinTripChanged(tripInfo, UNPIN));
    }

    protected void unPin(BCTripInfo tripInfo) {
        tripInfo.setPinned(false);
        tripInfo.setShowedChecked(false);
        tripInfo.update();
        EventBus.getDefault().postSticky(new BCEventPinTripChanged(tripInfo, UNPIN));
    }

    protected void pin(final BCTripInfo tripInfo) {
        if (tripInfo.getTripStatus() == NOT_DOWNLOADED) {
            BCAlertDialogHelper.showTripActionDialog(getActivity(),
                    PIN_TRIP_NOT_ALLOW, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            download(tripInfo);
                        }
                    }, null);
            return;
        }

        TripUtils.actionPinTrip(tripInfo, getContext());
    }

    protected void download(final BCTripInfo tripInfo) {
        if (!BCUtils.isPurchasedUser()) {
            BCAlertDialogHelper.showMembershipAlert(getContext(), MEMBERSHIP_REQUIRE, "");
            return;
        }

        BCAlertDialogHelper.showTripActionDialog(getContext(), CONFIRM_DOWNLOAD_TRIP, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                showProgress(null);
                BCApiService.getInstance().doDownloadTrip(tripInfo.getId(), new WebServiceCallBack<File>() {
                    @Override
                    public void onSuccess(File data) {
                        Logger.i("Trip", "onDownloadTripSuccess");
                        dismissProgress();

                        if (tripInfo.isPinned() && tripInfo.isShowedChecked()) {
                            EventBus.getDefault().postSticky(new BCEventPinTripChanged(tripInfo, REDRAW));
                        }

                        updateTripInfoStatus(tripInfo.getId(), PRISTINE);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        Logger.e("Trip", "onDownloadTripFailed " + errorMessage);
                        dismissProgress();
                        BCAlertDialogHelper.showErrorAlert(getContext(), BCErrorType.DOWNLOAD_TRIP_ERROR, errorMessage);
                    }
                });
            }
        }, null);
    }

    public abstract BCTripsAdapter.OnTripItemClicked getTripItemClickedListener();

    public interface IBaseTripFragmentListener {
        void onReload();

        void showProgress(String msg);

        void dismissProgress();
    }
}
