package com.crittermap.backcountrynavigator.xe.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.eventbus.BCForgotPasswordSuccessEvent;
import com.crittermap.backcountrynavigator.xe.service.BCIntendService;
import com.crittermap.backcountrynavigator.xe.service.BCUserService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCCheckValidation;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BCResetPasswordActivity extends BCBaseActivity implements View.OnClickListener {
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.email_txt)
    EditText mEmail;
    @BindView(R.id.reset_password_button)
    Button mResetPassword;

    private boolean isEmailValid = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        configToolbar();
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.icn_white_back);
        mToolBar.setNavigationOnClickListener(this);
    }

    /*
        Handle on back pressed
     */
    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    /*
       Login button clicked
    */
    @OnClick(R.id.reset_password_button)
    public void onLoginButtonClicked() {
        String email = mEmail.getText().toString();
        if (BCCheckValidation.isEmailValid(email)) {
            mResetPassword.setEnabled(false);
            Gson gson = new Gson();
            BCUserService bcUserService = new BCUserService();
            bcUserService.setUsername(email);
            BCIntendService.startActionForgotPassword(this, gson.toJson(bcUserService));
        } else {
            if (email.isEmpty()) {
                mEmail.requestFocus();
                mEmail.setError(getString(R.string.required_field));
            } else {
                mEmail.requestFocus();
                mEmail.setError(getString(R.string.email_is_not_valid));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterSuccess(BCForgotPasswordSuccessEvent bcForgotPasswordSuccessEvent) {
        dismissProgress();
        mResetPassword.setEnabled(true);
        if (bcForgotPasswordSuccessEvent.getResponseStatus() != null) {
            String message = bcForgotPasswordSuccessEvent.getResponseStatus().getData();

            if (message.equals("Mail sent successfully")) {
                Intent intent = new Intent(this, BCResetPasswordDoneActivity.class);
                startActivity(intent);
            } else {
                BCAlertDialogHelper.showErrorAlert(this, BCErrorType.RESET_PASSWORD_ERROR, message);
            }
        } else {
            BCAlertDialogHelper.showErrorAlert(this, null, "");
        }
    }

}
