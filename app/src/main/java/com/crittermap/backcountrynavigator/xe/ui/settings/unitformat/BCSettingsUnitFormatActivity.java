package com.crittermap.backcountrynavigator.xe.ui.settings.unitformat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.unitformat.impl.InteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.settings.unitformat.impl.PresenterImpl;

import butterknife.BindView;

public class BCSettingsUnitFormatActivity extends BCBaseSettingsActivity implements BCSettingsUnitFormatActivityContracts.View {
    @BindView(R.id.radio_group_area)
    protected RadioGroup radioGroupArea;

    @BindView(R.id.radio_group_distance)
    protected RadioGroup radioGroupDistance;

    @BindView(R.id.radio_group_coordinates)
    protected RadioGroup radioGroupCoordinates;

    private BCSettingsUnitFormatActivityContracts.Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeData();
        initializeViews();
        initializeEvents();
    }

    private void renderSettings(BCSettings settings) {
        safeClearEventListenersOnRadioButtonGroups();

        setRadioButtonGroupBySettings(radioGroupArea, settings.getArea().ordinal());
        setRadioButtonGroupBySettings(radioGroupCoordinates, settings.getCoordinates().ordinal());
        setRadioButtonGroupBySettings(radioGroupDistance, settings.getDistance().ordinal());

        initializeEvents();

        mSettings = settings;
    }

    @NonNull
    private RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                BCSettings settings = bindDataToSettingsBaseOnRadioGroupChanged(group, checkedId, mSettings);
                presenter.onSwitchSettingChanged(settings);
            }
        };
    }

    private BCSettings bindDataToSettingsBaseOnRadioGroupChanged(RadioGroup group, int checkedId, BCSettings settings) {
        if (settings == null) {
            settings = new BCSettings();
        }

        if (group.equals(radioGroupArea)) {
            settings.setArea(BCSettings.AREA.values()[checkedId]);
        } else if (group.equals(radioGroupCoordinates)) {
            settings.setCoordinates(BCSettings.COORDINATES.values()[checkedId]);
        } else if (group.equals(radioGroupDistance)) {
            settings.setDistance(BCSettings.DISTANCE.values()[checkedId]);
        }

        return settings;
    }

    private void setRadioButtonGroupBySettings(RadioGroup radioGroupArea, int id) {
        radioGroupArea.check(id);
    }

    private void safeClearEventListenersOnRadioButtonGroups() {
        radioGroupArea.setOnCheckedChangeListener(null);
        radioGroupCoordinates.setOnCheckedChangeListener(null);
        radioGroupDistance.setOnCheckedChangeListener(null);
    }

    private void initAreaRadioGroup() {
        for (int i = 0; i < BCSettings.AREA.values().length; i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_template, null);
            radioButton.setText(BCSettings.AREA.values()[i].valueResId);
            radioButton.setId(i);
            radioGroupArea.addView(radioButton);
        }
    }

    private void initDistanceRadioGroup() {
        for (int i = 0; i < BCSettings.DISTANCE.values().length; i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_template, null);
            radioButton.setText(BCSettings.DISTANCE.values()[i].valueResId);
            radioButton.setId(i);
            radioGroupDistance.addView(radioButton);
        }
    }

    private void initCoordinatesRadioGroup() {
        for (int i = 0; i < BCSettings.COORDINATES.values().length; i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_template, null);
            radioButton.setText(BCSettings.COORDINATES.values()[i].valueResId);
            radioButton.setId(i);
            radioGroupCoordinates.addView(radioButton);
        }
    }

    @Override
    public void initializeData() {
        presenter = new PresenterImpl(this, new InteractorImpl());
        presenter.onInitializeData();
    }

    @Override
    public void initializeViews() {
        initAreaRadioGroup();
        initDistanceRadioGroup();
        initCoordinatesRadioGroup();
    }

    @Override
    public void initializeEvents() {
        RadioGroup.OnCheckedChangeListener listener = getOnCheckedChangeListener();
        radioGroupArea.setOnCheckedChangeListener(listener);
        radioGroupCoordinates.setOnCheckedChangeListener(listener);
        radioGroupDistance.setOnCheckedChangeListener(listener);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_unit_format;
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
        if (settings != null) renderSettings(settings);
    }

    @Override
    public void onGetSettingsFromRepositoryFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
