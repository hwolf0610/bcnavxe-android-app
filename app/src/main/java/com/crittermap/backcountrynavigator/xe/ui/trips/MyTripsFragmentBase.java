package com.crittermap.backcountrynavigator.xe.ui.trips;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crittermap.backcountrynavigator.xe.controller.eventbus.OnTripAdapterChanged;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripResponse;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.ui.saveTrip.BCSaveTripActivity;
import com.crittermap.backcountrynavigator.xe.ui.trips.adapter.BCTripsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper.BCDialogType.MEMBERSHIP_REQUIRE;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.PRISTINE;
import static com.crittermap.backcountrynavigator.xe.ui.trips.BCTripsActivity.SAVE_TRIP_ACTIVITY_RESULT;

/**
 * Created by thutrang.dao on 4/6/18.
 */

public class MyTripsFragmentBase extends BaseTripsFragment implements BCTripsAdapter.OnTripItemClicked {

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
    public BCTripsAdapter.OnTripItemClicked getTripItemClickedListener() {
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClickPin(final BCTripInfo tripInfo) {
        pin(tripInfo);
    }

    @Override
    public void onClickUnpin(BCTripInfo tripInfo) {
        unPin(tripInfo);
    }

    @Override
    public void onClickDownload(final BCTripInfo tripInfo) {
        download(tripInfo);
    }

    @Override
    public void onClickUpload(final BCTripInfo tripInfo) {
        if (!BCUtils.isPurchasedUser()) {
            BCAlertDialogHelper.showMembershipAlert(getContext(), MEMBERSHIP_REQUIRE, "");
            return;
        }

        showProgress("Uploading trip");
        BCTripResponse tripResponse = new BCTripResponse(tripInfo);
        BCApiService.getInstance().doUploadTrip(BC_Helper.getTripDBPath(tripInfo.getId()), BCUtils.getCurrentUser().getUserName(), tripResponse, new WebServiceCallBack() {
            @Override
            public void onSuccess(Object data) {
                Logger.e("Trip", "onUploadTripSuccess " + tripInfo.getId());
                dismissProgress();
                updateTripInfoStatus(tripInfo.getId(), PRISTINE);
            }

            @Override
            public void onFailed(String errorMessage) {
                Logger.e("Trip", "onUploadTripFailed");
                dismissProgress();
                BCAlertDialogHelper.showErrorAlert(getContext(), BCErrorType.UPLOAD_TRIP_ERROR, errorMessage);
            }
        });
    }

    @Override
    public void onClickEdit(BCTripInfo tripInfo) {
        Logger.i("Check Time", "Last sync: " + DateFormat.getDateTimeInstance().format(new Date(tripInfo.getLastSync()))
                + ", edit: " + DateFormat.getDateTimeInstance().format(new Date(tripInfo.getTimestamp())));
        Intent intent = new Intent(getActivity(), BCSaveTripActivity.class);
        intent.putExtra("tripId", tripInfo.getId());
        startActivityForResult(intent, SAVE_TRIP_ACTIVITY_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SAVE_TRIP_ACTIVITY_RESULT) {
            if (resultCode == RESULT_OK) {
                listener.onReload();
            }
        }
    }

    @Override
    public void onClickDelete(final BCTripInfo tripInfo) {
        BCAlertDialogHelper.showTripActionDialog(getContext(), BCAlertDialogHelper.BCDialogType.CONFIRM_DELETE_TRIP,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTrip(tripInfo);
                    }
                }, tripInfo.getName());
    }

    @Override
    public void onClickSync(final BCTripInfo tripInfo) {
        switch (tripInfo.getTripStatus()) {
            case OUTDATE_LOCAL:
                BCAlertDialogHelper.showTripActionDialog(getContext(), BCAlertDialogHelper.BCDialogType.SYNC_REDOWNLOAD_TRIP,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        onClickDownload(tripInfo);
                                }

                            }
                        }, null);
                break;
            case OUTDATED_REMOTE:
                BCAlertDialogHelper.showTripActionDialog(getContext(), BCAlertDialogHelper.BCDialogType.SYNC_UPLOAD_TRIP,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        onClickUpload(tripInfo);
                                }

                            }
                        }, null);
                break;
            case CONFLICTED:
                BCAlertDialogHelper.showTripActionDialog(getContext(), BCAlertDialogHelper.BCDialogType.SYNC_CONFLICT_TRIP,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        onClickDownload(tripInfo);
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        onClickUpload(tripInfo);
                                }

                            }
                        }, null);
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTripAdapterChanged(OnTripAdapterChanged event) {
        filterMyTrip(trips, event.getArr());

        Collections.sort(trips, new Comparator<BCTripInfo>() {
            @Override
            public int compare(BCTripInfo o1, BCTripInfo o2) {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp());
            }
        });

        tripsAdapter.notifyDataSetChanged();
    }

    private void filterMyTrip(final ArrayList<BCTripInfo> trips, List<BCTripInfo> arr) {
        trips.clear();
        for (BCTripInfo item : arr) {
            if (BCUtils.getCurrentUser().getUserName().equals(item.getOwnerId())) {
                trips.add(item);
            }
        }
    }
}
