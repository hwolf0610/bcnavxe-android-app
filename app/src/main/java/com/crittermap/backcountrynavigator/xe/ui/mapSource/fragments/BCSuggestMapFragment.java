package com.crittermap.backcountrynavigator.xe.ui.mapSource.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.crashlytics.android.Crashlytics;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.geocoder.BCGeocoderAPIService;
import com.crittermap.backcountrynavigator.xe.service.geocoder.BCGeocoderData;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapIntentService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter.BCAutoCompleteSearchAdapter;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter.BCMapSourceAdapter;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.crittermap.backcountrynavigator.xe.share.BCConstant.AVIATION_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.HYBRID_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.MARINE_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.SATELLITE_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.STREET_FILTER;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.TOPO_FILTER;

public class BCSuggestMapFragment extends BaseMapFragment implements BCAutoCompleteSearchAdapter.OnItemClicked {
    @BindView(R.id.cb_topo)
    CheckBox topoCb;
    @BindView(R.id.cb_marine)
    CheckBox marineCb;
    @BindView(R.id.cb_hybrid)
    CheckBox hybridCb;
    @BindView(R.id.cb_satellite)
    CheckBox satelliteCb;
    @BindView(R.id.cb_street)
    CheckBox streetCb;
    @BindView(R.id.cb_aviation)
    CheckBox aviationCb;

    private List<ListCheckBox> mListCheckBox = new ArrayList<>();
    private BCAutoCompleteSearchAdapter mAutoCompleteSearchAdapter;
    private RecyclerView mAutoCompleteSearchRcv;
    private int currentTab = 0;

    public BCSuggestMapFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public BCSuggestMapFragment(RecyclerView autoCompleteSearchRcv) {
        super();
        mAutoCompleteSearchRcv = autoCompleteSearchRcv;
    }

    public static BCSuggestMapFragment newInstance() {
        return new BCSuggestMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        View view = inflater.inflate(R.layout.fragment_suggestion_map, container, false);
        ButterKnife.bind(this, view);
        mMapSourceAdapter = new BCMapSourceAdapter(maps, getContext(), this);
        mMapSourceRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMapSourceRcv.setAdapter(mMapSourceAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mMapSourceRcv.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mMapSourceRcv.addItemDecoration(dividerItemDecoration);
        initCheckBox();
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

    private void loadSuggestionMapFromLocal() {
        maps.clear();
        maps.addAll(MapUtils.getAllMaps());
        try {
            sortMapSource();
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            mMapSourceAdapter.sortMapData();
            mMapSourceAdapter.notifyDataSetChanged();
        }
    }

    private void sortMapSource() {
        String pointJSON = getActivity().getIntent().getStringExtra(BCHomeActivity.KEY_CENTER_POINT);
        Point centerPoint =  (Point) Geometry.fromJson(pointJSON);
        double longitude = centerPoint.getX();
        double latitude = centerPoint.getY();

        BCGeocoderAPIService.getInstance().reverseGeocoding(latitude, longitude, new WebServiceCallBack<BCGeocoderData>() {
            @Override
            public void onSuccess(BCGeocoderData data) {
                mMapSourceAdapter.setLocale(data.getCountryCode(), data.getStateCode());
                mMapSourceAdapter.sortMapData();
                mMapSourceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String errorMessage) {
                mMapSourceAdapter.sortMapData();
                mMapSourceAdapter.notifyDataSetChanged();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadMapSuccess(BCMapIntentService.BCLoadMapsSuccessEvent loadMapsSuccessEvent) {
        if (loadMapsSuccessEvent != null) {
            loadSuggestionMapFromLocal();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadMapFailed(BCMapIntentService.BCLoadMapsFailedEvent error) {
        loadSuggestionMapFromLocal();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeMapTab(BCMapIntentService.BCChangeMapTabEvent event) {
        currentTab = event.tabPosition;
        if (event.searchQuery.length() == 0) {
            loadSuggestionMapFromLocal();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchMap(BCMapIntentService.BCSearchMapEvent data) {
        String searchQuery = data.searchQuery;
        storeSearchQuery = searchQuery;
        mListCheckBox.clear();
        mListCheckBox.add(new ListCheckBox(TOPO_FILTER, topoCb != null && topoCb.isChecked()));
        mListCheckBox.add(new ListCheckBox(MARINE_FILTER, marineCb != null && marineCb.isChecked()));
        mListCheckBox.add(new ListCheckBox(HYBRID_FILTER, hybridCb != null && hybridCb.isChecked()));
        mListCheckBox.add(new ListCheckBox(SATELLITE_FILTER, satelliteCb != null && satelliteCb.isChecked()));
        mListCheckBox.add(new ListCheckBox(STREET_FILTER, streetCb != null && streetCb.isChecked()));
        mListCheckBox.add(new ListCheckBox(AVIATION_FILTER, aviationCb != null && aviationCb.isChecked()));

        List<BCMap> mapResults;
        mapResults = MapUtils.findMapByMapNameAndFilter(searchQuery, mListCheckBox);
        maps.clear();
        maps.addAll(mapResults);
        mMapSourceAdapter.sortMapData();
        mMapSourceAdapter.notifyDataSetChanged();
        mAutoCompleteSearchAdapter.notifyDataSetChanged();
        if (currentTab == 0) {
            mAutoCompleteSearchAdapter = new BCAutoCompleteSearchAdapter(maps, this);
            mAutoCompleteSearchRcv.setAdapter(mAutoCompleteSearchAdapter);
            mAutoCompleteSearchRcv.setVisibility(!TextUtils.isEmpty(searchQuery) ? View.VISIBLE : View.GONE);
            mAutoCompleteSearchAdapter.notifyDataSetChanged();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class ListCheckBox {
        String checkBoxName;
        boolean isChecked;

        public ListCheckBox(String checkBoxName, boolean isChecked) {
            this.checkBoxName = checkBoxName;
            this.isChecked = isChecked;
        }

        public String getCheckBoxName() {
            return checkBoxName;
        }

        public void setCheckBoxName(String checkBoxName) {
            this.checkBoxName = checkBoxName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    private void initCheckBox() {

        topoCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(storeSearchQuery));
            }
        });
        marineCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(storeSearchQuery));
            }
        });
        hybridCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(storeSearchQuery));
            }
        });
        satelliteCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(storeSearchQuery));
            }
        });
        streetCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(storeSearchQuery));
            }
        });
        aviationCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(storeSearchQuery));
            }
        });
    }
}
