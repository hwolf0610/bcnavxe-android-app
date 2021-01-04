package com.crittermap.backcountrynavigator.xe.ui.trips;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.MenuItem;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.OnTripAdapterChanged;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripIntentService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseDaggerActivity;
import com.crittermap.backcountrynavigator.xe.ui.custom.CustomViewPager;
import com.raizlabs.android.dbflow.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged.DrawTripEvent.UNPIN;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.CONFLICTED;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.NOT_DOWNLOADED;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.NOT_UPLOADED;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.OUTDATED_REMOTE;
import static com.crittermap.backcountrynavigator.xe.share.TripStatus.OUTDATE_LOCAL;

public class BCTripsActivity extends BCBaseDaggerActivity implements BaseTripsFragment.IBaseTripFragmentListener {

    private static final String TAG = BCTripsActivity.class.getSimpleName();
    private BCUser currentUser;
    public static int SAVE_TRIP_ACTIVITY_RESULT = 1011;

    protected List<BCTripInfo> serverList = new ArrayList<>();

    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    private TripActivityViewModel activityViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        ButterKnife.bind(this);

        activityViewModel = ViewModelProviders.of(this, viewModelFactory).get(TripActivityViewModel.class);
        activityViewModel.getSettingsMutableLiveData().observe(this, new Observer<BCSettings>() {
            @Override
            public void onChanged(@Nullable BCSettings settings) {
                if (settings != null) {
                    mUserSettings = settings;
                    loadTripsFromServer();
                } else {
                    //FIXME Error handling
                    Log.e(TAG, "FIXME: cannot get settings");
                }
            }
        });
        activityViewModel.fetchUserSettings();

        configToolbar();

        makeStatusBarNotTransparent();
        currentUser = BCUtils.getCurrentUser();

        CustomViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(true);
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void configToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BCConstant.TRIP_ACTION, BCConstant.TRIP_ACTION_UPDATE_VALUE);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void loadTripsFromServer() {
        if (currentUser == null) return;
        if (isOffline()) {
            EventBus.getDefault().post(new OnTripAdapterChanged(new ArrayList<>(BCTripInfoDBHelper.loadAll().values())));
        } else {
            showProgress("Loading trips from server");
            BCTripIntentService.startGetAllTripsByUser(this, currentUser.getUserName());
        }
    }

    protected void compareLocalTrips() {
        Map<String, BCTripInfo> localTrips = BCTripInfoDBHelper.loadAll();
        List<BCTripInfo> mergeTrip = new ArrayList<>(localTrips.values());

        Set<String> serverTripsKeys = new HashSet<>();
        for (BCTripInfo tripInfo : serverList) {
            //SET OWNER ID TO MY TRIPS - FOR OLD TRIP ONLY
            if (StringUtils.isNullOrEmpty(tripInfo.getOwnerId())) {
                tripInfo.setOwnerId(BCUtils.getCurrentUser().getUserName());
            }

            if (localTrips.containsKey(tripInfo.getId())) {
                //SET OWNER ID TO MY TRIPS - FOR OLD TRIP ONLY
                BCTripInfo localTrip = localTrips.get(tripInfo.getId());
                if (StringUtils.isNullOrEmpty(localTrip.getOwnerId())) {
                    localTrips.get(tripInfo.getId()).setOwnerId(BCUtils.getCurrentUser().getUserName());
                }
                synchronizeTrip(localTrips.get(tripInfo.getId()), tripInfo);
            } else {
                BCTripInfoDBHelper.save(tripInfo);
                mergeTrip.add(tripInfo);
            }
            serverTripsKeys.add(tripInfo.getId());
        }

        for (BCTripInfo tripInfo : localTrips.values()) {
            if (!serverTripsKeys.contains(tripInfo.getId()) && tripInfo.getTripStatus() != NOT_UPLOADED) {
                mergeTrip.remove(tripInfo);
                BCTripInfoDBHelper.delete(tripInfo);
                BC_TripsDBHelper.createInstance(tripInfo.getId()).dropDatabse();

                if (tripInfo.isPinned() && tripInfo.isShowedChecked()) {
                    EventBus.getDefault().postSticky(new BCEventPinTripChanged(tripInfo, UNPIN));
                }
            }
        }

        EventBus.getDefault().post(new OnTripAdapterChanged(mergeTrip));
    }

    protected void synchronizeTrip(BCTripInfo localTrip, BCTripInfo serverTrip) {

        if (localTrip.getTripStatus() == NOT_DOWNLOADED) {
            localTrip.setLastSync(Calendar.getInstance().getTimeInMillis());
            localTrip.setName(serverTrip.getName());
            BCTripInfoDBHelper.update(localTrip);
            return;
        }

        if (isServerChanged(localTrip, serverTrip)) {
            if (isLocalChanged(localTrip)) {
                localTrip.setTripStatus(CONFLICTED);
            } else {
                localTrip.setTimestamp(serverTrip.getTimestamp());
                localTrip.setTripStatus(OUTDATE_LOCAL);
            }
        } else if (isLocalChanged(localTrip)) {
            localTrip.setTripStatus(OUTDATED_REMOTE);
        }
    }

    protected boolean isLocalChanged(BCTripInfo localTrip) {
        return localTrip.getTimestamp() > localTrip.getLastSync();
    }

    protected boolean isServerChanged(BCTripInfo localTrip, BCTripInfo serverTrip) {
        return localTrip.getLastSync() < serverTrip.getTimestamp();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadTripSuccess(BCTripIntentService.BCLoadTripsSuccessEvent loadTripsSuccessEvent) {
        dismissProgress();
        if (loadTripsSuccessEvent != null) {
            serverList = loadTripsSuccessEvent.data;
            compareLocalTrips();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadTripFailed(BCTripIntentService.BCLoadTripsFailedEvent error) {
        dismissProgress();
        BCAlertDialogHelper.showErrorAlert(this, null, "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onReload() {
        compareLocalTrips();
    }

    class MainViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList.add(new MyTripsFragmentBase());
            mFragmentList.add(new DownloadedTripsFragment());

            mFragmentTitleList.add("My trips");
            mFragmentTitleList.add("Shared with me");
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
