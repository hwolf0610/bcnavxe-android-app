package com.crittermap.backcountrynavigator.xe.ui.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.eventbus.BCChangePasswordSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCSystemErrorEvent;
import com.crittermap.backcountrynavigator.xe.service.BCIntendService;
import com.crittermap.backcountrynavigator.xe.service.BCUserService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by henryhai on 3/29/18.
 */

public class BCChangePasswordActivity extends BCBaseActivity implements View.OnClickListener {
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.save_btn)
    Button mSave;
    @BindView(R.id.current_password_txt)
    EditText mCurrentPassword;
    @BindView(R.id.new_password_txt)
    EditText mNewPassword;

    private BCUser mCurrentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        configToolbar();
        makeStatusBarNotTransparent();
        mCurrentUser = BCUtils.getCurrentUser();
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.ic_go_back_left_arrow);
        mToolBar.setTitle(R.string.change_password);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolBar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.save_btn)
    public void onSaveButtonClicked() {
        String currentPassword = mCurrentPassword.getText().toString().trim();
        String newPassword = mNewPassword.getText().toString().trim();

        if (currentPassword.length() >= BCConstant.minPassword && newPassword.length() >= BCConstant.minPassword) {
            if (BCUtils.isNetworkAvailable(this)) {
                BCUserService userService = new BCUserService();
                userService.setUserName(mCurrentUser.getUserName());
                BCUserService.ChangePassword changePassword = new BCUserService.ChangePassword(newPassword, currentPassword);
                userService.setChangePassword(changePassword);
                Gson gson = new Gson();
                BCIntendService.startActionChangePassword(this, gson.toJson(userService));
                showProgress(getString(R.string.saving));
            } else {
                BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
            }
        } else {
            if (currentPassword.length() == 0) {
                mCurrentPassword.requestFocus();
                mCurrentPassword.setError(getString(R.string.required_field));
            }
            if (newPassword.length() == 0) {
                mNewPassword.requestFocus();
                mNewPassword.setError(getString(R.string.required_field));
            }
            if (newPassword.length() < BCConstant.minPassword || newPassword.length() > BCConstant.maxLength) {
                mNewPassword.requestFocus();
                mNewPassword.setError(getString(R.string.password_min_max));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePasswordSuccess(final BCChangePasswordSuccessEvent bcChangePasswordSuccessEvent) {
        dismissProgress();

        if (bcChangePasswordSuccessEvent.getResponseStatus() != null) {
            String message = bcChangePasswordSuccessEvent.getResponseStatus().getMessage();
            if (message.equals("password reset successful")) {
                //TODO REWORK ON API
                BCUser user = BCUtils.getCurrentUser();
                user.setPassword(mNewPassword.getText().toString());
                BCUtils.saveUserShareRef(user);
                Toast.makeText(this, R.string.saving_success, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            if (message.equals("false")) {
                BCAlertDialogHelper.showErrorAlert(this, BCErrorType.CHANGE_PASSWORD_ERROR, "");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemError(final BCSystemErrorEvent errorEvent) {
        dismissProgress();

        if (errorEvent != null) {
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.CHANGE_PASSWORD_ERROR, "System error! Please try again later.");
        }
    }
}
