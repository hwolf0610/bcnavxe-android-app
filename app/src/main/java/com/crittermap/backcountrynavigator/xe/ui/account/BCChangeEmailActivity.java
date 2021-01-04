package com.crittermap.backcountrynavigator.xe.ui.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCCheckValidation;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by henry on 3/29/2018.
 */

public class BCChangeEmailActivity extends BCBaseActivity implements View.OnClickListener {
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.save_btn)
    Button mSave;
    @BindView(R.id.current_email_txt)
    EditText mCurrentEmail;
    @BindView(R.id.new_email_txt)
    EditText mNewEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        ButterKnife.bind(this);
        configToolbar();
        makeStatusBarNotTransparent();
        bindUserInfo();
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.ic_go_back_left_arrow);
        mToolBar.setTitle(R.string.change_email);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolBar.setNavigationOnClickListener(this);
    }

    private void bindUserInfo(){
        BCUser user = BCUtils.getCurrentUser();
        mCurrentEmail.setText(user.getUserName());
    }
    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.save_btn)
    public void onSaveButtonClicked() {
        String currentEmail = mCurrentEmail.getText().toString().trim();
        String newEmail = mNewEmail.getText().toString().trim();
        boolean isEmailValid = true;
        if (!BCCheckValidation.isEmailValid(currentEmail)) {
            isEmailValid = false;
            mCurrentEmail.requestFocus();
            mCurrentEmail.setError(getString(R.string.email_is_not_valid));
        }
        if (!BCCheckValidation.isEmailValid(newEmail)) {
            isEmailValid = false;
            mNewEmail.requestFocus();
            mNewEmail.setError(getString(R.string.email_is_not_valid));
        }
        if (isEmailValid) {
            if (BCUtils.isNetworkAvailable(this)) {
                showProgress(getResources().getString(R.string.please_wait_progress));
                //BCIntendService.startActionChangeEmail(this, username, password);
            } else {
                BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
            }
        }
    }
}
