package com.crittermap.backcountrynavigator.xe.ui.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.BCMembershipDataHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BCMembershipActivity extends BCBaseActivity {

    @BindView(R.id.membership_txt)
    TextView mMemebership;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        ButterKnife.bind(this);
        configToolbar();
        loadUserMemberShip();
    }

    private void configToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void loadUserMemberShip() {
        BCUser currentUser = BCUtils.getCurrentUser();
        BCMembership currentMembership = BCMembershipDataHelper.findbyUserId(currentUser.getUserName());
        String membershipLevel = currentMembership.getMembershipType();
        String membershipExpireDate = BCUtils.reverseDate(currentMembership.getValidTo());
        String membershipStr = String.format(getString(R.string.membership_level), membershipLevel, membershipExpireDate);
        mMemebership.setText(membershipStr);
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
