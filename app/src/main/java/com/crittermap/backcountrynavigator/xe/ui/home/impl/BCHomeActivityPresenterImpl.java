package com.crittermap.backcountrynavigator.xe.ui.home.impl;

import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivityFragmentsContracts;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl;

public class BCHomeActivityPresenterImpl implements BCHomeActivityFragmentsContracts.IBCHomeActivityPresenter {
    public boolean isStatFragmentDisplayed = false;
    private BCHomeActivityFragmentsContracts.IBCHomeActivityView view;

    public BCHomeActivityPresenterImpl(BCHomeActivityFragmentsContracts.IBCHomeActivityView view) {
        this.view = view;
    }

    @Override
    public void onBtnStatsClicked() {
        if (!isStatFragmentDisplayed) {
            isStatFragmentDisplayed = true;
            view.displayStatsFragment(BCStatFragmentPresenterImpl.STAT_MODE.MINI);
        } else {
            isStatFragmentDisplayed = false;
            view.closeStatsFragment();
        }
    }
}
