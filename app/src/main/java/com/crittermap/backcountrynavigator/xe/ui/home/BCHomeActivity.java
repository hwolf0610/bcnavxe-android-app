package com.crittermap.backcountrynavigator.xe.ui.home;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.common.viewmodel.BC_MapViewModel;
import com.crittermap.backcountrynavigator.xe.common.viewmodel.BC_UserSettingsViewModel;
import com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController;
import com.crittermap.backcountrynavigator.xe.controller.BC_SearchWidget;
import com.crittermap.backcountrynavigator.xe.controller.constant.BC_DRAW_MODE;
import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCGeometryDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTripDAO;
import com.crittermap.backcountrynavigator.xe.controller.eventbus.BC_FoundGeometryToEditEvent;
import com.crittermap.backcountrynavigator.xe.controller.layer.BC_OnlineMapLayer;
import com.crittermap.backcountrynavigator.xe.controller.listener.BCRemoveGraphicEvent;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_Helper;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_TrackingNotification;
import com.crittermap.backcountrynavigator.xe.controller.utils.BC_VectorLoadUtils;
import com.crittermap.backcountrynavigator.xe.data.BCMembershipDataHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembership;
import com.crittermap.backcountrynavigator.xe.data.model.BCMembershipType;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.data.model.bookmark.BCBookmark;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMapDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.eventbus.BCDownloadingMapProcess;
import com.crittermap.backcountrynavigator.xe.eventbus.BCEventPinTripChanged;
import com.crittermap.backcountrynavigator.xe.eventbus.BCGetMembershipSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCSketchEditorChangeEvent;
import com.crittermap.backcountrynavigator.xe.service.BCApiService;
import com.crittermap.backcountrynavigator.xe.service.BCResponseStatus;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkData;
import com.crittermap.backcountrynavigator.xe.service.map.BCMapResponse;
import com.crittermap.backcountrynavigator.xe.service.map.BCVectorMapCatalogResponse;
import com.crittermap.backcountrynavigator.xe.service.membership.BCMembershipIntentService;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper.BCDialogType;
import com.crittermap.backcountrynavigator.xe.share.BCAlertType;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.FragmentHelper;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.share.PieProgressDrawable;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseDaggerActivity;
import com.crittermap.backcountrynavigator.xe.ui.account.BCAccountActivity;
import com.crittermap.backcountrynavigator.xe.ui.adapter.ListLayerAdapter;
import com.crittermap.backcountrynavigator.xe.ui.bookmark.BCBookmarkActivity;
import com.crittermap.backcountrynavigator.xe.ui.help.BCHelpActivity;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivityFragmentsContracts.OnActivityInteractionListener;
import com.crittermap.backcountrynavigator.xe.ui.home.builder.BC_GeometryInfoViewBuilder;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCAddBookmark1Fragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCAddGeometryFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCAddWayPointFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCColorPickerFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCDownloadMapFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCDrawTypePickerFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCQuickAccessFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCSharedTripFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCSketchControllerFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass.CompassFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.dummy.BCSketchDrawTypeContent;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.BCStatsSettingsFragment;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.model.BCUserStatistic;
import com.crittermap.backcountrynavigator.xe.ui.home.impl.BCHomeActivityPresenterImpl;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.BCMapSourceActivity;
import com.crittermap.backcountrynavigator.xe.ui.selecttrip.BCSelectTripActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.BCSettingsActivity;
import com.crittermap.backcountrynavigator.xe.ui.settings.base.BaseActivityContracts;
import com.crittermap.backcountrynavigator.xe.ui.settings.base.InteractorImpl;
import com.crittermap.backcountrynavigator.xe.ui.settings.base.PresenterImpl;
import com.crittermap.backcountrynavigator.xe.ui.trips.BCDestinationActivity;
import com.crittermap.backcountrynavigator.xe.ui.trips.BCTripsActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.NavigationChangedEvent;
import com.esri.arcgisruntime.mapping.view.NavigationChangedListener;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.raizlabs.android.dbflow.StringUtils;
import com.raizlabs.android.dbflow.data.Blob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.DB_TRACKING_TEMP;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_ID;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_TRIP_ID;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_TRIP_NAME;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.SEARCH_RESULT;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.createTripNamePrefix;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.PAUSE;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.STOP;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.TRACKING;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.INTENT_EXTRA_TRIP_ID;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.NOTIFICATION_KEY;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.NOTI_STOP_TRACKING;
import static com.crittermap.backcountrynavigator.xe.share.BCUtils.KEY_SHOW_DOWNLOAD_TUTORIAL;
import static com.crittermap.backcountrynavigator.xe.share.BCUtils.SHARED_TRIP;
import static com.crittermap.backcountrynavigator.xe.share.BCUtils.getCurrentUser;
import static com.crittermap.backcountrynavigator.xe.share.BCUtils.isPurchasedUser;
import static com.crittermap.backcountrynavigator.xe.ui.home.builder.BC_GeometryInfoViewBuilder.isWaypoint;
import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl.STAT_MODE.EXPAND;
import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.impl.BCStatFragmentPresenterImpl.STAT_MODE.MINI;
import static com.crittermap.backcountrynavigator.xe.ui.saveTrip.BCSaveTripActivity.mergeTrackingGeometries;

public class BCHomeActivity extends BCBaseDaggerActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ListLayerAdapter.ListenerCheckBox, BC_ArcGisController.SketchDrawListener,
        BC_SearchWidget.DisplaySearchResultListener, CompoundButton.OnCheckedChangeListener,
        BCSketchControllerFragment.OnSketchFragmentInteractionListener,
        BCColorPickerFragment.OnFragmentInteractionListener,
        BCDrawTypePickerFragment.OnListFragmentInteractionListener, View.OnClickListener,
        BCDownloadMapFragment.OnDownloadMapFragmentInteractionListener,
        BCAddGeometryFragment.OnFragmentInteractionListener,
        BCAddWayPointFragment.OnFragmentInteractionListener,
        BCSharedTripFragment.OnFragmentInteractionListener,
        BC_ArcGisController.ActivityInteractor,
        BCHomeActivityFragmentsContracts.IBCHomeActivityView,
        BCStatsSettingsFragment.OnFragmentInteractionListener,
        BCStatFragment.OnStatsFragmentListener, BaseActivityContracts.View,
        BCQuickAccessFragment.OnQuickAccessFragmentInteractionListener {

    //region Static
    static final String STATE_FRAGMENT = "state_of_fragment";
    static final String STATE_SKETCH_FRAGMENT = "state_of_sketch_fragment";
    static final String STATE_COLOR_FRAGMENT = "state_of_color_fragment";
    static final String STATE_DRAW_TYPE_FRAGMENT = "state_of_draw_fragment";
    static final String STATE_NEW_WAY_POINT = "state_of_add_waypoint_fragment";
    static final String STATE_DOWNLOAD_MAP = "state_of_download_map_fragment";

    static final String TAG = BCHomeActivity.class.getSimpleName();
    static final int ACTIVITY_BOOKMARK_RESULT = 1001;
    static final int ACTIVITY_SELECT_TRIP_AND_SAVE_RESULT = 1003;
    public static final int ACTIVITY_SELECT_TRIP_ONLY = 1004;
    public static final String KEY_CENTER_POINT = "centerPoint";

    static int requestPermissionCode = 11;
    static final String[] permission = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WAKE_LOCK
    };
    //endregion

    //region Butter Knife Binding View
    @BindView(R.id.fab_layer)
    ImageButton mFabQuickAccess;
    @BindView(R.id.fab_stat)
    ImageButton mFabShowStat;
    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.done_button)
    Button mWaypointDoneButton;
    @BindView(R.id.searchView)
    android.support.v7.widget.SearchView mSearchView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.fab_zoomOut)
    ImageButton mFabZoomOut;
    @BindView(R.id.ln_searchView)
    LinearLayout mLnSearchView;
    @BindView(R.id.fab_download_map_layout)
    LinearLayout mFabDownloadMapLayout;
    @BindView(R.id.fab_record_a_track_layout)
    LinearLayout mFabRecordTrackLayout;
    @BindView(R.id.fab_create_draw_layout)
    LinearLayout mFabCreateDrawLayout;
    @BindView(R.id.fab_new_way_point_my_location_layout)
    LinearLayout mFabNewWayPointMyLocationLayout;
    @BindView(R.id.fab_new_way_point_layout)
    LinearLayout mFabNewWayPointLayout;
    @BindView(R.id.fab_add_new)
    FloatingActionButton mFabAddNew;
    @BindView(R.id.fab_add_new_draw)
    ImageButton mFabAddNewDraw;
    @BindView(R.id.fab_download_map)
    FloatingActionButton mFabDownloadMap;
    @BindView(R.id.fab_record_a_track)
    FloatingActionButton mFabRecordTrack;
    @BindView(R.id.fab_create_draw)
    FloatingActionButton mFabCreateDraw;
    @BindView(R.id.fab_new_way_point_my_location)
    FloatingActionButton mFabNewWayPointMyLocation;
    @BindView(R.id.fab_new_way_point)
    FloatingActionButton mFabNewWayPoint;
    @BindView(R.id.tracking_menu_action)
    LinearLayout trackingMenuAction;
    @BindView(R.id.fabBGLayout)
    View fabBGLayout;
    @BindView(R.id.btn_pause_tracking)
    Button pauseTracking;
    @BindView(R.id.fab_zoomIn)
    ImageButton mFabZoomIn;
    @BindView(R.id.download_map_progress_container)
    ConstraintLayout download_map_progress_container;
    @BindView(R.id.download_map_progress)
    ImageView imv_download_map_progress;
    @BindView(R.id.tv_downloading_map)
    TextView tv_downloading_map;
    @BindView(R.id.fab_location)
    ImageView mFabLocation;
    @BindView(R.id.edit_trip_layout)
    ConstraintLayout edit_trip_layout;
    @BindView(R.id.tv_edit_trip_name)
    TextView tv_edit_trip_name;
    @BindView(R.id.lb_route)
    TextView lb_route;
    @BindView(R.id.imb_edit_trip)
    ImageButton imb_edit_trip;
    @BindView(R.id.imb_delete_edit_trip)
    ImageButton imb_delete_edit_trip;
    @BindView(R.id.tv_zoom_level)
    TextView tvZoomLevel;
    //endregion

    //region Dagger Injection
    @Inject
    BC_ArcGisController gisController;
    @Inject
    FragmentHelper fragmentHelper;
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    //endregion

    //region View model
    private BC_MapViewModel mMapViewModel;
    private BC_UserSettingsViewModel userSettingsViewModel;
    //endregion

    //region Properties
    private String currentSelectTrip = "";
    private ArcGISMap map = new ArcGISMap();
    private MapView mMapView;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isFABOpen = false;
    private boolean isQuickAccessFragmentDisplayed = false;
    private boolean isSketchFragmentDisplayed = false;
    private boolean isColorPickerFragmentDisplayed = false;
    private boolean isAddNewDrawDisplayed = false;
    private boolean isEditGeometryFragmentDisplayed = false;
    private boolean isDownloadMapFragmentDisplayed = false;
    private boolean isDownloadingMap = false;
    private ArrayList<BCGeometry> tempGeometries = new ArrayList<>();
    private PieProgressDrawable pieProgressDrawable;
    private List<BCTripInfo> bcTripInfoList = new ArrayList<>();
    private GraphicsOverlay sharedTripGraphicOverlay;
    private Observable<BCMap> observable;
    private boolean isFirstLoad = true;
    private OnActivityInteractionListener onActivityInteractionListener;
    private BCHomeActivityPresenterImpl presenter;
    private SwitchCompat drawerSwitch;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ActionMode.Callback mVectorDownloadActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.select_vector_download_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            //create grid for vector downloads
            BCApiService.getInstance().doLoadVectorMapCatalogAsync("wwbcnvector", new WebServiceCallBack<BCVectorMapCatalogResponse>() {
                @Override
                public void onSuccess(BCVectorMapCatalogResponse data) {
                    //show dialog
                    final BC_VectorLoadUtils loadUtils = new BC_VectorLoadUtils(data);

                    //use it to create rectangles on screen
                    gisController.drawVectorSquares(loadUtils);

                }
                @Override
                public void onFailed(String errorMessage) {
                    Log.w(TAG, "Getting Vector Map Catalog: " + errorMessage);
                }
            });

            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.done:

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            gisController.setDrawMode(BC_DRAW_MODE.NONE);
            ActionMode mActionMode = null;
        }
    };
    //endregion

    //region Android Activity Life Circle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        initializeData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        mAppBarLayout.setVisibility(View.GONE);

        presenter = new BCHomeActivityPresenterImpl(this);

        setUpDrawer();

        setupArcgis();

        this.permissionCheck();

        setUpFloatingButton();

        new BC_SearchWidget(mSearchView, this, this);
        mSearchView.setFocusable(false);

        // If returning from a configuration change, get the
        // fragment state and set the button text.
        setupFragmentSaveInstanceState(savedInstanceState);

        initFloatingActionMenu();

        mMapView.addMapScaleChangedListener(new MapScaleChangedListener() {

            @Override
            public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
                updateZoomLevelText();
            }
        });

        observable = Observable.create(new ObservableOnSubscribe<BCMap>() {
            @Override
            public void subscribe(ObservableEmitter<BCMap> emitter) {
                loadMap(emitter);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(getLoadMapObservable());

        setupOthers();

        safeDrawShareTrip();
    }

    private void setupFragmentSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //FIXME fix fragments state
        } else {
            mFabAddNewDraw.animate().translationY(400);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(STATE_FRAGMENT, isQuickAccessFragmentDisplayed);
        outState.putBoolean(STATE_SKETCH_FRAGMENT, isSketchFragmentDisplayed);
        outState.putBoolean(STATE_COLOR_FRAGMENT, isColorPickerFragmentDisplayed);
        outState.putBoolean(STATE_DRAW_TYPE_FRAGMENT, isAddNewDrawDisplayed);
        outState.putBoolean(STATE_NEW_WAY_POINT, isEditGeometryFragmentDisplayed);
        outState.putBoolean(STATE_DOWNLOAD_MAP, isDownloadMapFragmentDisplayed);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        loadMembership();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (isDownloadingMap) {
            isDownloadingMap = false;
            download_map_progress_container.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        BCUtils.saveLastLocation(mMapView);
        CompassFragment compassFragment = (CompassFragment) getSupportFragmentManager().findFragmentByTag(CompassFragment.TAG);
        if (compassFragment != null && compassFragment.isVisible()) {
            compassFragment.stopCompass();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
        userSettingsViewModel.fetchUserSettingsData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
    //endregion

    //region Initialize
    private void setupOthers() {
        Menu menu = mNavigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_offline_toggle);
        View actionView = menuItem.getActionView();

        drawerSwitch = actionView.findViewById(R.id.drawer_switch);

        mMapViewModel = ViewModelProviders.of(this).get(BC_MapViewModel.class);
        final android.arch.lifecycle.Observer<String> nameObserver = new android.arch.lifecycle.Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                if (StringUtils.isNotNullOrEmpty(newName)) {
                    if (StringUtils.isNotNullOrEmpty(newName)) {
                        BCMap map = BCMapDBHelper.getByShortName(newName);
                        BCUtils.saveLastMapPref(map);
                    }
                    observable.subscribe(getLoadMapObservable());
                }
            }
        };

        mMapViewModel.getCurrentName().observe(this, nameObserver);

        userSettingsViewModel = ViewModelProviders.of(this, viewModelFactory).get(BC_UserSettingsViewModel.class);
        userSettingsViewModel.getCurrentUserSettings().observe(this, new android.arch.lifecycle.Observer<BCSettings>() {
            @Override
            public void onChanged(@Nullable BCSettings settings) {
                if (settings != null) {
                    gisController.configTouchOnMap(settings);
                    renderUIBySettings(settings);
                }
            }
        });
        userSettingsViewModel.fetchUserSettingsData();

        pieProgressDrawable = new PieProgressDrawable();
        pieProgressDrawable.setColor(ContextCompat.getColor(this, R.color.greenishBlue));
        imv_download_map_progress.setImageDrawable(pieProgressDrawable);

        drawerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userSettingsViewModel.setOffline(isChecked);
            }
        });
    }

    private void setUpDrawer() {
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mDrawerLayout.setScrimColor(ContextCompat.getColor(this, R.color.lightNavigationDrawer));

        findViewById(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
        setupNavigation();
    }

    private void setupArcgis() {
        ArcGISRuntimeEnvironment.setLicense(getResources().getString(R.string.license));
        mMapView = findViewById(R.id.mapView);
        gisController.setup(this, mMapView, this);
        gisController.setActivityInteractor(this);
        mMapView.addNavigationChangedListener(new NavigationChangedListener() {
            @Override
            public void navigationChanged(NavigationChangedEvent navigationChangedEvent) {
                Log.v(TAG, "NavigationChanged: navigating=" + navigationChangedEvent.isNavigating());
                //when finished navigating.
                if (!navigationChangedEvent.isNavigating()) {
                    try {
                        gisController.onViewPointChangedAfterNavigation();
                    } catch (Exception e) {
                        Log.e(TAG, "Retrieving viewpoint from NavigationChangedEvent", e);
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isQuickAccessFragmentDisplayed) {
            closeQuickAccessFragment();
        } else if (isFABOpen) {
            closeFABMenu();
        } else if (isEditGeometryFragmentDisplayed) {
            if (gisController.drawMode == BC_DRAW_MODE.SKETCH) {
                findViewById(R.id.rv_sketch_controller).setVisibility(View.VISIBLE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager
                        .findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    FragmentTransaction fragmentTransaction =
                            fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment).commit();
                }
                isEditGeometryFragmentDisplayed = false;
                mAppBarLayout.setVisibility(View.GONE);
            } else {
                BCAlertDialogHelper.showAlertDialogBuider(this, BCAlertType.WARNING, "Do you want to save your drawing?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onDoneButtonClicked();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        closeEditGeometryFragment();
                    }
                });
            }
        } else if (presenter.isStatFragmentDisplayed) {
            presenter.onBtnStatsClicked();
        } else {
            if (doubleBackToExitPressedOnce) {
                Logger.i("OnBackPressed: got it!");
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.press_back_to_exit),
                    Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_trip:
                onClickTripItem();
                break;
            case R.id.nav_account:
                if (BCUtils.getCurrentUser() != null) {
                    Intent intent = new Intent(this, BCAccountActivity.class);
                    startActivity(intent);
                } else {
                    BCUtils.goToMain(this);
                }
                break;
            case R.id.nav_map:
                onClickMapItem();
                break;
            case R.id.nav_bookmark:
                if (BCUtils.getCurrentUser() != null) {
                    Intent bmIntend = new Intent(this, BCBookmarkActivity.class);
                    startActivityForResult(bmIntend, ACTIVITY_BOOKMARK_RESULT);
                } else {
                    BCAlertDialogHelper.showNeedLoginAlert(this, this);
                }
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, BCHelpActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, BCSettingsActivity.class));

        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (isQuickAccessFragmentDisplayed) {
            closeQuickAccessFragment();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == ACTIVITY_BOOKMARK_RESULT) {
            if (resultCode == RESULT_OK) {
                try {
                    BCBookmarkData bookmarkData = (BCBookmarkData) intent.getSerializableExtra("bookmark");
                    gisController.navigateToBookmark(bookmarkData, isOffline());
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e(TAG, e.getMessage());
                    Toast.makeText(BCHomeActivity.this, "Map loading error!!! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == ACTIVITY_SELECT_TRIP_ONLY && resultCode == RESULT_OK) {
            String tripId = intent.getStringExtra(INTENT_EXTRA_TRIP_ID);
            if (StringUtils.isNotNullOrEmpty(tripId)) {
                currentSelectTrip = tripId;
                onActivityInteractionListener.onTripSelected(tripId);
                BCUtils.saveTrackingTrip(tripId);
            } else {
                Toast.makeText(this, "No trip selected", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == ACTIVITY_SELECT_TRIP_AND_SAVE_RESULT && resultCode == RESULT_OK) {
            String tripId = intent.getStringExtra(INTENT_EXTRA_TRIP_ID);
            if (StringUtils.isNotNullOrEmpty(tripId)) {
                currentSelectTrip = tripId;
                if (gisController.drawMode == BC_DRAW_MODE.SKETCH) {
                    onActivityInteractionListener.onSaveGeometries(tempGeometries, BC_Helper.getZoomLevelByScale(mMapView.getMapScale()));
                } else if (gisController.drawMode == BC_DRAW_MODE.GET_TRIP_FOR_TRACKING) {
                    startTracking(tripId);
                } else {
                    onActivityInteractionListener.onTripSelectedAndSave(tripId);
                }
                BCUtils.saveTrackingTrip(tripId);
            } else {
                Toast.makeText(this, "No trip selected", Toast.LENGTH_SHORT).show();
            }
        }

        gisController.configTouchOnMap(userSettingsViewModel.getCurrentUserSettings().getValue());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == requestPermissionCode) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
        }
    }
    //endregion

    //region Components listeners impl
    @Override
    public void onRowChecked(int index, boolean value) {
        gisController.toggleLayer(index, value);
    }

    @Override
    public void onSketchEditorChange(boolean canUndo, boolean canRedo) {
        EventBus.getDefault().post(new BCSketchEditorChangeEvent(canUndo, canRedo, tempGeometries));
    }

    @Override
    public void displaySearchResult(final GeocodeResult result) {
        // create graphic object for resulting location
        gisController.setDrawMode(BC_DRAW_MODE.SEARCH);
        final Point resultPoint = result.getDisplayLocation();
        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(mMapView.getContext(),
                R.drawable.point);
        final ListenableFuture<PictureMarkerSymbol> p = PictureMarkerSymbol.createAsync(drawable);
        final Activity activity = this;
        p.addDoneListener(new Runnable() {
            @Override
            public void run() {
                PictureMarkerSymbol pictureMarkerSymbol;
                try {
                    pictureMarkerSymbol = p.get();
                    pictureMarkerSymbol.setHeight(40);
                    pictureMarkerSymbol.setWidth(40);
                    Graphic resultLocGraphic =
                            new Graphic(resultPoint, result.getAttributes(), pictureMarkerSymbol);


                    Graphic trickGraphic = new Graphic(resultPoint, result.getAttributes(), pictureMarkerSymbol);
                    resultLocGraphic.getAttributes().put(SEARCH_RESULT, "true");


                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentUser == null) {
                                BCAlertDialogHelper.showNeedLoginAlert(BCHomeActivity.this, BCHomeActivity.this);
                            } else {
                                mMapView.getCallout().dismiss();
                                BCMap map = MapUtils.getDefaultOrLastMap();
                                final BCBookmark bookmark = new BCBookmark();
                                bookmark.setName(result.getAttributes().get("PlaceName").toString());
                                bookmark.setBasemapType(map.getShortName());
                                bookmark.setLon(String.valueOf(resultPoint.getX()));
                                bookmark.setLat(String.valueOf(resultPoint.getY()));
                                bookmark.setZoom(String.valueOf(BC_Helper.getZoomLevelByScale(mMapView.getMapScale())));
                                bookmark.setUserName(currentUser.getUserName());

                                BCApiService.getInstance().doAddBookmark(bookmark, new WebServiceCallBack<BCResponseStatus>() {
                                    @Override
                                    public void onSuccess(BCResponseStatus data) {
                                        //After save successfully, call fragment
                                        displayAddedBookmarkFragment("bookmarkId");
                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                closeAddedBookmarkFragment();
                                            }
                                        }, 2000);
                                        BCDatabaseHelper.save(bookmark);
                                    }

                                    @Override
                                    public void onFailed(String errorMessage) {
                                        Toast.makeText(getApplicationContext(), "SAVE BOOKMARK ONLINE ERROR", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    };
                    @SuppressLint("InflateParams")
                    View callOutView = activity.getLayoutInflater().inflate(R.layout.add_bookmark_callout, null);
                    gisController.drawCalloutWithGraphic(resultLocGraphic, trickGraphic, listener, callOutView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mMapView.setViewpointAsync(new Viewpoint(result.getExtent()));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void loadMapToArcGisMap(BCMap bcMap) {
        BCUser user = BCUtils.getCurrentUser();
        if (user == null) { //not login
            MapUtils.getOnlineMap(mMapView, map, bcMap, isFirstLoad);
        } else {
            String mapType = bcMap.getMapType();
            if (isOffline()) {
                if (mapType == null) {
                    MapUtils.getOfflineMap(mMapView, map, bcMap, isFirstLoad);
                } else {
                    switch (mapType) {
                        case "multilayer":
                            MapUtils.getOfflineMultilayerMap(mMapView, map, bcMap, isFirstLoad);
                            break;
                        case "mapwithmapid":
                            MapUtils.getOfflineVectorMap(mMapView, map, bcMap, isFirstLoad);
                            break;
                        default:
                            MapUtils.getOfflineMap(mMapView, map, bcMap, isFirstLoad);
                    }
                }

            } else {
                if (mapType == null) {
                    MapUtils.getOnlineMap(mMapView, map, bcMap, isFirstLoad);
                } else {
                    switch (mapType) {
                        case "multilayer":
                            MapUtils.getOnlineMultilayerMap(mMapView, map, bcMap, isFirstLoad);
                            break;
                        case "mapwithmapid":
                            MapUtils.getOnlineVectorMap(mMapView, map, bcMap, isFirstLoad);
                            break;
                        default:
                            MapUtils.getOnlineMap(mMapView, map, bcMap, isFirstLoad);
                    }
                }
            }
        }
        isFirstLoad = false;
        mMapView.setMap(map);
        mMapView.getMap().getBasemap().setName(bcMap.getShortName());
        updateZoomLevelText();
        mMapView.post(new Runnable() {
            @Override
            public void run() {
                gisController.onViewPointChangedAfterNavigation();
            }
        });
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }
    //endregion

    //region EVENT BUS
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMembershipSuccess(final BCGetMembershipSuccessEvent BCGetMembershipSuccessEvent) {
        if (BCGetMembershipSuccessEvent.getMembership() != null) {
            BCMembership membership = BCGetMembershipSuccessEvent.getMembership();

            if (membership.getMembershipType() != null) {
                checkExpiredDay(membership);
            }

            BCMembership currentMembership = BCMembershipDataHelper.findbyUserId(currentUser.getUserName());
            if (currentMembership == null) {
                membership.setUserId(currentUser.getUserName());
                BCMembershipDataHelper.save(membership);
            } else {
                checkMembershipChanged(currentMembership, membership);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPinTripChanged(final BCEventPinTripChanged event) {
        if (event != null && event.getBcTripInfo() != null)
            switch (event.getAction()) {
                case PIN:
                    Logger.d(TAG, "onPinTripChanged draw trip " + event.getBcTripInfo().getId());
                    drawTrip(event.getBcTripInfo());
                    break;
                case UNPIN:
                    gisController.removeLayer(event.getBcTripInfo().getId());
                    Logger.d(TAG, "onPinTripChanged remove layer trip");
                    break;
                case REDRAW:
                    gisController.removeLayer(event.getBcTripInfo().getId());
                    drawTrip(event.getBcTripInfo());
                    Logger.d(TAG, "onPinTripChanged redraw layer trip");
                    break;
                case REPLACE:
                    gisController.removeLayer(event.getBcTripInfo().getId());
                    drawTrip(event.getNewTripInfo());

            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingMap(final BCDownloadingMapProcess event) {
        dismissProgress();
        String log = String.format(Locale.getDefault(), "Downloading \"%s\"", event.getMapName());
        pieProgressDrawable.setLevel((int) event.getProgressPercent());
        imv_download_map_progress.invalidate();
        isDownloadingMap = true;
        Log.d(TAG, log);
        tv_downloading_map.setText(log);
        if (download_map_progress_container.getVisibility() != View.VISIBLE) {
            download_map_progress_container.setVisibility(View.VISIBLE);
            download_map_progress_container.setAlpha(1);
        }
        if ((int) event.getProgressPercent() == 100) {
            tv_downloading_map.setText(String.format(Locale.getDefault(), "Finish Downloading \"%s\"", event.getMapName()));
            download_map_progress_container.animate().alpha(0).setStartDelay(2000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    download_map_progress_container.setVisibility(View.GONE);
                    isDownloadingMap = false;
                }
            });
            BCMap lastMap = MapUtils.getDefaultOrLastMap();
            if (lastMap != null && lastMap.getMapName().equals(event.getMapName()) && isOffline()) {
                mMapViewModel.getCurrentName().postValue(lastMap.getShortName());
            }
        }
    }
    //endregion

    //region FLOAT MENU
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            Rect outRect = new Rect();
            mSearchView.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                mSearchView.clearFocus();
                hideKeyboard(mSearchView);
            }

        }
        return super.dispatchTouchEvent(event);
    }

    private void initFloatingActionMenu() {
        final BCHomeActivity activity = this;
        final Context c = this;
        mFabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    if (!isFABOpen) {
                        showFABMenu();
                    } else {
                        closeFABMenu();
                    }
                } else {
                    BCAlertDialogHelper.showNeedLoginAlert(activity, c);
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
    }

    private void showFABMenu() {
        ArrayList<View> animationViews = new ArrayList<>();
        animationViews.add(mFabNewWayPointLayout);
        animationViews.add(mFabNewWayPointMyLocationLayout);
        animationViews.add(mFabCreateDrawLayout);
        isFABOpen = true;
        if (BCUtils.isTracking()) {
            mFabRecordTrackLayout.setVisibility(View.INVISIBLE);
        } else {
            mFabRecordTrackLayout.setVisibility(View.VISIBLE);
            animationViews.add(mFabRecordTrackLayout);
        }
        if (!isDownloadingMap) {
            mFabDownloadMapLayout.setVisibility(View.VISIBLE);
            animationViews.add(mFabDownloadMapLayout);
        }


        mFabCreateDrawLayout.setVisibility(View.VISIBLE);
        mFabNewWayPointMyLocationLayout.setVisibility(View.VISIBLE);
        mFabNewWayPointLayout.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        mFabAddNew.setEnabled(false);
        mFabAddNew.animate().rotation(45).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFabAddNew.setEnabled(true);
                super.onAnimationEnd(animation);
            }
        });
        float distance = getResources().getDimension(R.dimen.margin_64);

        for (int i = 0; i < animationViews.size(); i++) {
            animationViews.get(i).animate().translationY(-distance * (i + 1));
        }
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        mFabAddNew.setEnabled(false);
        mFabAddNew.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFabAddNew.setEnabled(true);
                super.onAnimationEnd(animation);
            }
        });
        mFabDownloadMapLayout.animate().translationY(0);
        mFabRecordTrackLayout.animate().translationY(0);
        mFabCreateDrawLayout.animate().translationY(0);
        mFabNewWayPointMyLocationLayout.animate().translationY(0);
        mFabNewWayPointLayout.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isFABOpen) {
                    mFabDownloadMapLayout.setVisibility(View.GONE);
                    mFabRecordTrackLayout.setVisibility(View.GONE);
                    mFabCreateDrawLayout.setVisibility(View.GONE);
                    mFabNewWayPointMyLocationLayout.setVisibility(View.GONE);
                    mFabNewWayPointLayout.setVisibility(View.GONE);
                }
            }
        });
    }
    //endregion

    //region BOOKMARK
    @Override
    public void displayAddedBookmarkFragment(String bookmarkId) {
        fragmentHelper.addFragment(
                this,
                BCAddBookmark1Fragment.newInstance(bookmarkId),
                BCAddBookmark1Fragment.TAG,
                R.id.fragment_container);
    }

    @Override
    public void closeAddedBookmarkFragment() {
        fragmentHelper.removeFragmentByTag(this, BCAddBookmark1Fragment.TAG);
    }
    //endregion

    //region SHOW TRIP
    private void drawTrip(BCTripInfo trip) {
        try {
            String tripId = trip.getId();

            Log.d(TAG, "Draw trip: " + tripId);
            if (gisController.findLayerByName(tripId) != null) return;


            BCGeometryDAO geometryDAO = new BCGeometryDAO(tripId);
            List<BCGeometry> geometries = geometryDAO.getAll();
            if (geometries == null || geometries.size() <= 0) return;

            Point center = Geometry.fromJson(geometries.get(0).getGeoJSON()).getExtent().getCenter();
            gisController.navigateToLocationNoZoom(center);

            GraphicsOverlay graphicsOverlay = BC_ArcGisController.createTripGraphicsOverlay();
            for (BCGeometry bcGeometry : geometries) {
                gisController.draw(trip, bcGeometry, graphicsOverlay);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, e.getMessage());
        }
    }

    private void drawPinnedTrips() {
        bcTripInfoList = BCTripInfoDBHelper.loadPinnedTrip();
        for (BCTripInfo bcTripInfo : bcTripInfoList) {
            if (bcTripInfo.isShowedChecked())
                drawTrip(bcTripInfo);
        }
    }

    private void safeDrawShareTrip() {
        final String tripId = getIntent().getStringExtra(SHARED_TRIP);
        if (StringUtils.isNotNullOrEmpty(tripId)) {
            //DOWNLOAD SHARE TRIP
            WebServiceCallBack<BCTripInfo> webServiceCallBack = new WebServiceCallBack<BCTripInfo>() {
                @Override
                public void onSuccess(BCTripInfo trip) {
                    //READ SHARE TRIP
                    try {
                        if (!trip.isShareTrek()) {
                            Toast.makeText(getApplicationContext(), "This trip is not shared with you", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //CHECK SHARE TRIP
                        BCTripDAO tripDAO = new BCTripDAO(BC_Helper.getTripDBPath(tripId), tripId);
                        List<BCTrip> arr = tripDAO.getAll();
                        if (arr.size() == 0) {
                            throw new Exception("Trip database has error!!!");
                        }
                        BCTrip bcTrip = arr.get(0);
                        if (BCUtils.getCurrentUser().getUserName().equals(bcTrip.getOwnerId())) {
                            throw new Exception("Cannot display own trip as shared trip");
                        }

                        BCGeometryDAO geometryDAO = new BCGeometryDAO(BC_Helper.getTripDBPath(tripId), tripId);
                        List<BCGeometry> geometries = geometryDAO.getAll();
                        if (geometries == null || geometries.size() <= 0) return;

                        Point center = Geometry.fromJson(geometries.get(0).getGeoJSON()).getExtent().getCenter();
                        gisController.navigateToLocationNoZoom(center);

                        sharedTripGraphicOverlay = BC_ArcGisController.createTripGraphicsOverlay();
                        for (BCGeometry bcGeometry : geometries) {
                            //DRAW SHARE TRIP
                            gisController.draw(trip, bcGeometry, sharedTripGraphicOverlay);
                        }
                        //OPEN FRAGMENT
                        displaySharedTripActionFragment();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getMessage());
                    }
                }

                @Override
                public void onFailed(String errorMessage) {
                    Logger.e(TAG, errorMessage);
                    Toast.makeText(getApplicationContext(), "Fail to download shared trip", Toast.LENGTH_SHORT).show();
                }
            };
            BCApiService.getInstance().doDownloadSharedTrip(tripId, webServiceCallBack);
        }
    }

    @Override
    public void onDiscardSharedTrip() {
        try {
            String tripId = getIntent().getStringExtra(SHARED_TRIP);

            sharedTripGraphicOverlay.getGraphics().clear();
            gisController.removeLayer(tripId);
            sharedTripGraphicOverlay = null;

            BC_TripsDBHelper.createInstance(BC_Helper.getTripDBPath(tripId), tripId).dropDatabse();
            closeSharedTripActionFragment();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, e.getMessage());
            closeSharedTripActionFragment();
        }
    }

    @Override
    public void onSaveSharedTrip() {
        try {
            String tripId = getIntent().getStringExtra(SHARED_TRIP);
            BCTripDAO tripDAO = new BCTripDAO(BC_Helper.getTripDBPath(tripId), tripId);
            List<BCTrip> arr = tripDAO.getAll();
            if (arr.size() == 0) {
                throw new Exception("Trip database has error!!!");
            }
            BCTrip bcTrip = arr.get(0);

            BCTripInfo bcTripInfo = new BCTripInfo();
            bcTripInfo.setId(bcTrip.getId());
            bcTripInfo.setImage(new Blob(bcTrip.getImage()));
            bcTripInfo.setName(bcTrip.getName());
            bcTripInfo.setOwnerId(bcTrip.getOwnerId());
            bcTripInfo.setTrekFolder(bcTrip.getFolder());
            bcTripInfo.setDownloading(false);
            bcTripInfo.setTripStatus(TripStatus.PRISTINE);
            bcTripInfo.setLastSync((new Date()).getTime());
            BCTripInfoDBHelper.save(bcTripInfo);

            sharedTripGraphicOverlay.getGraphics().clear();
            gisController.removeLayer(tripId);
            sharedTripGraphicOverlay = null;

            closeSharedTripActionFragment();

            Toast.makeText(this, "Save shared trip successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, e.getMessage());
            closeSharedTripActionFragment();
        }
    }

    @Override
    public void displaySharedTripActionFragment() {
        mFabAddNew.animate().translationX(200);
        fragmentHelper.addFragment(
                this,
                BCSharedTripFragment.Companion.newInstance(),
                BCSharedTripFragment.TAG,
                R.id.fragment_container
        );
    }

    @Override
    public void closeSharedTripActionFragment() {
        fragmentHelper.removeFragmentByTag(
                this,
                BCSharedTripFragment.TAG,
                new Runnable() {
                    @Override
                    public void run() {
                        mFabAddNew.animate().translationX(0);
                    }
                }
        );
    }

    /**
     * onCheckedChanged event for checkbox of Pinned Trips
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String tripId = (String) buttonView.getTag();
        if (gisController.findLayerByName(tripId) != null) {
            gisController.toggleLayer(tripId, isChecked);
        } else if (isChecked) {
            for (BCTripInfo t : bcTripInfoList) {
                if (t.getId().equals(tripId)) {
                    drawTrip(t);
                    Logger.i(TAG, "drawTrip:" + tripId);
                }
            }
        }
        Logger.i(TAG, "Layers size" + gisController.getLayers().size());
    }
    //endregion

    //region GET_TRIP_FOR_TRACKING
    @SuppressLint("DefaultLocale")
    @OnClick(R.id.fab_record_a_track)
    public void onClickFabRecord() {
        closeFABMenu();
        initEditGeometryToolbar(getString(R.string.prepare_tracking));
        mWaypointDoneButton.setText(R.string.start);
        gisController.setDrawMode(BC_DRAW_MODE.GET_TRIP_FOR_TRACKING);

        BCGeometry bcGeometry = new BCGeometry();
        bcGeometry.setName(createTripNamePrefix() + "-TRACK");
        BCTripInfo tripInfo = BCTripInfoDBHelper.getLastEditedTrip(BCUtils.getCurrentUser().getUserName());
        if (tripInfo != null) {
            currentSelectTrip = tripInfo.getId();
            bcGeometry.setTripId(tripInfo.getId());
        }
        displayEditGeometryFragment(bcGeometry);

        BCUtils.resetUserStatisticValue();
        BCUserStatistic userStatistic = BCUtils.getUserStatSettings();
        userStatistic.setStartTime(Calendar.getInstance().getTimeInMillis());
        BCUtils.saveUserStatSettings(userStatistic);
    }

    @OnClick(R.id.btn_pause_tracking)
    public void onClickPauseTracking() {
        if (getString(R.string.btn_pause).contentEquals(pauseTracking.getText())) {
            gisController.pauseTracking();
            BC_TrackingNotification.notify(this, PAUSE);
            pauseTracking.setText(R.string.btn_resume);
            BCUtils.saveTrackingStatus(PAUSE);
            Drawable img = getResources().getDrawable(R.drawable.ic_resume);
            pauseTracking.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            gisController.startTracking();
            BC_TrackingNotification.notify(this, TRACKING);
            pauseTracking.setText(R.string.btn_pause);
            BCUtils.saveTrackingStatus(TRACKING);
            Drawable img = getResources().getDrawable(R.drawable.ic_pause);
            pauseTracking.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }

    }

    @OnClick(R.id.btn_stop_tracking)
    public void onClickStopTracking() {
        gisController.stopTracking();
        trackingMenuAction.setVisibility(View.GONE);
        mFabShowStat.setVisibility(View.INVISIBLE);
        BCUtils.saveTrackingStatus(STOP);
    }

    //update UI only
    @Subscribe
    public void onTrackingStatusChanged(BCTrackingStatusChangedEvent event) {
        switch (event.getTrackingStatus()) {
            case TRACKING:
                pauseTracking.setText(R.string.btn_pause);
                Drawable img = getResources().getDrawable(R.drawable.ic_pause);
                pauseTracking.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                break;
            case PAUSE:
                pauseTracking.setText(R.string.btn_resume);
                Drawable img2 = getResources().getDrawable(R.drawable.ic_resume);
                pauseTracking.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
                break;
            case STOP:
                trackingMenuAction.setVisibility(View.GONE);
                mFabShowStat.setVisibility(View.INVISIBLE);
                BC_TrackingNotification.cancel(this);
                saveTrack();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NOTI_STOP_TRACKING.equals(intent.getStringExtra(NOTIFICATION_KEY))) {
            trackingMenuAction.setVisibility(View.GONE);
            mFabShowStat.setVisibility(View.INVISIBLE);
            saveTrack();
        }
    }

    private void saveTrack() {
        try {
            String trackingTripId = BCUtils.getLastEditedTrip();
            BCTrip trip = (new BCTripDAO(trackingTripId)).findById(trackingTripId);
            trip.setTimestamp((new Date()).getTime());
            trip.setTripZoom(BC_Helper.getZoomLevelByScale(mMapView.getMapScale()));
            List<BCGeometry> geometries = mergeTrackingGeometries();
            BC_TripsDBHelper.saveLocalTrip(trip, geometries, false);
            BC_TripsDBHelper.createInstance(DB_TRACKING_TEMP).dropDatabse();
            GraphicsOverlay graphicsOverlay = gisController.getTripLayerById(trackingTripId);
            if (graphicsOverlay != null) {
                List<Graphic> graphics = gisController.getSketchGraphicsOverlay().getGraphics();
                for (Graphic graphic : graphics) {
                    Graphic clone = new Graphic();
                    clone.setGeometry(graphic.getGeometry());
                    clone.setSymbol(graphic.getSymbol());
                    clone.setZIndex(graphic.getZIndex());
                    clone.getAttributes().putAll(graphic.getAttributes());
                    clone.getAttributes().put(ATTR_TRIP_ID, trackingTripId);
                    clone.getAttributes().put(ATTR_TRIP_NAME, trip.getName());
                    graphicsOverlay.getGraphics().add(clone);
                }
                gisController.safeClearSketchLayer();
            }
            BCUtils.saveTrackingStatus(STOP);
            BCUtils.saveTrackingTrip("");
            BCUtils.resetUserStatisticValue();
            drawPinnedTrips();
            Toast.makeText(this, R.string.msg_saved_track_successfully, Toast.LENGTH_LONG).show();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.msg_saved_track_failed, Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region DRAWING
    @Override
    public void displayEditGeometryFragment(BCGeometry wp) {
        BCAddGeometryFragment fragment;

        if (StringUtils.isNotNullOrEmpty(currentSelectTrip)) {
            BCTripInfo tripInfo = BCTripInfoDBHelper.get(currentSelectTrip);
            fragment = BCAddGeometryFragment.newInstance(this, this, tripInfo.getId(), wp, getSupportFragmentManager(), gisController);
            onActivityInteractionListener = fragment;
        } else {
            fragment = BCAddGeometryFragment.newInstance(this, this, "", wp, getSupportFragmentManager(), gisController);
            onActivityInteractionListener = fragment;
        }

        fragmentHelper.addFragment(this, fragment, BCAddGeometryFragment.TAG, R.id.fragment_container);

        isEditGeometryFragmentDisplayed = true;
        mFabAddNew.animate().translationX(400);
    }

    @Override
    public void closeEditGeometryFragment() {
        gisController.clearSketchEditor();
        gisController.resetSketchEditor();
        onActivityInteractionListener.showGraphic();

        fragmentHelper.removeFragmentByTag(this, Arrays.asList(BCAddGeometryFragment.TAG, BCAddWayPointFragment.TAG));

        isEditGeometryFragmentDisplayed = false;

        mFabAddNew.animate().translationX(0);

        gisController.setDrawMode(BC_DRAW_MODE.NONE);

        mAppBarLayout.setVisibility(View.GONE);
        mLnSearchView.setVisibility(View.VISIBLE);
        searchViewLayoutWithAnimation(true);

        if (BCUtils.getTrackingStatus() == STOP) {
            currentSelectTrip = "";
        }

        gisController.configTouchOnMap(userSettingsViewModel.getCurrentUserSettings().getValue());

        renderUIBySettings(userSettingsViewModel.getCurrentUserSettings().getValue());
    }

    @Override
    public void onListFragmentInteraction(BCSketchControllerFragment.SketchAction action) {
        final SketchEditor editor = this.gisController.sketchEditor;
        switch (action) {
            case CLOSE:
                if ((editor.getGeometry() == null || editor.getGeometry().isEmpty()) && tempGeometries.size() <= 0) {
                    editor.stop();
                    closeSketchControllerFragment();
                } else {
                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    saveSketchToTrip();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    editor.stop();
                                    closeSketchControllerFragment();
                                    break;
                            }
                        }
                    };
                    BCAlertDialogHelper.showAlertWithConfirm(this, BCDialogType.SAVE_DRAWING, onClickListener, null, null);
                }
                break;
            case OPEN_COLOR_PICKER:
                if (!isColorPickerFragmentDisplayed) {
                    displayColorPickerFragment();
                } else {
                    closeColorPickerFragment();
                }
                break;
            case UNDO:
                gisController.undo();
                break;
            case REDO:
                gisController.redo();
                break;
            case SAVE:
                saveSketchToTrip();
                break;
            case ERASER:
                if (editor.isSketchValid()) {
                    BCGeometry geometry = createGeometryFromSketchEditor(editor);
                    tempGeometries.add(geometry);
                    gisController.draw(new BCTripInfo(), geometry, gisController.getSketchGraphicsOverlay());
                    editor.stop();
                    gisController.configTouchOnMap(userSettingsViewModel.getCurrentUserSettings().getValue());
                    EventBus.getDefault().post(new BCSketchEditorChangeEvent(false, false, tempGeometries));
                }
                //mFabAddNewDraw.setImageResource(R.drawable.ic_add_black_24dp);
                break;
            case RESET:
                editor.stop();
                tempGeometries.clear();
                gisController.getSketchGraphicsOverlay().getGraphics().clear();
                EventBus.getDefault().post(new BCSketchEditorChangeEvent(false, false, tempGeometries));
                mFabAddNewDraw.setImageResource(R.drawable.ic_add_black_24dp);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoveGraphicOnSketch(BCRemoveGraphicEvent bcRemoveGraphicEvent) {
        if (bcRemoveGraphicEvent.idx >= 0) {
            this.tempGeometries.remove(bcRemoveGraphicEvent.idx);
            gisController.getSketchGraphicsOverlay().getGraphics().remove(bcRemoveGraphicEvent.idx);
            EventBus.getDefault().post(new BCSketchEditorChangeEvent(false, false, tempGeometries));
        }
        mMapView.getCallout().dismiss();
    }

    private void saveSketchToTrip() {
        boolean isCreateNew = gisController.drawMode == BC_DRAW_MODE.SKETCH;
        SketchEditor editor = gisController.sketchEditor;
        if (editor.isSketchValid() && editor.getGeometry() != null && !editor.getGeometry().isEmpty()) {
            BCGeometry bcGeometry = createGeometryFromSketchEditor(editor);
            tempGeometries.add(bcGeometry);
            gisController.draw(new BCTripInfo(), bcGeometry, gisController.getSketchGraphicsOverlay());
        }

        if (BCUtils.getTrackingStatus() != STOP) {
            BCGeometryDAO tempGeometryDAO = new BCGeometryDAO(DB_TRACKING_TEMP);
            tempGeometryDAO.insertOrUpdate(tempGeometries);

            for (Graphic graphic : gisController.getSketchGraphicsOverlay().getGraphics()) {
                graphic.getAttributes().put(TripUtils.ATTR_SAVED, true);
            }

            editor.stop();
            closeSketchControllerFragment();
            mMapView.getCallout().dismiss();
            dismissProgress();

        } else if (isCreateNew) {
            initEditGeometryToolbar(getString(R.string.edit_geometry));
            findViewById(R.id.rv_sketch_controller).setVisibility(View.GONE);
            BCTripInfo tripInfo = BCTripInfoDBHelper.getLastEditedTrip(BCUtils.getCurrentUser().getUserName());
            if (tripInfo != null) currentSelectTrip = tripInfo.getId();
            displayEditGeometryFragment(null);
        }
    }

    @NonNull
    private BCGeometry createGeometryFromSketchEditor(SketchEditor editor) {
        Geometry geometry = editor.getGeometry();
        BCGeometry bcGeometry = new BCGeometry();
        bcGeometry.setId(UUID.randomUUID().toString());
        bcGeometry.setType(editor.getGeometry().getGeometryType().toString().toLowerCase());
        bcGeometry.setColor(gisController.selectedColor);
        bcGeometry.setGeoJSON(geometry.toJson());

        return bcGeometry;
    }

    @Override
    public void onColorPick(int color) {
        this.gisController.setSelectedColor(color);
        closeColorPickerFragment();
    }

    /**
     * On Change Sketch Editor
     *
     * @param item sketch type
     */
    @Override
    public void onListFragmentInteraction(BCSketchDrawTypeContent.SketchDrawTypeItem item) {
        Log.d(TAG, String.format("On Change Sketch Editor: %s", item.content));
        mFabAddNewDraw.setImageResource(item.idActive);
        closeDrawPickerFragment();

        SketchEditor editor = gisController.sketchEditor;

        if (editor.isSketchValid() && editor.getGeometry() != null && !editor.getGeometry().isEmpty()) {
            BCGeometry bcGeometry = createGeometryFromSketchEditor(editor);
            gisController.draw(new BCTripInfo(), bcGeometry, gisController.getSketchGraphicsOverlay());
            tempGeometries.add(bcGeometry);
        }
        gisController.setSketchEditorMode(item.content);
    }
    //endregion

    //region EDIT TRIP
    public void onEditGeometryButtonClicked(View view) {
        try {
            mMapView.getCallout().dismiss();
            final Graphic graphic = (Graphic) view.getTag();

            String geoId = graphic.getAttributes().get(ATTR_GEO_ID).toString();
            String tripId = graphic.getAttributes().get(ATTR_TRIP_ID).toString();
            final BCGeometry editingGeo = (new BCGeometryDAO(tripId)).findById(geoId);
            editingGeo.setGraphic(graphic);
            Geometry editingGeometry = Geometry.fromJson(editingGeo.getGeoJSON());
            initEditGeometryToolbar(getString(R.string.edit_geometry));
            if (isWaypoint(editingGeo, editingGeometry.getGeometryType())) {
                Disposable disposable = gisController.setSketchEditorModeForWaypoint(editingGeo.getImageUrl() + ".svg")
                        .subscribe(new Consumer<PictureMarkerSymbol>() {

                            @Override
                            public void accept(PictureMarkerSymbol pictureMarkerSymbol) {
                                //Hide current graphic if it is a waypoint
                                graphic.setVisible(false);
                                displayWayPointFragment(editingGeo);
                                gisController.startSketchEditorWithPicture(pictureMarkerSymbol, graphic.getGeometry().getExtent().getCenter());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                Log.e(TAG, throwable.getMessage());
                            }
                        });
                compositeDisposable.add(disposable);
            } else {
                displayEditGeometryFragment(editingGeo);
            }
            currentSelectTrip = tripId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDeleteGeometryButtonClicked(final View view) {
        try {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        Graphic graphic = (Graphic) view.getTag();

                        String tripId = (String) graphic.getAttributes().get(ATTR_TRIP_ID);
                        String geoId = (String) graphic.getAttributes().get(ATTR_GEO_ID);

                        GraphicsOverlay graphicsOverlay = gisController.getTripLayerById(tripId);
                        graphicsOverlay.getGraphics().remove(graphic);

                        new BCGeometryDAO(tripId).delete(geoId);

                        BCTripInfo tripInfo = BCTripInfoDBHelper.get(tripId);
                        tripInfo.setTimestamp(Calendar.getInstance().getTimeInMillis());
                        tripInfo.setTripStatus(TripStatus.OUTDATE_LOCAL);
                        BCTripInfoDBHelper.update(tripInfo);
                        edit_trip_layout.setVisibility(View.GONE);
                        gisController.drawMode = BC_DRAW_MODE.NONE;

                        mMapView.getCallout().dismiss();
                    } else {
                        dialog.cancel();
                    }
                }
            };
            BCAlertDialogHelper.showAlertWithConfirm(this, BCDialogType.CONFIRM_DELETE, onClickListener, null, getString(R.string.confirm_delete_geo));
        } catch (Exception ex) {
            Logger.e(TAG, "delete geometry error", ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFoundGeometryToEdit(final BC_FoundGeometryToEditEvent event) {
        final Graphic graphic = event.getGraphic();
        if (isNoGraphicSelected(graphic)) {
            return;
        }

        String tripId = graphic.getAttributes().get(ATTR_TRIP_ID) != null ? graphic.getAttributes().get(ATTR_TRIP_ID).toString() : "";
        final BCTripInfo tripInfo = BCTripInfoDBHelper.get(tripId);

        try {
            String geoId = (String) graphic.getAttributes().get(ATTR_GEO_ID);
            final BCGeometry bcGeometry = (new BCGeometryDAO(tripId)).findById(geoId);

            BCSettings settings = userSettingsViewModel.getCurrentUserSettings().getValue();
            if (settings != null) {
                View.OnClickListener onBtnEditClickedListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setTag(graphic);
                        onEditGeometryButtonClicked(v);
                    }
                };
                View.OnClickListener onBtnDeleteClickedListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setTag(graphic);
                        onDeleteGeometryButtonClicked(v);
                    }
                };
                View view = View.inflate(BCHomeActivity.this, R.layout.geometry_details_layout, null);
                BC_GeometryInfoViewBuilder builder = new BC_GeometryInfoViewBuilder();
                builder.withView(view)
                        .withContext(BCHomeActivity.this)
                        .setOnBtnDeleteClickedListener(onBtnDeleteClickedListener)
                        .setOnBtnEditClickedListener(onBtnEditClickedListener)
                        .withDecimalFormat(new DecimalFormat(BCUtils.DECIMAL_PATTERN))
                        .withBCGeometry(bcGeometry)
                        .withTripName(tripInfo.getName())
                        .withLogo((ImageView) view.findViewById(R.id.imv_geometry))
                        .withSettings(settings);

                gisController.drawCallout(graphic, builder.build(), event.getTapPoint());
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean isNoGraphicSelected(Graphic graphic) {
        return graphic == null;
    }
    //endregion

    //region ADD WAYPOINT
    private void initEditGeometryToolbar(String title) {
        makeStatusBarNotTransparent();
        mAppBarLayout.setVisibility(View.VISIBLE);
        mToolBar.setNavigationIcon(R.drawable.ic_go_back_left_arrow);
        mToolBar.setTitle(title);
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolBar.setNavigationOnClickListener(this);
        mWaypointDoneButton.setVisibility(View.VISIBLE);
        searchViewLayoutWithAnimation(false);
    }

    @OnClick(R.id.fab_new_way_point_my_location)
    public void onNewWayPointMyLocationClicked() {
        if (BCUtils.getTrackingStatus() != STOP) {
            beginAddWayPoint(true);
            return;
        }

        if (StringUtils.isNotNullOrEmpty(currentSelectTrip)) {
            BCTripInfo tripInfo = BCTripInfoDBHelper.get(currentSelectTrip);
            if (!BCUtils.getCurrentUser().getUserName().equals(tripInfo.getOwnerId())) {
                Toast.makeText(this, R.string.cannot_edit_shared_trip, Toast.LENGTH_SHORT).show();
                closeFABMenu();
                return;
            }
        }

        beginAddWayPoint(true);
    }

    @OnClick(R.id.fab_new_way_point)
    public void onNewWayPointClicked() {
        if (BCUtils.getTrackingStatus() != STOP) {
            beginAddWayPoint(false);
            return;
        }

        if (StringUtils.isNotNullOrEmpty(currentSelectTrip)) {
            BCTripInfo tripInfo = BCTripInfoDBHelper.get(currentSelectTrip);
            if (!BCUtils.getCurrentUser().getUserName().equals(tripInfo.getOwnerId())) {
                Toast.makeText(this, R.string.cannot_edit_shared_trip, Toast.LENGTH_SHORT).show();
                closeFABMenu();
                return;
            }
        }

        beginAddWayPoint(false);
    }

    @OnClick(R.id.done_button)
    public void onDoneButtonClicked() {
        if (isEditGeometryFragmentDisplayed) {
            if (BCUtils.getTrackingStatus() != STOP) {
                saveGeometriesToTrip();
            } else {
                if (StringUtils.isNullOrEmpty(currentSelectTrip)) {
                    BCAlertDialogHelper.showAlertWithConfirm(this, BCDialogType.NEED_SELECT_TRIP,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToSelectTripActivity(ACTIVITY_SELECT_TRIP_AND_SAVE_RESULT);
                                }
                            }, null, "");
                } else {
                    if (gisController.drawMode == BC_DRAW_MODE.GET_TRIP_FOR_TRACKING) {
                        if (StringUtils.isNotNullOrEmpty(currentSelectTrip)) {
                            startTracking(currentSelectTrip);
                        } else {
                            Toast.makeText(this, "No trip selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        saveGeometriesToTrip();
                    }
                }
            }
        }
    }

    private void goToSelectTripActivity(int activityCode) {
        Intent intent = new Intent(this, BCSelectTripActivity.class);
        startActivityForResult(intent, activityCode);
    }

    private void saveGeometriesToTrip() {
        if (gisController.drawMode == BC_DRAW_MODE.SKETCH) {
            onActivityInteractionListener.onSaveGeometries(tempGeometries, BC_Helper.getZoomLevelByScale(mMapView.getMapScale()));
        } else {
            onActivityInteractionListener.onSaveGeometry();
        }
    }

    private void beginAddWayPoint(boolean myLocation) {
        closeFABMenu();
        if (myLocation) {
            if (!isGPSEnabled()) {
                String message = getResources().getString(R.string.enable_gps);
                BCAlertDialogHelper.showErrorAlert(this, BCErrorType.GPS_NOT_ENABLE, message);
                return;
            }
            showProgress("Preparing waypoints library...");
            gisController.navigateToMyLocation(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        BCTripInfo tripInfo = BCTripInfoDBHelper.getLastEditedTrip(BCUtils.getCurrentUser().getUserName());
                        if (tripInfo != null) currentSelectTrip = tripInfo.getId();
                        final Point p = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                        doLoadWaypointSVG(p, "point.svg");
                    } else {
                        Log.e(TAG, "Fail to get location");
                        Toast.makeText(BCHomeActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        dismissProgress();
                        beginAddWayPoint(false);
                    }
                }
            }, mMapView.getMapScale());
        } else {
            showProgress("Preparing waypoints library...");
            final Point p = gisController.getMapView().getVisibleArea().getExtent().getCenter();
            doLoadWaypointSVG(p, "point.svg");
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void doLoadWaypointSVG(final Point point, String svgName) {
        Disposable disposable = gisController.setSketchEditorModeForWaypoint(svgName)
                .subscribe(onGetWaypointSVGSuccess(point), onGetWaypointSVGFailed());
        compositeDisposable.add(disposable);
    }

    private Consumer<PictureMarkerSymbol> onGetWaypointSVGSuccess(final Point point) {
        return new Consumer<PictureMarkerSymbol>() {
            @Override
            public void accept(PictureMarkerSymbol pictureMarkerSymbol) {
                renderAddWaypoint(pictureMarkerSymbol, point);
            }
        };
    }

    private Consumer<Throwable> onGetWaypointSVGFailed() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.e(TAG, "Error to load waypoint", throwable);
                Toast.makeText(BCHomeActivity.this, "Error to load waypoint library. Please try again!!!", Toast.LENGTH_SHORT).show();
                dismissProgress();
            }
        };
    }

    private void renderAddWaypoint(PictureMarkerSymbol pictureMarkerSymbol, Point p) {
        initEditGeometryToolbar(getString(R.string.add_way_point));
        displayWayPointFragment(null);
        gisController.startSketchEditorWithPicture(pictureMarkerSymbol, p);
        dismissProgress();
    }

    @Override
    public void displayWayPointFragment(BCGeometry wp) {
        BCAddWayPointFragment bcAddWayPointFragment;

        if (StringUtils.isNotNullOrEmpty(currentSelectTrip)) {
            bcAddWayPointFragment = displayWaypointFragmentWithTripId(wp, currentSelectTrip);
        } else {
            String lastEditTrip = BCUtils.getLastEditedTrip();
            if (StringUtils.isNotNullOrEmpty(lastEditTrip)) {
                bcAddWayPointFragment = displayWaypointFragmentWithTripId(wp, lastEditTrip);
            } else {
                bcAddWayPointFragment = displayWaypointFragmentWithTripId(wp, "");
            }
        }

        fragmentHelper.addFragment(this, bcAddWayPointFragment, BCAddWayPointFragment.TAG, R.id.fragment_container);

        isEditGeometryFragmentDisplayed = true;
        mFabAddNew.animate().translationX(400);
    }

    private BCAddWayPointFragment displayWaypointFragmentWithTripId(BCGeometry wp, String tripId) {
        BCAddWayPointFragment bcAddWayPointFragment;
        if (StringUtils.isNotNullOrEmpty(tripId)) {
            BCTripInfo tripInfo = BCTripInfoDBHelper.get(tripId);
            bcAddWayPointFragment = BCAddWayPointFragment.newInstance(this, this, tripInfo.getId(), wp, getSupportFragmentManager(), gisController);
        } else {
            bcAddWayPointFragment = BCAddWayPointFragment.newInstance(this, this, "", wp, getSupportFragmentManager(), gisController);
        }
        onActivityInteractionListener = bcAddWayPointFragment;
        return bcAddWayPointFragment;
    }

    @Override
    public void onSelectTripClicked(int activityResult) {
        goToSelectTripActivity(activityResult);
    }

    @Override
    public void onSaveGeometriesCompleted() {
        if (gisController.drawMode == BC_DRAW_MODE.SKETCH) {
            closeSketchControllerFragment();
            findViewById(R.id.rv_sketch_controller).setVisibility(View.VISIBLE);
            tempGeometries.clear();
        }
        closeEditGeometryFragment();
        drawPinnedTrips();
    }
    //endregion

    //region DOWNLOAD MAP
    @Override
    public void onClose() {
        closeDownloadMapFragment();
    }

    @Override
    public void onShowTutorial() {
        BCAlertDialogHelper.showDownloadMapTutorial(this, null);
    }

    @Override
    public void onToggleDrawGrid(int gridSize) {
        onReset();
        gisController.enterMode(BC_DRAW_MODE.SELECT_TILES, gridSize, false, true);
    }

    @Override
    public void onReset() {
        gisController.safeClearTempLayer();
        gisController.pickedTilesList.clear();
        gisController.drawMode = BC_DRAW_MODE.NONE;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onDownloadMapStart(double availableSpace, double requireSpace, final int maxZoom) {
        BCAlertDialogHelper.showAlertDialogBuider(this, BCAlertType.INFORMATION, String.format("Available Space:%.2f MB\nRequired Space: ~%.2f MB", availableSpace, requireSpace), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gisController.stopDraw(maxZoom);
                closeDownloadMapFragment();
                isDownloadingMap = true;
                showProgress("Prepare to download");
            }
        }, null);

    }
    //endregion

    //region ButterKnife Event
    @OnClick(R.id.fab_location)
    public void onFabLocationClicked() {
        if (isGPSEnabled()) {
            gisController.navigateToMyLocationByArcgis();
        } else {
            String message = getResources().getString(R.string.enable_gps);
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.GPS_NOT_ENABLE, message);
        }
    }

    @OnClick(R.id.fab_stat)
    public void onFabStatClicked() {
        presenter.onBtnStatsClicked();
    }

    @OnClick(R.id.fab_download_map)
    public void onDownloadMapFabClicked() {
        if (isPurchasedUser()) {
            closeFABMenu();
            //if vector map
            //mention coming soon.
            if (gisController.isVectorMap(mMapView)) {
                Toast.makeText(this, "Looking for Vector Maps to download . . .", Toast.LENGTH_LONG).show();

                displayVectorMapDownload();
            } else {
                if (!BCUtils.isSharedPrefContainsKey(KEY_SHOW_DOWNLOAD_TUTORIAL) || BCUtils.getSharedPrefereneceBoolean(KEY_SHOW_DOWNLOAD_TUTORIAL)) {
                    BCAlertDialogHelper.showDownloadMapTutorial(this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayDownloadMapFragment();
                        }
                    });
                    if (!BCUtils.isSharedPrefContainsKey(KEY_SHOW_DOWNLOAD_TUTORIAL)) {
                        BCUtils.saveSharedPreferences(KEY_SHOW_DOWNLOAD_TUTORIAL, false);
                    }
                } else {
                    displayDownloadMapFragment();
                }
            }
        } else {
            BCAlertDialogHelper.showMembershipAlert(this, BCDialogType.MEMBERSHIP_REQUIRE, "");
        }
    }

    @OnClick(R.id.fab_create_draw)
    public void onCreateDrawClick() {
        closeFABMenu();
        if (!isSketchFragmentDisplayed)
            displaySketchControllerFragment();
    }

    @OnClick(R.id.fab_add_new_draw)
    public void onAddNewDrawType() {
        if (!isAddNewDrawDisplayed) {
            displayDrawPickerFragment();
        } else {
            closeDrawPickerFragment();
        }
    }
    //endregion

    //region IBCHomeActivityView Impl
    @Override
    public void displayStatsFragment(BCStatFragmentPresenterImpl.STAT_MODE statMode) {
        AppCompatDialogFragment fragment = BCStatFragment.newInstance(statMode);
        fragmentHelper.addFragment(this, fragment, BCStatFragment.TAG, R.id.fragment_container);
    }

    @Override
    public void closeStatsFragment() {
        fragmentHelper.removeFragmentByTag(this, BCStatFragment.TAG);
    }

    @Override
    public void displayStatsSettingsFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BCStatsSettingsFragment.TAG);
        if (fragment == null || !fragment.isVisible()) {
            fragment = BCStatsSettingsFragment.newInstance();
            fragmentHelper.addFragment(this, fragment, BCStatsSettingsFragment.TAG, R.id.fragment_stats);
        }
    }

    @Override
    public void closeStatsSettingsFragment() {
        fragmentHelper.removeFragmentByTag(this, BCStatsSettingsFragment.TAG);
    }

    @Override
    public void displayCompassFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CompassFragment.TAG);
        if (fragment == null || !fragment.isVisible()) {
            fragment = CompassFragment.newInstance();
            fragmentHelper.addFragment(this, fragment, CompassFragment.TAG, R.id.fragment_compass);
        }
    }

    @Override
    public void closeCompassFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CompassFragment.TAG);
        if (fragment instanceof CompassFragment) {
            ((CompassFragment) fragment).stopCompass();
        }
        fragmentHelper.removeFragmentByTag(this, CompassFragment.TAG);
    }

    @Override
    public void displayQuickAccessFragment() {
        bcTripInfoList = BCTripInfoDBHelper.loadPinnedTrip();
        BCQuickAccessFragment quickAccessFragment;
        quickAccessFragment = (BCQuickAccessFragment) getSupportFragmentManager().findFragmentByTag(BCQuickAccessFragment.TAG);
        if (quickAccessFragment == null) {
            quickAccessFragment = BCQuickAccessFragment.newInstance(userSettingsViewModel.getCurrentUserSettings().getValue());
            quickAccessFragment.setTogglePinnedTripListener(this);
            quickAccessFragment.setQuickAccessFragmentInteractionListener(this);
        }
        fragmentHelper.addFragment(this, quickAccessFragment, BCQuickAccessFragment.TAG, R.id.fragment_quick_access);
        isQuickAccessFragmentDisplayed = true;
    }

    @Override
    public void closeQuickAccessFragment() {
        fragmentHelper.removeFragmentByTag(this, BCQuickAccessFragment.TAG);
        isQuickAccessFragmentDisplayed = false;
    }

    @Override
    public void displaySketchControllerFragment() {
        BCSketchControllerFragment sketchControllerFragment;
        sketchControllerFragment = (BCSketchControllerFragment) getSupportFragmentManager().findFragmentByTag(BCSketchControllerFragment.TAG);

        if (sketchControllerFragment == null) {
            sketchControllerFragment = BCSketchControllerFragment.newInstance();
        }

        fragmentHelper.addFragment(this, sketchControllerFragment, BCSketchControllerFragment.TAG, R.id.fragment_sketch_container);

        isSketchFragmentDisplayed = true;

        mFabAddNew.setEnabled(false);
        mFabAddNew.animate().rotation(0).translationX(300).setStartDelay(500).setDuration(300);

        mFabAddNewDraw.animate().translationY(0).setDuration(300);

        searchViewLayoutWithAnimation(false);

        //enter draw mode
        if (gisController.selectedSketchCreateMode != null) {
            gisController.sketchEditor.start(gisController.selectedSketchCreateMode);
        }

        gisController.setDrawMode(BC_DRAW_MODE.SKETCH);
    }

    @Override
    public void closeSketchControllerFragment() {
        fragmentHelper.removeFragmentByTag(this, BCSketchControllerFragment.TAG);

        isSketchFragmentDisplayed = false;

        mFabAddNew.setEnabled(true);
        mFabAddNew.animate().rotation(0).translationX(0).setDuration(300);
        mFabAddNewDraw.animate().translationY(400).setDuration(300);

        if (isColorPickerFragmentDisplayed) {
            closeColorPickerFragment();
        }

        if (isAddNewDrawDisplayed) {
            closeDrawPickerFragment();
        }

        tempGeometries.clear();

        if (BCUtils.getTrackingStatus() != STOP) {
            for (Graphic graphic : gisController.getSketchGraphicsOverlay().getGraphics()) {
                if (!graphic.getAttributes().containsKey(TripUtils.ATTR_SAVED)) {
                    gisController.getSketchGraphicsOverlay().getGraphics().remove(graphic);
                }
            }
        } else {
            gisController.getSketchGraphicsOverlay().getGraphics().clear();
        }

        gisController.setDrawMode(BC_DRAW_MODE.NONE);
        searchViewLayoutWithAnimation(true);

        gisController.configTouchOnMap(userSettingsViewModel.getCurrentUserSettings().getValue());
    }

    @Override
    public void displayColorPickerFragment() {
        BCColorPickerFragment colorPickerFragment;

        colorPickerFragment = (BCColorPickerFragment) getSupportFragmentManager().findFragmentByTag(BCColorPickerFragment.TAG);
        if (colorPickerFragment == null) {
            colorPickerFragment = BCColorPickerFragment.newInstance();
        }

        fragmentHelper.addFragment(this, colorPickerFragment, BCColorPickerFragment.TAG, R.id.fragment_color_picker_container);

        isColorPickerFragmentDisplayed = true;
    }

    @Override
    public void closeColorPickerFragment() {
        fragmentHelper.removeFragmentByTag(this, BCColorPickerFragment.TAG);
        isColorPickerFragmentDisplayed = false;
    }

    @Override
    public void displayDrawPickerFragment() {
        BCDrawTypePickerFragment bcDrawTypePickerFragment;
        bcDrawTypePickerFragment = (BCDrawTypePickerFragment) getSupportFragmentManager().findFragmentByTag(BCDrawTypePickerFragment.TAG);

        if (bcDrawTypePickerFragment == null) {
            bcDrawTypePickerFragment = BCDrawTypePickerFragment.newInstance(1);
        }

        fragmentHelper.addFragment(this, bcDrawTypePickerFragment, BCDrawTypePickerFragment.TAG, R.id.fragment_draw_type_picker_container);

        mFabAddNewDraw.animate().rotation(45);

        isAddNewDrawDisplayed = true;
    }

    @Override
    public void closeDrawPickerFragment() {

        fragmentHelper.removeFragmentByTag(this, BCDrawTypePickerFragment.TAG);

        mFabAddNewDraw.animate().rotation(0);

        isAddNewDrawDisplayed = false;
    }

    @Override
    public void displayVectorMapDownload() {
        this.startSupportActionMode(mVectorDownloadActionModeCallback);
    }

    @Override
    public void displayDownloadMapFragment() {
        mLnSearchView.animate().translationY(-300);
        mFabAddNew.animate().translationX(300);
        mFabQuickAccess.animate().translationX(300);

        fragmentHelper.addFragment(
                this,
                BCDownloadMapFragment.newInstance(gisController),
                BCDownloadMapFragment.TAG,
                R.id.fragment_container);

        isDownloadMapFragmentDisplayed = true;
    }

    @Override
    public void closeDownloadMapFragment() {
        mLnSearchView.animate().translationY(0);
        mFabAddNew.animate().translationX(0);
        mFabQuickAccess.animate().translationX(0);

        fragmentHelper.removeFragmentByTag(this, BCDownloadMapFragment.TAG, new Runnable() {
            @Override
            public void run() {
                onReset();
            }
        });
    }

    @Override
    public void showLayersIcon(boolean isOffline) {
        if (isOffline) {
            mFabQuickAccess.setImageResource(R.drawable.ic_layers_clear);
        } else {
            mFabQuickAccess.setImageResource(R.drawable.ic_layers);
        }
        drawerSwitch.setChecked(isOffline);
        if (!isLoggedIn()) {
            drawerSwitch.setEnabled(false);
        }
    }

    @Override
    public void updateZoomLevelText() {
        tvZoomLevel.setText(String.valueOf(BC_Helper.getZoomLevelByScale(mMapView.getMapScale())));
    }

    @Override
    public void setUpFloatingButton() {
        mFabQuickAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isQuickAccessFragmentDisplayed) {
                    displayQuickAccessFragment();
                } else {
                    closeQuickAccessFragment();
                }
            }
        });

        mFabZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gisController.zoomOut(mFabZoomOut);
            }
        });

        mFabZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gisController.zoomIn(mFabZoomIn);
            }
        });
        //end
    }

    @Override
    public void setupNavigation() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }
    //endregion

    //region OnQuickAccessFragmentInteractionListener
    @Override
    public void onClickTripItem() {
        if (BCUtils.getCurrentUser() != null) {
            Intent intent = new Intent(this, BCTripsActivity.class);
            startActivity(intent);
        } else {
            BCAlertDialogHelper.showNeedLoginAlert(this, this);
        }
    }

    @Override
    public void onClickMapItem() {
        if (BCUtils.getCurrentUser() == null) {
            BCAlertDialogHelper.showNeedLoginAlert(this, this);
        } else {
            Intent intent = new Intent(this, BCMapSourceActivity.class);
            Point center = BCUtils.getMapCenterPoint(mMapView);
            intent.putExtra(KEY_CENTER_POINT, center.toJson());
            startActivity(intent);
        }
    }

    @Override
    public void switchShowCompass(final boolean isShow) {
        userSettingsViewModel.setShowCompass(isShow,
                new Runnable() {
                    @Override
                    public void run() {
                        if (isShow) {
                            displayCompassFragment();
                        } else {
                            closeCompassFragment();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Toast.makeText(BCHomeActivity.this, "Show compass got error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPinnedMapChanged(BCMap map) {
        BCUtils.saveLastMapPref(map);
        mMapViewModel.getCurrentName().postValue(map.getShortName());
    }

    @Override
    public void switchOfflineMode(final boolean isOffline) {
        userSettingsViewModel.setOffline(isOffline);
    }

    @Override
    public void navigateToPinnedTrip(BCTripInfo tripInfo) {
        try {
            List<BCGeometry> geometries = new BCGeometryDAO(tripInfo.getId()).getAll();
            if (geometries == null || geometries.size() <= 0) return;

            Point center = Geometry.fromJson(geometries.get(0).getGeoJSON()).getExtent().getCenter();
            gisController.navigateToLocationNoZoom(center);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

    }
    //endregion

    //region OnStatsFragmentListener
    @Override
    public void openStatsSettings() {
        closeStatsFragment();
        displayStatsSettingsFragment();
    }

    @Override
    public void onOpenMinimizedStatClicked() {
        closeStatsSettingsFragment();
        displayStatsFragment(MINI);
    }

    @Override
    public void onOpenMaximizedStatClicked() {
        closeStatsSettingsFragment();
        displayStatsFragment(EXPAND);
    }
    //endregion

    //region Theme
    @Override
    public void initializeData() {
        BaseActivityContracts.Presenter themePresenter = new PresenterImpl(this, new InteractorImpl());
        themePresenter.onInitializeData();
    }

    @Override
    public void onGetSettingsFromRepositorySuccess(BCSettings settings) {
        switch (BCSettings.THEME.values()[settings.getTheme().ordinal()]) {
            case DAY:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case NIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case AUTO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    public void onGetSettingsFromRepositoryFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region Private functions
    private Observer<BCMap> getLoadMapObservable() {
        return new Observer<BCMap>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgress("Loading map");
            }

            @Override
            public void onNext(BCMap bcMap) {
                loadMapToArcGisMap(bcMap);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
                dismissProgress();
            }

            @Override
            public void onComplete() {
                dismissProgress();
            }
        };
    }

    private void loadMap(final ObservableEmitter<BCMap> emitter) {
        for (Layer layer : map.getOperationalLayers()) {
            if (layer instanceof BC_OnlineMapLayer) {
                ((BC_OnlineMapLayer) layer).setStop(true);
            }
        }
        for (Layer layer : map.getBasemap().getBaseLayers()) {
            if (layer instanceof BC_OnlineMapLayer) {
                ((BC_OnlineMapLayer) layer).setStop(true);
            }
        }
        //what will happen if I eliminate this.
        //map = new ArcGISMap();

        BCUser user = getCurrentUser();
        if (user != null) {
            if (!isOffline()) {
                WebServiceCallBack<BCMapResponse> webServiceCallBack = new WebServiceCallBack<BCMapResponse>() {
                    @Override
                    public void onSuccess(BCMapResponse data) {
                        List<BCMap> maps = MapUtils.mapAll(data.getMapSources());
                        BCMap lastMap = MapUtils.getDefaultOrLastMap();
                        if (lastMap == null) {
                            for (int i = 0; i < maps.size(); i++) {
                                BCMap bcMap = maps.get(i);
                                if (bcMap.getShortName().equals("wwbcnvector")) {
                                    BCUtils.saveLastMapPref(bcMap);
                                    lastMap = bcMap;
                                    bcMap.setPinned(true);
                                    BCDatabaseHelper.save(bcMap);
                                    break;
                                }
                            }
                        }
                        assert lastMap != null;
                        emitter.onNext(lastMap);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        Toast.makeText(getApplicationContext(), "Error in load map", Toast.LENGTH_SHORT).show();
                        emitter.onComplete();
                    }
                };

                if (!MapUtils.isMapLocalExist()) {
                    BCApiService.getInstance().doLoadMapsByUserAsync(user.getUserName(), webServiceCallBack);
                } else {
                    BCMap lastMap = MapUtils.getDefaultOrLastMap();
                    emitter.onNext(lastMap);
                }
            } else {
                BCMap lastMap = MapUtils.getDefaultOrLastMap();
                emitter.onNext(lastMap);
            }

            map.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    if (map.getLoadStatus() == LoadStatus.LOADED) {
                        showProgress("Drawing trips");
                        drawPinnedTrips();
                        if (BCUtils.getTrackingStatus() != STOP || "STOP_TRACKING".equals(getIntent().getStringExtra("Notification"))) {
                            resumeTrackingUI();
                        }
                        emitter.onComplete();
                    } else if (map.getLoadError() != null) {

                        ArcGISRuntimeException exception = map.getLoadError();
                        Crashlytics.log(Log.ERROR, TAG, "ArcGisMap loading failure");
                        Crashlytics.logException(exception);
                        Toast.makeText(getApplicationContext(), "Error in load map", Toast.LENGTH_SHORT).show();
                        emitter.onComplete();
                    }
                }
            });
        } else {
            BCMap lastMap = MapUtils.getDefaultOrLastMap();
            emitter.onNext(lastMap);
            emitter.onComplete();
        }
    }

    private void resumeTrackingUI() {
        if (trackingMenuAction.getVisibility() != View.VISIBLE) {
            mFabShowStat.setVisibility(View.VISIBLE);
            trackingMenuAction.setVisibility(View.VISIBLE);

            //FIXME Don't know why it impact fragments
            if (BCUtils.getTrackingStatus() == TRACKING) {
                pauseTracking.setText(R.string.btn_pause);
                Drawable img = getResources().getDrawable(R.drawable.ic_pause);
                pauseTracking.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else if (BCUtils.getTrackingStatus() == PAUSE) {
                pauseTracking.setText(R.string.btn_resume);
                Drawable img = getResources().getDrawable(R.drawable.ic_resume);
                pauseTracking.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }
            if (!EventBus.getDefault().isRegistered(gisController)) {
                EventBus.getDefault().register(gisController);
            }
            gisController.navigateToMyLocationByArcgis();
            gisController.drawTrackingLines();

            if (NOTI_STOP_TRACKING.equals(getIntent().getStringExtra(NOTIFICATION_KEY))) {
                trackingMenuAction.setVisibility(View.GONE);
                mFabShowStat.setVisibility(View.INVISIBLE);
                saveTrack();
            }
        }
    }

    private void renderUIBySettings(BCSettings settings) {
        if (settings != null) {
            checkShowView(mFabLocation, !settings.isShowFullScreenMode());

            checkShowView(mFabQuickAccess, settings.isShowMapSources() && !settings.isShowFullScreenMode());
            checkShowView(tvZoomLevel, settings.isShowZoomNumber() && !settings.isShowFullScreenMode());
            checkShowView(mFabZoomIn, settings.isShowZoom() && !settings.isShowFullScreenMode());
            checkShowView(mFabZoomOut, settings.isShowZoom() && !settings.isShowFullScreenMode());
            checkShowView(mFabShowStat, settings.isShowStats() && !settings.isShowFullScreenMode());

            checkShowViewWithVisibilityGone(mLnSearchView, !settings.isShowFullScreenMode());

            checkShowCompassFragment(settings);

            renderMapBySettings(settings);

            showLayersIcon(settings.isOffline());
        }
    }

    private void renderMapBySettings(BCSettings settings) {
        BCMap lastMap = MapUtils.getDefaultOrLastMap();
        if (isNeedToReloadMap(settings, lastMap, isFirstLoad)) {
            mMapViewModel.getCurrentName().postValue(lastMap.getShortName());
        }
    }

    private boolean isNeedToReloadMap(BCSettings settings, BCMap lastMap, boolean isFirstLoad) {
        if (isFirstLoad) {
            return true;
        } else {
            if (mMapView.getMap() != null && mMapView.getMap().getBasemap() != null) {
                boolean currentOfflineSetting = drawerSwitch.isChecked();
                String currentMapName = mMapView.getMap().getBasemap().getName();
                return currentOfflineSetting != settings.isOffline() || !lastMap.getShortName().equals(currentMapName);
            } else {
                return true;
            }
        }
    }

    private void checkShowCompassFragment(BCSettings settings) {
        if (settings.isShowCompass()) {
            displayCompassFragment();
        } else {
            closeCompassFragment();
        }
    }

    private void checkShowView(View view, boolean isShowed) {
        if (isShowed) {
            showView(view);
        } else {
            hideView(view);
        }
    }

    private void checkShowViewWithVisibilityGone(View view, boolean isShowed) {
        if (isShowed) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void showView(View view) {
        view.setEnabled(true);
        view.animate().alpha(1);
    }

    private void hideView(View view) {
        view.setEnabled(false);
        view.animate().alpha(0);
    }

    //FIXME
    public void openSelectDestinationActivity() {
        Intent intent = new Intent(this, BCDestinationActivity.class);
        startActivity(intent);
    }

    private boolean isGPSEnabled() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void loadMembership() {
        if (BCUtils.getCurrentUser() != null) {
            BCMembershipIntentService.startActionGetMembership(this, BCUtils.getCurrentUser().getUserName());
        }
    }

    private boolean permissionCheck() {
        boolean permissionCheck =
                ContextCompat.checkSelfPermission(BCHomeActivity.this, permission[0])
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(BCHomeActivity.this, permission[1])
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(BCHomeActivity.this, permission[2])
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(BCHomeActivity.this, permission[3])
                        == PackageManager.PERMISSION_GRANTED;

        if (!permissionCheck) {
            // If permissions are not already granted, request permission from the user.
            ActivityCompat.requestPermissions(BCHomeActivity.this, permission, requestPermissionCode);
        }

        return permissionCheck;
    }

    private void checkExpiredDay(BCMembership membership) {
        long currentTime = new Date().getTime();
        if (membership.getValidTo() < currentTime || BCUtils.isSameDay(membership.getValidTo(), currentTime)) {
            BCAlertDialogHelper.showMembershipAlert(this, BCDialogType.MEMBERSHIP_EXPIRED, null);
        }
    }

    private void checkMembershipChanged(BCMembership oldMembership, BCMembership newMembership) {
        BCMembershipType currentType = BCMembershipType.getFromName(oldMembership.getMembershipType());
        BCMembershipType newType = BCMembershipType.getFromName(newMembership.getMembershipType());

        if (newType == null || currentType == newType)
            return;

        newMembership.setUserId(oldMembership.getUserId());
        newMembership.update();

        if (currentType == null || currentType.ordinal() < newType.ordinal()) {
            BCAlertDialogHelper.showMembershipAlert(this, BCDialogType.MEMBERSHIP_UPGRADE, newType.toString());
        } else if (currentType.ordinal() > newType.ordinal()) {
            BCAlertDialogHelper.showMembershipAlert(this, BCDialogType.MEMBERSHIP_DOWNGRADE, null);
        }
    }

    private void searchViewLayoutWithAnimation(boolean isShow) {
        if (isShow) {
            mLnSearchView.animate().translationY(0).setDuration(300);
        } else {
            mLnSearchView.animate().translationY(-400).setDuration(300);
        }
    }

    private void startTracking(String tripId) {
        mWaypointDoneButton.setText("DONE");
        gisController.drawMode = BC_DRAW_MODE.NONE;
        BC_TripsDBHelper.createInstance(DB_TRACKING_TEMP).dropDatabse();
        BCUtils.saveTrackingStatus(TRACKING);
        BCUtils.saveTrackingTrip(tripId);
        trackingMenuAction.setVisibility(View.VISIBLE);
        mFabShowStat.setVisibility(View.VISIBLE);
        pauseTracking.setText(getString(R.string.btn_pause));
        closeEditGeometryFragment();
        gisController.startTracking();
        BC_TrackingNotification.notify(this, TRACKING);
    }
    //endregion
}