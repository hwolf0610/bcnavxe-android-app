package com.crittermap.backcountrynavigator.xe.ui.selecttrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.saveTrip.BCSaveTripActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.crittermap.backcountrynavigator.xe.share.BCConstant.INTENT_EXTRA_TRIP_ID;

public class BCSelectTripActivity extends BCBaseActivity {

    public static final int REQUEST_CODE_NEW_TRIP = 100;
    public static final String KEY_SELECT_TRIP = "isFromSelectTrip";
    @BindView(R.id.tv_folder_name)
    TextView tvFolderName;

    @BindView(R.id.folderChevron)
    ImageView folderChevron;

    @BindView(R.id.tv_no_trips)
    TextView tvNoTrips;

    private Map<String, List<BCTripInfo>> folderTrips = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_trip);
        ButterKnife.bind(this);

        configToolbar();
        makeStatusBarNotTransparent();

        ButterKnife.bind(this);

        hideFolderText();
        if (BCUtils.getCurrentUser() != null) {
            folderTrips = BCTripInfoDBHelper.getLocalTrips(BCUtils.getCurrentUser().getUserName());
        }
        if (folderTrips.size() > 0) {
            showSelectFolderFragment();
            tvNoTrips.setVisibility(View.GONE);
        } else {
            tvNoTrips.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_trip_menu, menu);
        return true;
    }

    private void showFolderText(String folder) {
        tvFolderName.setVisibility(View.VISIBLE);
        tvFolderName.setText(folder);
        folderChevron.setVisibility(View.VISIBLE);
    }

    private void hideFolderText() {
        tvFolderName.setVisibility(View.GONE);
        folderChevron.setVisibility(View.GONE);
    }

    private void showSelectFolderFragment() {
        SelectTripFolderFragment folderFragment = new SelectTripFolderFragment();
        folderFragment.setFolders(new ArrayList<>(folderTrips.keySet()));
        folderFragment.setOnItemClickListener(new OnItemClickedListener<String>() {
            @Override
            public void onClick(String folder) {
                showSelectTripsFragment(folder);
            }
        });
        showFragment(folderFragment);
    }

    private void showSelectTripsFragment(String folder) {
        showFolderText(folder);

        SelectTripFragment tripFragment = new SelectTripFragment();
        tripFragment.setTrips(folderTrips.get(folder));
        tripFragment.setOnItemClickListener(new OnItemClickedListener<BCTripInfo>() {
            @Override
            public void onClick(BCTripInfo tripInfo) {
                onSelectedTrip(tripInfo);
            }
        });
        showFragment(tripFragment);
    }

    private void onSelectedTrip(final BCTripInfo tripInfo) {
        TripUtils.actionPinTrip(tripInfo, BCSelectTripActivity.this, new BCSaveTripActivity.PinTripCallback() {
            @Override
            public void onSave() {
                Intent intent = new Intent();
                intent.putExtra(INTENT_EXTRA_TRIP_ID, tripInfo.getId());
                BCSelectTripActivity.this.setResult(RESULT_OK, intent);
                BCSelectTripActivity.this.finish();
            }
        });
    }


    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
        if (fragmentManager.findFragmentById(R.id.container) == null) {
            ft.add(R.id.container, fragment);
        } else {
            ft.replace(R.id.container, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    private void configToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            hideFolderText();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_addition:
                openCreateNewTripActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_TRIP && resultCode == RESULT_OK) {
            String tripId = data.getStringExtra("tripId");
            BCTripInfo tripInfo = BCTripInfoDBHelper.get(tripId);
            onSelectedTrip(tripInfo);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openCreateNewTripActivity() {
        Intent intent = new Intent(this, BCSaveTripActivity.class);
        intent.putExtra(KEY_SELECT_TRIP, true);
        startActivityForResult(intent, REQUEST_CODE_NEW_TRIP);
    }

    public interface OnItemClickedListener<T> {
        void onClick(T data);
    }
}
