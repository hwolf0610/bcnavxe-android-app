package com.crittermap.backcountrynavigator.xe.ui.mapSource.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.eventbus.BCPinnedMapClickedEvent;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;
import com.crittermap.backcountrynavigator.xe.service.BCUserService;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapIntentService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter.BCMapSourceAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

/**
 * Created by henry on 4/22/2018.
 */

public class BaseMapFragment extends Fragment implements BCMapSourceAdapter.OnMapItemClicked {

    public static final int MAX_PINNED_MAP = 5;

    @BindView(R.id.map_source_rcv)

    RecyclerView mMapSourceRcv;
    List<BCMap> maps = new ArrayList<>();
    BCMapSourceAdapter mMapSourceAdapter;
    String storeSearchQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void dismissProgress() {
        ((BCBaseActivity) getActivity()).dismissProgress();
    }

    protected void showProgress(String message) {
        ((BCBaseActivity) getActivity()).showProgress(message);
    }

    @Override
    public void onDeleteClicked(int position) {

    }

    @Override
    public void onPinClicked(int position) {
        if (maps.size() > 0) {
            BCMap pinMap = maps.get(position);
            List<BCMap> pinnedMaps = MapUtils.getPinnedMaps();
            if (pinnedMaps.size() >= MAX_PINNED_MAP) {
                showPinMapAlert(pinnedMaps, pinMap);
            } else {
                pinMap.setPinned(true);
                pinMap.setLastUsedTime(Calendar.getInstance().getTimeInMillis());
                BCDatabaseHelper.save(pinMap);
                mMapSourceAdapter.notifyDataSetChanged();
                EventBus.getDefault().postSticky(new BCPinnedMapClickedEvent(pinMap));
            }
        }
    }

    protected void showPinMapAlert(final List<BCMap> pinnedMaps, final BCMap newPinMap) {
        BCAlertDialogHelper.showMapActionDialog(getContext(), BCAlertDialogHelper.BCDialogType.PIN_MAP_ALERT,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != DialogInterface.BUTTON_POSITIVE) {
                            BCMap replaceMap = pinnedMaps.get(which);
                            replaceMap.setPinned(false);
                            newPinMap.setPinned(true);
                            newPinMap.setLastUsedTime(Calendar.getInstance().getTimeInMillis());
                            BCDatabaseHelper.update(replaceMap);
                            BCDatabaseHelper.update(newPinMap);
                            mMapSourceAdapter.notifyDataSetChanged();
                            EventBus.getDefault().postSticky(new BCPinnedMapClickedEvent(newPinMap));
                        }
                    }
                }, pinnedMaps);
    }

    @Override
    public void onUnpinClicked(int position) {
        if (maps.size() > 0) {
            BCMap pinMap = maps.get(position);
            pinMap.setPinned(false);
            BCDatabaseHelper.save(pinMap);
            mMapSourceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFavouriteClicked(int position) {
        doFavouriteMapAction(position);
    }

    protected void doFavouriteMapAction(final int position) {
        if (maps.size() > 0) {
            final BCUser user = BCUtils.getCurrentUser();
            final BCMap selectedFavouriteMap = maps.get(position);
            int isSetFav;
            if (selectedFavouriteMap.isFavoriteMap()) {
                user.getFavoriteBasemap().remove(selectedFavouriteMap.getShortName());
                isSetFav = -1;
            } else {
                isSetFav = 1;
                user.getFavoriteBasemap().add(selectedFavouriteMap.getShortName());
            }
            BCUserService userService = new BCUserService();
            userService.setFirstname(user.getFirstName());
            userService.setLastName(user.getLastName());
            userService.setFavoriteBasemap(user.getFavoriteBasemap());

            try {
                showProgress("Update user profile");
                final int finalIsSetFav = isSetFav;
                WebServiceCallBack<BCUser> webServiceCallBack = new WebServiceCallBack<BCUser>() {
                    @Override
                    public void onSuccess(BCUser data) {
                        BCUtils.saveUserShareRef(user);
                        selectedFavouriteMap.setFavoriteMap(finalIsSetFav > 0);
                        BCDatabaseHelper.save(selectedFavouriteMap);
                        mMapSourceAdapter.notifyItemChanged(position);
                        if (storeSearchQuery == null) {
                            storeSearchQuery = "";
                        }

                        EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(storeSearchQuery));
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        BCAlertDialogHelper.showErrorAlert(getContext(), BCErrorType.NETWORK_ERROR, errorMessage);
                    }
                };
                BCApiService.getInstance().doUpdateUserProfile(user.getUserName(), userService, webServiceCallBack);
            } catch (Exception e) {
                e.printStackTrace();
                BCAlertDialogHelper.showErrorAlert(getContext(), BCErrorType.NETWORK_ERROR, "Network error!!! Please try again.");
            } finally {
                dismissProgress();
            }
        }
    }

    @Override
    public void onOpenClicked(int position) {
        BCMap openMap = maps.get(position);

        if (!openMap.isPinned()) {
            List<BCMap> pinnedMaps = MapUtils.getPinnedMaps();
            if (pinnedMaps.size() >= MAX_PINNED_MAP) {
                pinnedMaps.get(0).setPinned(false);
                BCDatabaseHelper.save(pinnedMaps.get(0));
            }
            openMap.setPinned(true);
            openMap.setLastUsedTime(Calendar.getInstance().getTimeInMillis());
            BCDatabaseHelper.save(openMap);
        }
        EventBus.getDefault().postSticky(new BCPinnedMapClickedEvent(openMap));
        getActivity().finish();
    }

}
