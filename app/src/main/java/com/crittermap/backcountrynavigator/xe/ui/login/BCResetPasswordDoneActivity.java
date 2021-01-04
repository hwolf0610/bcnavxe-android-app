package com.crittermap.backcountrynavigator.xe.ui.login;

import android.os.Bundle;
import android.widget.Button;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BCResetPasswordDoneActivity extends BCBaseActivity {

    @BindView(R.id.done_button) Button mDoneButton;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_done);
        ButterKnife.bind(this);
    }

    /*
       Done button clicked
    */
    @OnClick(R.id.done_button) public void onDoneButtonClicked() {
        BCUtils.goToLogin(this);
    }
}
