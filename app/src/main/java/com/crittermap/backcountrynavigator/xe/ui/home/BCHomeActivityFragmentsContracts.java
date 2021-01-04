package com.crittermap.backcountrynavigator.xe.ui.home;

import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl;

import java.util.List;

public class BCHomeActivityFragmentsContracts {
    public interface OnActivityInteractionListener {
        void onTripSelected(String tripId);

        void onTripSelectedAndSave(String tripId);

        void onSaveGeometry();

        void onSaveGeometries(List<BCGeometry> geometries, int tripZoom);

        void showGraphic();
    }

    public interface IBCHomeActivityPresenter {
        void onBtnStatsClicked();
    }

    public interface IBCHomeActivityView {
        //region Fragments Controller
        void displayStatsFragment(BCStatFragmentPresenterImpl.STAT_MODE statMode);

        void closeStatsFragment();

        void displayStatsSettingsFragment();

        void closeStatsSettingsFragment();

        void displayCompassFragment();

        void closeCompassFragment();

        void displayAddedBookmarkFragment(String bookmarkId);

        void closeAddedBookmarkFragment();

        void displayDownloadMapFragment();

        void closeDownloadMapFragment();

        void displaySharedTripActionFragment();

        void closeSharedTripActionFragment();

        void displayEditGeometryFragment(BCGeometry geometry);

        void closeEditGeometryFragment();

        void displayWayPointFragment(BCGeometry wp);

        void displayQuickAccessFragment();

        void closeQuickAccessFragment();

        void displaySketchControllerFragment();

        void closeSketchControllerFragment();

        void displayColorPickerFragment();

        void closeColorPickerFragment();

        void displayDrawPickerFragment();

        void closeDrawPickerFragment();

        void displayVectorMapDownload();
        //endregion

        //region Common
        void showLayersIcon(boolean isOffline);

        void updateZoomLevelText();

        void setUpFloatingButton();

        void setupNavigation();
        //endregion

        //region Button Click
        //endregion
    }
}
