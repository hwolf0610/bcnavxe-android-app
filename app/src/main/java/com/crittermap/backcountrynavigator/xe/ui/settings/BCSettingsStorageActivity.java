package com.crittermap.backcountrynavigator.xe.ui.settings;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;

import butterknife.BindView;

public class BCSettingsStorageActivity extends BCBaseSettingsActivity {
    @BindView(R.id.radio_group_data_storage)
    protected RadioGroup radioGroupDataStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataStorageOptions();
    }

    private void initDataStorageOptions() {
        for (int i = 0; i < BCSettings.DATA_STORAGE.values().length; i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_template, null);
            radioButton.setText(BCSettings.DATA_STORAGE.values()[i].valueResId);
            radioButton.setId(i);
            radioGroupDataStorage.addView(radioButton);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_storage;
    }

    //TODO use item_storage_info.xml for adapter
}
