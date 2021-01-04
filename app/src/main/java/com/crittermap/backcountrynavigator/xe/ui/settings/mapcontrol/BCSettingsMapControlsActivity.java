package com.crittermap.backcountrynavigator.xe.ui.settings.mapcontrol;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.mapcontrol.impl.InteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.settings.mapcontrol.impl.PresenterImpl;

import butterknife.BindView;

public class BCSettingsMapControlsActivity extends BCBaseSettingsActivity implements BCSettingsMapControlsActivityContracts.View {
    @BindView(R.id.radio_group_compass_stype)
    protected RadioGroup radioGroupCompassStyle;

    @BindView(R.id.radio_group_compass_type)
    protected RadioGroup radioGroupCompassType;

    @BindView(R.id.switch_allow_rotation)
    protected SwitchCompat switchCompatAllowRotation;

    private BCSettingsMapControlsActivityContracts.Presenter presenter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_map_control;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeData();
        initializeViews();
    }

    private void renderSettings(BCSettings settings) {
        safeClearEventListeners();

        switchCompatAllowRotation.setChecked(settings.isAllowMapRotation());
        radioGroupCompassStyle.check(settings.getCompassStyle().ordinal());
        radioGroupCompassType.check(settings.getCompassSensorType().ordinal());

        initializeEvents();
        mSettings = settings;
    }

    private void initRadioGroup() {
        for (int i = 0; i < BCSettings.COMPASS_STYLE.values().length; i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_template, null);
            radioButton.setText(BCSettings.COMPASS_STYLE.values()[i].valueResId);
            radioButton.setId(i);
            radioGroupCompassStyle.addView(radioButton);
        }

        for (int i = 0; i < BCSettings.COMPASS_SENSOR_TYPE.values().length; i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_template, null);
            radioButton.setText(BCSettings.COMPASS_SENSOR_TYPE.values()[i].valueResId);
            radioButton.setId(i);
            radioGroupCompassType.addView(radioButton);
        }
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
        if (settings != null) {
            renderSettings(settings);
        }
    }

    @Override
    public void onGetSettingsFromRepositoryFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initializeData() {
        presenter = new PresenterImpl(this, new InteractorImpl());
        presenter.onInitializeData();
    }

    @Override
    public void initializeViews() {
        initRadioGroup();
    }

    @Override
    public void initializeEvents() {
        switchCompatAllowRotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettings.setAllowMapRotation(isChecked);
                presenter.onSettingsChanged(mSettings);
            }
        });
        radioGroupCompassStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mSettings.setCompassStyle(BCSettings.COMPASS_STYLE.values()[checkedId]);
                presenter.onSettingsChanged(mSettings);
            }
        });
        radioGroupCompassType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mSettings.setCompassSensorType(BCSettings.COMPASS_SENSOR_TYPE.values()[checkedId]);
                presenter.onSettingsChanged(mSettings);
            }
        });
    }

    private void safeClearEventListeners() {
        switchCompatAllowRotation.setOnCheckedChangeListener(null);
        radioGroupCompassStyle.setOnCheckedChangeListener(null);
        radioGroupCompassType.setOnCheckedChangeListener(null);
    }
}
