package com.crittermap.backcountrynavigator.xe.ui.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController;
import com.crittermap.backcountrynavigator.xe.controller.constant.LayerType;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCGeometryDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTripDAO;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.share.SvgSoftwareLayerSetter;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivityFragmentsContracts.OnActivityInteractionListener;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.raizlabs.android.dbflow.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.DB_TRACKING_TEMP;
import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.FILE_ANDROID_ASSET_WAYPOINTS;
import static com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper.saveLocalTrip;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_ID;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_NAME;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_TYPE;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_GEO_URL;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_TRIP_ID;
import static com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils.ATTR_TRIP_NAME;
import static com.crittermap.backcountrynavigator.xe.service.trip.BCTrackingStatusChangedEvent.TrackingStatus.STOP;
import static com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCWaypointsPickerFragment.OnWaypointSelectedListener;

/**
 * Created by henry on 4/15/2018.
 */

public class BCAddWayPointFragment extends AppCompatDialogFragment implements OnWaypointSelectedListener, OnActivityInteractionListener {
    public static String TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCAddWayPointFragment";
    private static String LOG_TAG = "BCAddWayPointFragment";

    Unbinder mUnbinder;
    @BindView(R.id.way_point_name_txt)
    EditText mWayPointName;

    @BindView(R.id.search_trip_layout)
    RelativeLayout mSearchTripLayout;

    @BindView(R.id.search_trip_btn)
    Button mSearchTripBtn;

    @BindView(R.id.grid_waypoint_picker)
    GridLayout mGrid;

    HashMap<String, BCGeometry> waypoints = new HashMap<>();

    Context mContext;

    boolean isOpenWaypointsPicker = false;

    OnFragmentInteractionListener mListener;

    public BCGeometry currentWaypoint;
    FragmentManager fragmentManager;
    BCTripInfo trip;
    private BC_ArcGisController gisController;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BCAddWayPointFragment() {
    }

    public static BCAddWayPointFragment newInstance(Context context, OnFragmentInteractionListener listener, String tripId, BCGeometry wp, FragmentManager fragmentManager, BC_ArcGisController controller) {
        BCAddWayPointFragment fragment = new BCAddWayPointFragment();
        fragment.mContext = context;
        fragment.mListener = listener;
        fragment.trip = BCTripInfoDBHelper.get(tripId);
        if (wp != null) {
            fragment.currentWaypoint = wp;
        }
        fragment.fragmentManager = fragmentManager;
        fragment.gisController = controller;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_way_point, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        try {
            getWaypointsDrawableList(inflater, mGrid);
            if (BCUtils.isTracking()) {
                String tripId = BCUtils.getLastEditedTrip();
                BCTripInfo bcTripInfo = BCTripInfoDBHelper.get(tripId);
                if (bcTripInfo != null) {
                    mSearchTripBtn.setText(bcTripInfo.getName());
                }
                currentWaypoint = new BCGeometry();
                currentWaypoint.setImageUrl("point");
            } else {
                if (currentWaypoint != null) {
                    mWayPointName.setText(currentWaypoint.getName());
                    trip = BCTripInfoDBHelper.get(currentWaypoint.getTripId());
                    mSearchTripBtn.setText(trip.getName());
                    mSearchTripBtn.setEnabled(false);
                } else {
                    currentWaypoint = new BCGeometry();
                    currentWaypoint.setImageUrl("point");
                    if (trip != null) {
                        mSearchTripBtn.setText(trip.getName());
                    } else {
                        mSearchTripBtn.setText(getString(R.string.search_trip));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeWayPointPickerFragment();
        mUnbinder.unbind();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @OnClick(R.id.search_trip_layout)
    public void onSearchTripLayoutClicked() {
        //FIXME Use fragment listener
        ((BCHomeActivity) Objects.requireNonNull(getActivity())).openSelectDestinationActivity();
    }

    @OnClick(R.id.imb_awp_more)
    public void onOpenMoreWaypoints() {
        if (isOpenWaypointsPicker) {
            closeWayPointPickerFragment();
        } else {
            displayWayPointPickerFragment();
        }
        isOpenWaypointsPicker = !isOpenWaypointsPicker;
    }

    @OnClick(R.id.search_trip_btn)
    public void onSearchTripClicked() {
        mListener.onSelectTripClicked(BCHomeActivity.ACTIVITY_SELECT_TRIP_ONLY);
    }

    private void getWaypointsDrawableList(LayoutInflater inflater, GridLayout view) throws IOException {
        AssetManager assetManager = Objects.requireNonNull(getContext()).getAssets();
        String[] imgPaths = assetManager.list("waypoints");
        for (String path : imgPaths) {
            final BCGeometry geometry = new BCGeometry();
            geometry.setImageUrl(path.replace(".svg", ""));
            geometry.setType(TripUtils.POINT);
            waypoints.put(path, geometry);
            @SuppressLint("InflateParams") ImageView imb = (ImageView) inflater.inflate(R.layout.waypoint_item, null);
            if (path.endsWith("svg")) {
                RequestBuilder<PictureDrawable> requestBuilder;
                requestBuilder = Glide.with(view.getContext())
                        .as(PictureDrawable.class)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .transition(withCrossFade())
                        .listener(new SvgSoftwareLayerSetter());
                Uri uri = Uri.parse(String.format("%s%s", FILE_ANDROID_ASSET_WAYPOINTS, path));
                requestBuilder.load(uri).apply(new RequestOptions().override(24, 24)).into(imb);
            } else {
                Glide.with(view.getContext()).load(path).into(imb);
            }

            imb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWayPointPickedOnPicker(geometry);
                }
            });
            view.addView(imb);
        }
    }

    public void displayWayPointPickerFragment() {
        // Instantiate the fragment.
        BCWaypointsPickerFragment fragment = BCWaypointsPickerFragment.newInstance();

        fragment.setListener(BCAddWayPointFragment.this);
        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);

        // Add the SimpleFragment.
        fragmentTransaction.add(R.id.fragment_waypoint_picker_container,
                fragment).addToBackStack(null).commit();
    }

    private void closeWayPointPickerFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        BCWaypointsPickerFragment fragment = (BCWaypointsPickerFragment) fragmentManager
                .findFragmentById(R.id.fragment_waypoint_picker_container);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment).commit();
        }
    }

    private FragmentManager getSupportFragmentManager() {
        return this.fragmentManager;
    }

    private void doSaveWaypoint() {
        if (trip == null && (null == BCUtils.getTrackingStatus() || BCUtils.getTrackingStatus() == STOP)) {
            Toast.makeText(getContext(), "No trip selected", Toast.LENGTH_SHORT).show();
        } else {
            ((BCHomeActivity) Objects.requireNonNull(getActivity())).showProgress("Saving waypoint");
            Geometry geometry = gisController.sketchEditor.getGeometry();
            final Point point = (Point) geometry;
            WebServiceCallBack<Double> elevationCallback = new WebServiceCallBack<Double>() {
                @Override
                public void onSuccess(Double elevation) {
                    saveWaypointWithElevation(elevation, point);
                }

                @Override
                public void onFailed(String errorMessage) {
                    Log.e(LOG_TAG, "Fail to get elevation");
                    saveWaypointWithElevation(0d, point);
                }
            };
            BCUtils.getMapPointElevation(elevationCallback, point, getActivity());
        }
    }

    private void saveWaypointWithElevation(Double elevation, Point point) {
        Point finalPoint = Point.createWithM(point.getX(), point.getY(), elevation, System.currentTimeMillis(), point.getSpatialReference());
        currentWaypoint.setGeoJSON(finalPoint.toJson());
        if (StringUtils.isNullOrEmpty(currentWaypoint.getId())) {
            currentWaypoint.setId(UUID.randomUUID().toString());
        }
        doStartSaveWaypoint(currentWaypoint);
    }

    public void onAddWayPointSuccess() {
        SketchEditor editor = gisController.sketchEditor;
        Geometry geometry = editor.getGeometry();
        Symbol symbol = editor.getSketchStyle().getVertexSymbol();
        Graphic graphic = currentWaypoint.getGraphic();
        if (graphic != null) {
            graphic.setVisible(true);
        } else {
            graphic = new Graphic();
            graphic.getAttributes().put(ATTR_GEO_ID, currentWaypoint.getId());
            graphic.getAttributes().put(ATTR_GEO_NAME, currentWaypoint.getName());
            graphic.getAttributes().put(ATTR_GEO_TYPE, TripUtils.POINT);
            graphic.getAttributes().put(ATTR_TRIP_NAME, trip.getName());
            graphic.getAttributes().put(ATTR_TRIP_ID, trip.getId());
            GraphicsOverlay graphicsOverlay = gisController.getTripLayerById(trip.getId());
            if (graphicsOverlay == null) {
                graphicsOverlay = BC_ArcGisController.createTripGraphicsOverlay();
                gisController.addLayer(LayerType.SKETCH, graphicsOverlay, trip.getId(), true);
            }
            graphicsOverlay.getGraphics().add(graphic);
        }

        graphic.getAttributes().put(ATTR_GEO_NAME, currentWaypoint.getName());
        graphic.getAttributes().put(ATTR_GEO_URL, currentWaypoint.getImageUrl());

        graphic.setSymbol(symbol);
        graphic.setGeometry(geometry);

        if (BCUtils.getTrackingStatus() == STOP) {
            graphic.setSelected(true);
        }

        if (!graphic.isVisible()) {
            graphic.setVisible(true);
        }

        mListener.onSaveGeometriesCompleted();
    }

    public void doStartSaveWaypoint(BCGeometry geometry) {
        String wpName = mWayPointName.getText().toString();
        if (wpName.trim().isEmpty()) {
            Toast.makeText(getContext(), "Set waypoint name", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            geometry.setName(wpName);
            geometry.setType(TripUtils.POINT);
            if (BCUtils.getTrackingStatus() != STOP) {
                new BCGeometryDAO(DB_TRACKING_TEMP).insertOrUpdate(geometry);
            } else {
                geometry.setTripId(trip.getId());
                BCTrip bcTrip = (new BCTripDAO(trip.getId())).findById(trip.getId());
                saveLocalTrip(bcTrip, Collections.singletonList(geometry), false);
                BCTripInfo tripInfo = BCTripInfoDBHelper.get(trip.getId());
                tripInfo.setShowedChecked(true);
                tripInfo.save();
            }
            mListener.dismissProgress();
            Log.d(LOG_TAG, "Saving waypoint success");
            onAddWayPointSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(LOG_TAG, e.getMessage());
            mListener.dismissProgress();
        }
    }

    @Override
    public void onWayPointPickedOnPicker(BCGeometry geometry) {
        closeWayPointPickerFragment();
        SketchEditor editor = gisController.sketchEditor;
        final Point p = editor.getGeometry().getExtent().getCenter();
        currentWaypoint.setImageUrl(geometry.getImageUrl());
        compositeDisposable.add(gisController.setSketchEditorModeForWaypoint(geometry.getImageUrl() + ".svg")
                .subscribe(new Consumer<PictureMarkerSymbol>() {

                    @Override
                    public void accept(PictureMarkerSymbol pictureMarkerSymbol) {
                        gisController.startSketchEditorWithPicture(pictureMarkerSymbol, p);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(LOG_TAG, throwable.getMessage());
                    }
                }));
    }

    @Override
    public void onTripSelected(String tripId) {
        trip = BCTripInfoDBHelper.get(tripId);
        mSearchTripBtn.setText(trip.getName());
    }

    @Override
    public void onTripSelectedAndSave(String tripId) {
        trip = BCTripInfoDBHelper.get(tripId);
        mSearchTripBtn.setText(trip.getName());
        doSaveWaypoint();
    }

    @Override
    public void onSaveGeometry() {
        doSaveWaypoint();
    }

    @Override
    public void onSaveGeometries(List<BCGeometry> geometries, int tripZoom) {

    }

    @Override
    public void showGraphic() {
        if (currentWaypoint.getGraphic() != null) currentWaypoint.getGraphic().setVisible(true);
    }

    public interface OnFragmentInteractionListener {
        void onSelectTripClicked(int activityResult);

        void onSaveGeometriesCompleted();

        void dismissProgress();
    }
}
