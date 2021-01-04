package com.crittermap.backcountrynavigator.xe.ui.settings.appearance;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCAlertType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCSettingAppearanceActivityContracts;
import com.crittermap.backcountrynavigator.xe.ui.settings.appearance.impl.InteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.settings.appearance.impl.PresenterImpl;

import butterknife.BindView;

public class BCSettingsAppearanceActivity extends BCBaseSettingsActivity implements BCSettingAppearanceActivityContracts.View {

    @BindView(R.id.radio_group_theme)
    RadioGroup groupTheme;

    /**
     * presenter used for handling bussiness logics
     */
    private BCSettingAppearanceActivityContracts.Presenter presenter;

    //region activitiy methods
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initializeData();
        initializeViews();
        initializeEvents();
    }
    //endregion

    /**
     * adding radio button into radio group
     */
    private void createRadioButton() {
        for (int i = 0; i < BCSettings.THEME.values().length; i++) {
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_template, null);
            radioButton.setText(BCSettings.THEME.values()[i].valueResId);
            radioButton.setId(i);
            groupTheme.addView(radioButton);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_appearance;
    }

    /**
     * final trigger point indicating operation has done
     */
    @Override
    public void onChangeSettingsSuccess() {
        Toast.makeText(this, R.string.change_settings_success, Toast.LENGTH_SHORT).show();
        BCAlertDialogHelper.showAlertDialogBuider(this, BCAlertType.SHOW_RESTART_APP, "please restart app to apply theme", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BCUtils.goToHome(BCSettingsAppearanceActivity.this);
            }
        }, null);
    }

    @Override
    public void onChangeSettingsFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //region load data from db and set accordingly
    @Override
    public void onGetSettingsFromRepositorySuccess(BCSettings settings) {
        if (settings != null) renderSettings(settings);
    }
    //endregion

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
        createRadioButton();
    }

    @Override
    public void initializeEvents() {
        RadioGroup.OnCheckedChangeListener listener = getOnCheckedChangeListener();
        groupTheme.setOnCheckedChangeListener(listener);
    }

    //region add check change listener
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
    //endregion

    private BCSettings bindDataToSettingsBaseOnRadioGroupChanged(RadioGroup group, int checkedId, BCSettings settings) {
        if (settings == null) {
            settings = new BCSettings();
        }

        settings.setTheme(BCSettings.THEME.values()[checkedId]);

        return settings;
    }

    private void renderSettings(BCSettings settings) {
        safeClearEventListenersOnRadioButtonGroups();
        setRadioButtonGroupBySettings(groupTheme, settings.getTheme().ordinal());

        initializeEvents();

        mSettings = settings;
    }

    private void safeClearEventListenersOnRadioButtonGroups() {
        groupTheme.setOnCheckedChangeListener(null);
    }

    private void setRadioButtonGroupBySettings(RadioGroup radioGroupArea, int id) {
        radioGroupArea.check(id);

    }

}
