package com.crittermap.backcountrynavigator.xe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.login.BCLoginActivity;
import com.crittermap.backcountrynavigator.xe.ui.register.BCSignUpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BCMainActivity extends BCBaseActivity {

    @BindView(R.id.sign_up_button) Button mSignUpButton;
    @BindView(R.id.log_in_button) Button mLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BCUser user = BCUtils.getCurrentUser();
        if (user != null) {
            BCUtils.goToHome(this);
        }
    }

    /*
        Sign up button clicked
     */
    @OnClick(R.id.sign_up_button) public void onSignUpButtonClicked() {
        Intent intent = new Intent(this, BCSignUpActivity.class);
        startActivity(intent);
    }

    /*
        Login button clicked
     */
    @OnClick(R.id.log_in_button) public void onLoginButtonClicked() {
        Intent intent = new Intent(this, BCLoginActivity.class);
        startActivity(intent);
    }

    /*
        Skip button clicked
     */
    @OnClick(R.id.skip_button) public void onSkipButtonClicked() {
        BCUtils.goToHome(this);
    }
}
