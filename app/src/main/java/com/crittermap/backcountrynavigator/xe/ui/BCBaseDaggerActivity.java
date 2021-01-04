package com.crittermap.backcountrynavigator.xe.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.core.data.settings.Settings;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.settings.base.BaseActivityContracts;
import com.crittermap.backcountrynavigator.xe.ui.settings.base.InteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.settings.base.PresenterImpl;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import dagger.android.support.DaggerAppCompatActivity;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

/**
 * Created by nhat@saveondev.com on 12/20/17.
 */

public class BCBaseDaggerActivity extends DaggerAppCompatActivity{

    private BaseActivityContracts.Presenter presenter;

    protected BCUser currentUser;
    private ProgressDialog mProgressDialog;
    protected BCSettings mUserSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent();
        currentUser = BCUtils.getCurrentUser();
    }


    public void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            dismissProgress();
        } else {
            mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.please_wait), msg);
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgress();
        super.onDestroy();
    }

    @SuppressLint("DefaultLocale")
    protected void setProgressDialog(int progress) {
        mProgressDialog.setMessage(String.format("Processing %d", progress));
    }

    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void makeStatusBarNotTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.clearFlags(FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private boolean checkNetwork() {
        return BCUtils.isNetworkAvailable(this);
    }

    public boolean isOffline() {
        return (mUserSettings != null && mUserSettings.isOffline()) || !checkNetwork();
    }

    public void normalSetupBackToolbar(String title, Toolbar toolbar) {
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }



}
