package com.crittermap.backcountrynavigator.xe.ui.settings.backup;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCBaseSettingsActivity;

import butterknife.BindView;

public class BCSettingsBackupActivity extends BCBaseSettingsActivity {

    @BindView(R.id.ll_backup_options)
    LinearLayout llBackupOption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBackupCheckBox();
    }

    private void initBackupCheckBox() {
        for (int i = 0; i < BCSettings.BACKUP_OPTION.values().length; i++) {
            CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.check_box_template, null);
            checkBox.setText(BCSettings.BACKUP_OPTION.values()[i].valueResId);
            checkBox.setId(i);
            llBackupOption.addView(checkBox);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_backup;
    }
}
