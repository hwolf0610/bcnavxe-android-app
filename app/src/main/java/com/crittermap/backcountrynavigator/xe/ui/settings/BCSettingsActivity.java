package com.crittermap.backcountrynavigator.xe.ui.settings;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.crittermap.backcountrynavigator.xe.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BCSettingsActivity extends BCBaseSettingsActivity {
    @BindView(R.id.recycler_view_settings)
    protected RecyclerView recyclerViewSettings;

    private BCSettingsAdapter adapter;
    private List<BCSettingsViewModel> settingsViewModels = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsViewModels = initSettingsItem();
        adapter = new BCSettingsAdapter(settingsViewModels, this);

        recyclerViewSettings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSettings.setAdapter(adapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings;
    }

    private List<BCSettingsViewModel> initSettingsItem() {
        List<BCSettingsViewModel> settingsViewModels = new ArrayList<>();
        settingsViewModels.add(new BCSettingsViewModel(R.string.settings_quick_access_title, getString(R.string.settings_quick_access_subtitle)));
        //       settingsViewModels.add(new BCSettingsViewModel(R.string.settings_storage_title));
        settingsViewModels.add(new BCSettingsViewModel(R.string.settings_power_saving_title));
        //TODO remove Maps download screen until have these features
//        settingsViewModels.add(new BCSettingsViewModel(R.string.settings_map_download_title, getString(R.string.settings_map_download_subtitle)));
        settingsViewModels.add(new BCSettingsViewModel(R.string.settings_map_controls_title, getString(R.string.settings_map_controls_subtitle)));
        //      settingsViewModels.add(new BCSettingsViewModel(R.string.settings_appearance_title, getString(R.string.settings_appearance_subtitle)));
        settingsViewModels.add(new BCSettingsViewModel(R.string.settings_unit_format_title));
//        settingsViewModels.add(new BCSettingsViewModel(R.string.settings_backup_title));
        return settingsViewModels;
    }

}
