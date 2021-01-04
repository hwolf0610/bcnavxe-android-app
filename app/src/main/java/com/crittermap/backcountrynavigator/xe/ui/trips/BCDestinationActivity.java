package com.crittermap.backcountrynavigator.xe.ui.trips;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.trips.adapter.BCDestinationAdapter;
import com.crittermap.backcountrynavigator.xe.ui.trips.adapter.BCTripsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by henry on 4/16/2018.
 */

public class BCDestinationActivity extends BCBaseActivity implements View.OnClickListener {

    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.create_folder_btn)
    Button mCreateFolder;
    @BindView(R.id.create_trip_btn)
    Button mCreateTrip;
    @BindView(R.id.list_rcv)
    RecyclerView mListRcv;

    private BCDestinationAdapter mDestinationAdapter;
    private List<BCTripInfo> mDestinationList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        ButterKnife.bind(this);
        configToolbar();
        makeStatusBarNotTransparent();

        // TODO: 4/16/2018 Replace this code with trips loaded from database
        mDestinationList = new ArrayList<>();

        BCTripInfo info1 = new BCTripInfo();
        info1.setId("1");
        info1.setName("My trips");
        BCTripInfo info2 = new BCTripInfo();
        info2.setId("2");
        info2.setName("Downloaded");
        BCTripInfo info3 = new BCTripInfo();
        info3.setId("3");
        info3.setName("Downloaded Trip");
        mDestinationList.add(info1);
        mDestinationList.add(info2);
        mDestinationList.add(info3);

        mDestinationAdapter = new BCDestinationAdapter(mDestinationList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mListRcv.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mListRcv.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mListRcv.addItemDecoration(dividerItemDecoration);
        mListRcv.setItemAnimator(new DefaultItemAnimator());
        mListRcv.setAdapter(mDestinationAdapter);
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.ic_go_back_left_arrow);
        mToolBar.setTitle(R.string.select_destination);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolBar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.create_folder_btn)
    public void onCreateFolderButtonClicked() {

    }

    @OnClick(R.id.create_trip_btn)
    public void onCreateTripButtonClicked() {

    }

    public void goToTrip(BCTripInfo trip) {
        Toast.makeText(this, "Destination selected: " + trip.getName(), Toast.LENGTH_LONG).show();
    }
}
