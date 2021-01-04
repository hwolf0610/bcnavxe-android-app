package com.crittermap.backcountrynavigator.xe.ui.mapSource;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapIntentService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.fragments.BCDownloadedMapFragment;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.fragments.BCFavoriteMapFragment;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.fragments.BCSuggestMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by henry on 3/25/2018.
 */

public class BCMapSourceActivity extends BCBaseActivity implements View.OnClickListener {
    private static final String TAG = BCMapSourceActivity.class.getSimpleName();
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.tabs)
    public TabLayout tabLayout;
    @BindView(R.id.viewpager)
    public ViewPager viewPager;
    @BindView(R.id.searchView)
    public SearchView searchView;
    @BindView(R.id.auto_complete_searchRcv)
    public RecyclerView autoCompleteSearchRcv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_source);
        ButterKnife.bind(this);

        configToolbar();

        makeStatusBarNotTransparent();

        setupViewPager(viewPager);

        initSearchView();

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                EventBus.getDefault().post(new BCMapIntentService.BCChangeMapTabEvent(searchView.getQuery(), position));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initSearchView() {
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                EventBus.getDefault().post(new BCMapIntentService.BCSearchMapEvent(query));
                return false;
            }
        });
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.ic_go_back_left_arrow);
        mToolBar.setNavigationOnClickListener(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void updateMapSources() {
        final BCUser user = BCUtils.getCurrentUser();
        if (user == null) return;

        if (BCUtils.isNetworkAvailable(this)) {
            showProgress("Loading map");
            WebServiceCallBack<ArrayList<String>> webServiceCallBack = new WebServiceCallBack<ArrayList<String>>() {
                @Override
                public void onSuccess(ArrayList<String> data) {
                    user.setFavoriteBasemap(data);
                    BCUtils.saveUserShareRef(user);
                    BCMapIntentService.startGetAllMapsByUser(BCMapSourceActivity.this, user.getUserName());
                }

                @Override
                public void onFailed(String errorMessage) {
                    dismissProgress();
                    BCAlertDialogHelper.showErrorAlert(BCMapSourceActivity.this, BCErrorType.NETWORK_ERROR, errorMessage);
                }
            };
            BCApiService.getInstance().doGetUserFavoriteMap(user.getUserName(), webServiceCallBack);
        } else {
            BCMapIntentService.startGetAllMapsLocal(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadMapSuccess(BCMapIntentService.BCLoadMapsSuccessEvent loadMapsSuccessEvent) {
        dismissProgress();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadMapFailed(BCMapIntentService.BCLoadMapsFailedEvent error) {
        dismissProgress();
        Logger.e(TAG, error.toString());
        Toast.makeText(this, "Error on loading map sources!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BCConstant.MAP_ACTION, BCConstant.MAP_ACTION_UPDATE_VALUE);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        updateMapSources();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    public void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BCSuggestMapFragment(autoCompleteSearchRcv), getString(R.string.suggestion_map));
        adapter.addFragment(new BCFavoriteMapFragment(autoCompleteSearchRcv), getString(R.string.favourite_map));
        adapter.addFragment(new BCDownloadedMapFragment(autoCompleteSearchRcv), getString(R.string.downloaded_map));
        viewPager.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAutoCompleteSearchSelected(BCMapIntentService.BCAutoCompletedSearchMapEvent event) {
        searchView.setQuery(event.mapName, false);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
