package com.crittermap.backcountrynavigator.xe.ui.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import com.crittermap.backcountrynavigator.xe.core.data.settings.Settings;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.others.UpdateUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.core.domain.settings.query.GetUserSettingsUseCase;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public abstract class BCBaseSettingsActivity extends BCBaseActivity {
    @Inject
    public GetUserSettingsUseCase getUserSettingsUseCase;
    @Inject
    public UpdateUserSettingsUseCase updateUserSettingsUseCase;
    protected BCSettings mSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);

        configToolbar();
        makeStatusBarNotTransparent();
    }

    protected abstract int getContentViewId();

    private void configToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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

}
