package com.crittermap.backcountrynavigator.xe.ui.mapSource.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapIntentService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.MAP_STATUS;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter.BCAutoCompleteSearchAdapter;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter.BCMapSourceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by henry on 4/25/2018.
 */

public class BCFavoriteMapFragment extends BaseMapFragment implements BCAutoCompleteSearchAdapter.OnItemClicked {

    private BCUser mCurrentUser;
    private BCAutoCompleteSearchAdapter mAutoCompleteSearchAdapter;
    private RecyclerView mAutoCompleteSearchRcv;
    private int currentTab = -1;

    public BCFavoriteMapFragment() {

    }

    @SuppressLint("ValidFragment")
    public BCFavoriteMapFragment(RecyclerView autoCompleteSearchRcv) {
        super();
        mAutoCompleteSearchRcv = autoCompleteSearchRcv;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = BCUtils.getCurrentUser();
        loadFavoriteMapFromLocal();
        mAutoCompleteSearchAdapter = new BCAutoCompleteSearchAdapter(maps, this);
        mAutoCompleteSearchRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAutoCompleteSearchRcv.setAdapter(mAutoCompleteSearchAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mAutoCompleteSearchRcv.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mAutoCompleteSearchRcv.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_map, container, false);
        ButterKnife.bind(this, view);
        mMapSourceAdapter = new BCMapSourceAdapter(maps, getContext(), this);
        mMapSourceRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMapSourceRcv.setAdapter(mMapSourceAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mMapSourceRcv.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mMapSourceRcv.addItemDecoration(dividerItemDecoration);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemSelected(String mapName) {
        mAutoCompleteSearchRcv.setVisibility(View.GONE);
        mAutoCompleteSearchAdapter.notifyDataSetChanged();
        EventBus.getDefault().post(new BCMapIntentService.BCAutoCompletedSearchMapEvent(mapName));
    }

    private void loadFavoriteMapFromLocal() {
        if (mCurrentUser == null) return;
        BCMapIntentService.startGetFavouriteMapsLocal(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadMapSuccess(BCMapIntentService.BCLoadFavouriteMapsSuccessEvent loadFavouriteMapsSuccessEvent) {
        if (loadFavouriteMapsSuccessEvent != null) {
            List<BCMap> mLocalList = loadFavouriteMapsSuccessEvent.data;
            maps.clear();
            maps.addAll(mLocalList);
            mMapSourceAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadMapFailed(BCMapIntentService.BCLoadMapsFailedEvent error) {
        BCAlertDialogHelper.showErrorAlert(getActivity(), null, "");
    }

    @Override
    public void onDeleteClicked(int position) {
        BCMap deleteMap = maps.get(position);
        if (deleteMap != null) {
            showConfirmDialogDeleteMap(deleteMap);
        }
    }

    private void showConfirmDialogDeleteMap(final BCMap deleteMap) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        BCDatabaseHelper.delete(deleteMap);
                        maps.remove(deleteMap);
                        BC_Helper.deleteMap(deleteMap.getId());
                        mMapSourceAdapter.notifyDataSetChanged();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        BCAlertDialogHelper.showAlertWithConfirm(getContext(), BCAlertDialogHelper.BCDialogType.DELETE_MAP, onClickListener, null, deleteMap.getMapName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeMapTab(BCMapIntentService.BCChangeMapTabEvent event) {
        currentTab = event.tabPosition;
        if (event.searchQuery.length() == 0) {
            loadFavoriteMapFromLocal();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchMap(BCMapIntentService.BCSearchMapEvent data) {
        String searchQuery = data.searchQuery;
        List<BCMap> mapResults;
        if (!TextUtils.isEmpty(searchQuery)) {
            mapResults = MapUtils.findMapByMapName(searchQuery, true, MAP_STATUS.NOT_DOWNLOAD);
            maps.clear();
            maps.addAll(mapResults);
            mMapSourceAdapter.notifyDataSetChanged();
        } else {
            loadFavoriteMapFromLocal();
        }
        if (currentTab == 1) {
            mAutoCompleteSearchAdapter = new BCAutoCompleteSearchAdapter(maps, this);
            mAutoCompleteSearchRcv.setAdapter(mAutoCompleteSearchAdapter);
            mAutoCompleteSearchRcv.setVisibility(searchQuery.length() > 0 ? View.VISIBLE : View.GONE);
            mAutoCompleteSearchAdapter.notifyDataSetChanged();
        }
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
                BCDatabaseHelper.save(pinMap);
                mMapSourceAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onUnpinClicked(int position) {
        BCMap pinMap = maps.get(position);
        pinMap.setPinned(false);
        BCDatabaseHelper.save(pinMap);
        mMapSourceAdapter.notifyItemChanged(position);
    }
}
