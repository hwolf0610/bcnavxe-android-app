package com.crittermap.backcountrynavigator.xe.ui.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.eventbus.BCUpdateUserProfileSuccessEvent;
import com.crittermap.backcountrynavigator.xe.service.BCIntendService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by henryhai on 3/29/18.
 */

public class BCBasicInformationActivity extends BCBaseActivity implements View.OnClickListener {
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.save_btn)
    Button mSave;
    @BindView(R.id.first_name_txt)
    EditText mFirstName;
    @BindView(R.id.last_name_txt)
    EditText mLastName;
    @BindView(R.id.username_txt)
    EditText mUsername;

    private BCUser mCurrentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
        ButterKnife.bind(this);
        configToolbar();
        makeStatusBarNotTransparent();
        bindUserInfo();
        mCurrentUser = BCUtils.getCurrentUser();
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.ic_go_back_left_arrow);
        mToolBar.setTitle(R.string.basic_information);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolBar.setNavigationOnClickListener(this);
    }

    private void bindUserInfo() {
        BCUser user = BCUtils.getCurrentUser();
        mFirstName.setText(user.getFirstName());
        mLastName.setText(user.getLastName());
        mUsername.setText(user.getUserName());
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.save_btn)
    public void onSaveButtonClicked() {
        String firstName = mFirstName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();
        if (BCUtils.isNetworkAvailable(this)) {
            BCIntendService.startActionUpdateUserProfile(this, mCurrentUser.getUserName(), firstName, lastName);
            showProgress(getString(R.string.saving));
        } else {
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
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
    public void onChangePasswordSuccess(final BCUpdateUserProfileSuccessEvent bcUpdateUserProfileSuccessEvent) {
        dismissProgress();
        if (bcUpdateUserProfileSuccessEvent.getUser() != null) {
            mCurrentUser.setFirstName(mFirstName.getText().toString().trim());
            mCurrentUser.setLastName(mLastName.getText().toString().trim());
            BCUtils.saveUserShareRef(mCurrentUser);

            onBackPressed();
        }
    }
}
