package com.crittermap.backcountrynavigator.xe.share;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.R;

import java.util.List;

public class FragmentHelper {
    private String LOG_TAG = "FragmentHelper";

    public void removeFragmentByTag(FragmentActivity activity, String tag) {
        removeFragmentByTag(activity, tag, null);
    }

    public void removeFragmentByTag(FragmentActivity activity, List<String> tags) {
        for (String tag : tags) {
            removeFragmentByTag(activity, tag, null);
        }
    }

    public void removeFragmentByTag(FragmentActivity activity, String tag, Runnable postAction) {
        Log.d(LOG_TAG, "Remove fragment : " + tag);
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null && fragment.isAdded()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
                if (postAction != null) {
                    postAction.run();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFragment(AppCompatActivity activity, Fragment fragment, String tag, int fragmentContainerId) {
        Log.d(LOG_TAG, "Add fragment with container: " + tag);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        fragmentTransaction.add(fragmentContainerId, fragment, tag).addToBackStack("BCXE").commit();
    }

    public void showFragment(AppCompatActivity activity, AppCompatDialogFragment fragment, String tag) {
        Log.d(LOG_TAG, "Show fragment with container: " + tag);
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragment.show(fragmentManager, tag);
    }
}
