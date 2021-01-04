package com.crittermap.backcountrynavigator.xe.ui.register;

import android.os.Bundle;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;

/**
 * Created by henryhai on 3/13/18.
 */

public class BCCreateAccountDoneActivity extends BCBaseActivity {

    @BindView(R.id.done_button) Button mDoneButton;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_done);
        ButterKnife.bind(this);
    }

    /*
       Done button clicked
    */
    @OnClick(R.id.done_button) public void onDoneButtonClicked() {
        BCUtils.goToMain(this);
    }
}
