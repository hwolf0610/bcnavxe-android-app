package com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess.impl.InteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.settings.quickaccess.impl.PresenterImpl;

public class BCSettingsQuickAccessActivity extends BCBaseSettingsActivity
        implements BCSettingsQuickAccessActivityContracts.View,
        CompoundButton.OnCheckedChangeListener {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_quick_access;
    }

    private BCSettingsQuickAccessActivityContracts.Presenter presenter;
    private BCSettings bcSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new PresenterImpl(this, new InteractorImpl());
        initializeData();
    }

    @Override
    public void onChangeSettingsSuccess() {
        Toast.makeText(this, R.string.change_settings_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeSettingsFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetSettingsFromRepositorySuccess(BCSettings settings) {
        bcSettings = settings;
        initializeViews();
        initializeEvents();
    }

    @Override
    public void onGetSettingsFromRepositoryFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.switch_fullscreen_mode:
                bcSettings = onSwitchSettingChanged(bcSettings, BCSettings.QUICK_ACCESS_OPTIONS.FULL_SCREEN_MODE, isChecked);
                break;
            case R.id.switch_scale_bar:
                bcSettings = onSwitchSettingChanged(bcSettings, BCSettings.QUICK_ACCESS_OPTIONS.SCALE_BAR_NUMBER, isChecked);
                break;
            case R.id.switch_statistic:
                bcSettings = onSwitchSettingChanged(bcSettings, BCSettings.QUICK_ACCESS_OPTIONS.STATS, isChecked);
                break;
            case R.id.switch_zoom:
                bcSettings = onSwitchSettingChanged(bcSettings, BCSettings.QUICK_ACCESS_OPTIONS.ZOOM, isChecked);
                break;
            case R.id.switch_map_sources:
                bcSettings = onSwitchSettingChanged(bcSettings, BCSettings.QUICK_ACCESS_OPTIONS.MAP_SOURCES, isChecked);
                break;
        }
        presenter.onSwitchSettingChanged(bcSettings);
    }

    private BCSettings onSwitchSettingChanged(BCSettings settings, BCSettings.QUICK_ACCESS_OPTIONS quickAccessOptions, boolean value) {
        switch (quickAccessOptions) {
            case FULL_SCREEN_MODE:
                settings.setShowFullScreenMode(value);
                break;
            case ZOOM:
                settings.setShowZoom(value);
                break;
            case MAP_SOURCES:
                settings.setShowMapSources(value);
                break;
            case STATS:
                settings.setShowStats(value);
                break;
            case SCALE_BAR_NUMBER:
                settings.setShowZoomNumber(value);
                break;
        }
        return settings;
    }

    @Override
    public void initializeData() {
        presenter.onInitializeData();
    }

    @Override
    public void initializeViews() {
        ((SwitchCompat) findViewById(R.id.switch_scale_bar)).setChecked(bcSettings.isShowZoomNumber());
        ((SwitchCompat) findViewById(R.id.switch_fullscreen_mode)).setChecked(bcSettings.isShowFullScreenMode());
        ((SwitchCompat) findViewById(R.id.switch_zoom)).setChecked(bcSettings.isShowZoom());
        ((SwitchCompat) findViewById(R.id.switch_statistic)).setChecked(bcSettings.isShowStats());
        ((SwitchCompat) findViewById(R.id.switch_map_sources)).setChecked(bcSettings.isShowMapSources());
    }

    @Override
    public void initializeEvents() {
        ((SwitchCompat) findViewById(R.id.switch_scale_bar)).setOnCheckedChangeListener(this);
        ((SwitchCompat) findViewById(R.id.switch_fullscreen_mode)).setOnCheckedChangeListener(this);
        ((SwitchCompat) findViewById(R.id.switch_statistic)).setOnCheckedChangeListener(this);
        ((SwitchCompat) findViewById(R.id.switch_map_sources)).setOnCheckedChangeListener(this);
        ((SwitchCompat) findViewById(R.id.switch_zoom)).setOnCheckedChangeListener(this);
    }
}
