package com.crittermap.backcountrynavigator.xe.ui.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.eventbus.BCRegisterSuccessEvent;
import com.crittermap.backcountrynavigator.xe.service.BCIntendService;
import com.crittermap.backcountrynavigator.xe.service.BCUserService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCAlertType;
import com.crittermap.backcountrynavigator.xe.share.BCCheckValidation;
import com.crittermap.backcountrynavigator.xe.share.BCConstant;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.login.BCLoginActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by henry on 3/7/2018.
 */

public class BCCreateAccountActivity extends BCBaseActivity implements View.OnClickListener {
    @BindView(R.id.tool_bar) Toolbar mToolBar;
    @BindView(R.id.skip_button) TextView mSkipButton;
    @BindView(R.id.username_txt) EditText mUserName;
    @BindView(R.id.email_txt) EditText mEmail;
    @BindView(R.id.password_txt) EditText mPassword;
    @BindView(R.id.sign_up_button) Button mSignUpButton;
    @BindView(R.id.term_service_checkbox) ImageView mTermOfServiceCheckBox;
    @BindView(R.id.term_of_service_button) TextView mTermOfSerivceButton;
    @BindView(R.id.privacy_policy_button) TextView mPrivacyButton;
    @BindView(R.id.log_in_button) TextView mLoginButton;

    private boolean mIsTermOfServiceChecked = false;
    private boolean isEmailValid = false;
    private boolean isUserNameValid = false;
    private boolean isPasswordValid = false;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        configToolbar();
        enableSignUpButton(mIsTermOfServiceChecked);
        initOnTextChangeListener();
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.icn_white_back);
        mToolBar.setNavigationOnClickListener(this);
    }

    private void initOnTextChangeListener() {
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void afterTextChanged(Editable editable) {
                String userName = mUserName.getText().toString();
                if (BCCheckValidation.isWordValid(userName)) {
                    if (userName.length() >= BCConstant.minLength
                            && userName.length() <= BCConstant.maxLength) {
                        isUserNameValid = true;
                    } else {
                        mUserName.requestFocus();
                        mUserName.setError(getString(R.string.username_min_max));
                        isUserNameValid = false;
                    }
                } else {
                    isUserNameValid = false;
                    mUserName.requestFocus();
                    mUserName.setError(
                            getString(R.string.username_cannot_contain_special_character));
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void afterTextChanged(Editable editable) {
                String email = mEmail.getText().toString();
                if (BCCheckValidation.isEmailValid(email)) {
                    isEmailValid = true;
                } else {
                    isEmailValid = false;
                    mEmail.requestFocus();
                    mEmail.setError(getString(R.string.email_is_not_valid));
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void afterTextChanged(Editable editable) {
                String password = mPassword.getText().toString();
                if (!password.isEmpty()) {
                    if (password.length() >= BCConstant.minPassword
                            && password.length() <= BCConstant.maxPasswordLength) {
                        isPasswordValid = true;
                    } else {
                        mPassword.requestFocus();
                        mPassword.setError(getString(R.string.password_min_max));
                    }
                } else {
                    mPassword.requestFocus();
                    mPassword.setError(getString(R.string.required_field));
                    isPasswordValid = false;
                }
            }
        });
    }

    private void enableSignUpButton(boolean isCheckboxChecked) {
        if (isCheckboxChecked) {
            mSignUpButton.setEnabled(true);
        } else {
            mSignUpButton.setEnabled(false);
        }
    }

    /*
        Handle on back pressed
     */
    @Override public void onClick(View view) {
        onBackPressed();
    }

    /*
        Skip button clicked
     */
    @OnClick(R.id.skip_button) public void onSkipButtonClicked() {
        BCUtils.goToHome(this);
    }

    /*
        Sign Up button clicked
     */
    @OnClick(R.id.sign_up_button) public void onSignUpButtonClicked() {
        if (isEmailValid && isPasswordValid && isUserNameValid) {
            if (BCUtils.isNetworkAvailable(this)) {
                showProgress("Creating Your Account");
                Gson gson = new Gson();
                BCUserService bcUserService = new BCUserService();
                bcUserService.setEmail(mEmail.getText().toString());
                bcUserService.setPassword(mPassword.getText().toString());
                bcUserService.setFirstname(mUserName.getText().toString());
                BCIntendService.startActionRegister(this, gson.toJson(bcUserService));
            } else {
                BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
            }
        } else {
            if (mUserName.getText().toString().isEmpty()) {
                mUserName.requestFocus();
                mUserName.setError(getString(R.string.required_field));
            }
            if (mEmail.getText().toString().isEmpty()) {
                mEmail.requestFocus();
                mEmail.setError(getString(R.string.required_field));
            }
            if (mPassword.getText().toString().isEmpty()) {
                mPassword.requestFocus();
                mPassword.setError(getString(R.string.required_field));
            }
        }
    }

    /*
        Checkbox term of service clicked
     */
    @OnClick(R.id.term_service_checkbox) public void onTermOfServiceCheckboxClicked() {
        mIsTermOfServiceChecked = !mIsTermOfServiceChecked;
        mTermOfServiceCheckBox.setImageDrawable(getResources().getDrawable(
                mIsTermOfServiceChecked ? R.drawable.icn_checkbox_check
                        : R.drawable.icn_checkbox_uncheck));
        enableSignUpButton(mIsTermOfServiceChecked);
    }

    /*
        Term of service button clicked
     */
    @OnClick(R.id.term_of_service_button) public void onTermOfServiceButtonClicked() {
        Uri uri = Uri.parse(BCConstant.TERM_OF_SERVICE_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /*
        Privacy button clicked
     */
    @OnClick(R.id.privacy_policy_button) public void onPrivacyButtonClicked() {
        Uri uri = Uri.parse(BCConstant.PRIVACY_POLICY_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /*
       Login button clicked
    */
    @OnClick(R.id.log_in_button) public void onLoginButtonClicked() {
        Intent intent = new Intent(this, BCLoginActivity.class);
        startActivity(intent);
    }

    @Override public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterSuccess(BCRegisterSuccessEvent bcRegisterSuccessEvent) {
        dismissProgress();
        if (bcRegisterSuccessEvent.getResponseStatus() != null) {
            //BCUser user = bcRegisterSuccessEvent.getUser();
            //BCSingleton singleton = BCSingleton.getInstance();
            //singleton.setCurrentUser(user);
            //BCUtils.goToHome(this);
            Intent intent = new Intent(this, BCCreateAccountDoneActivity.class);
            startActivity(intent);
        } else {
            BCAlertDialogHelper.showAlertDialogBuider(this, BCAlertType.INVALID_EMAIL,getString(R.string.emailExistText),null,null);
        }
    }
}
