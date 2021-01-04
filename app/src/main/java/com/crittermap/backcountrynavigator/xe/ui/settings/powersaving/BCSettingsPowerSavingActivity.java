package com.crittermap.backcountrynavigator.xe.ui.settings.powersaving;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.powersaving.impl.InteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.settings.powersaving.impl.PresenterImpl;

import butterknife.BindView;

public class BCSettingsPowerSavingActivity extends BCBaseSettingsActivity implements BCSettingsPowerSavingActivityContracts.View {
    private static final String TAG = BCSettingsPowerSavingActivity.class.getSimpleName();

    @BindView(R.id.btn_save_sample_rate)
    Button btnSaveGPSSampleRate;
    @BindView(R.id.et_gps_sample_rate)
    EditText etGPSSampleRate;

    private BCSettings mSettings;
    private PresenterImpl presenter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_power_saving;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeData();
        initializeViews();
    }

    private void renderSettings(BCSettings settings) {
        btnSaveGPSSampleRate.setOnClickListener(null);

        etGPSSampleRate.setText(String.valueOf(settings.getGpsSampleRate()));

        initializeEvents();

        mSettings = settings;
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
        Log.v(TAG, "Nothing to init");
    }

    @Override
    public void initializeEvents() {
        btnSaveGPSSampleRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gpsSampleRate = getGPSSampleRateFromEditText();
                mSettings.setGpsSampleRate(gpsSampleRate);
                presenter.onSettingsChanged(mSettings);
            }
        });
    }

    private int getGPSSampleRateFromEditText() {
        int gpsSampleRate;
        try {
            gpsSampleRate = Integer.parseInt(etGPSSampleRate.getText().toString());
            if (gpsSampleRate < 1) gpsSampleRate = 1;
        } catch (Exception e) {
            gpsSampleRate = 1;
        }
        return gpsSampleRate;
    }

    @Override
    public void lockSaveGPSButton() {
        btnSaveGPSSampleRate.setText(R.string.saving);
        btnSaveGPSSampleRate.setEnabled(false);
    }

    @Override
    public void unlockSaveGPSButton() {
        btnSaveGPSSampleRate.setText(R.string.save);
        btnSaveGPSSampleRate.setEnabled(true);
    }
}
