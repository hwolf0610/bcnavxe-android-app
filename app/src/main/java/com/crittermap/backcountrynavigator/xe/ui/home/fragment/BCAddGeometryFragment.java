package com.crittermap.backcountrynavigator.xe.ui.home.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController;
import com.crittermap.backcountrynavigator.xe.controller.constant.BC_DRAW_MODE;
import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCGeometryDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTripDAO;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivity;
import com.crittermap.backcountrynavigator.xe.ui.home.BCHomeActivityFragmentsContracts.OnActivityInteractionListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.raizlabs.android.dbflow.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.GONE;

public class BCAddGeometryFragment extends AppCompatDialogFragment implements OnActivityInteractionListener {
    public static String TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCAddGeometryFragment";
    private final String LOG_TAG = "BCAddGeometryFragment";
    public BCGeometry currentGeometry;
    Unbinder mUnbinder;

    //GEOMETRY NAME SECTION
    @BindView(R.id.tv_geometry_name)
    TextView mGeometryName;
    @BindView(R.id.et_geometry_name)
    EditText mETGeometryName;

    //GEOMETRY NAME SECTION
    @BindView(R.id.tv_desc)
    TextView mGeometryDescription;
    @BindView(R.id.et_description)
    EditText mETGeometryDescription;

    @BindView(R.id.search_trip_btn)
    Button mSearchTripBtn;

    //INTERNAL
    Context mContext;
    OnFragmentInteractionListener mListener;
    FragmentManager fragmentManager;

    BCTripInfo trip;

    private BC_ArcGisController gisController;

    public BCAddGeometryFragment() {
    }

    public static BCAddGeometryFragment newInstance(Context context, OnFragmentInteractionListener listener, String tripId, BCGeometry wp, FragmentManager fragmentManager, BC_ArcGisController controller) {
        BCAddGeometryFragment fragment = new BCAddGeometryFragment();
        fragment.mContext = context;
        fragment.mListener = listener;
        fragment.trip = BCTripInfoDBHelper.get(tripId);
        if (wp != null) {
            fragment.currentGeometry = wp;
        }
        fragment.fragmentManager = fragmentManager;
        fragment.gisController = controller;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_geometry, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        if (BCUtils.isTracking()) {
            String tripId = BCUtils.getLastEditedTrip();
            BCTripInfo bcTripInfo = BCTripInfoDBHelper.get(tripId);
            if (bcTripInfo != null) {
                mSearchTripBtn.setText(bcTripInfo.getName());
            }
            currentGeometry = new BCGeometry();
            currentGeometry.setImageUrl("point");
        } else {
            if (currentGeometry != null) {
                if (StringUtils.isNotNullOrEmpty(currentGeometry.getName())) {
                    mETGeometryName.setText(currentGeometry.getName());
                }

                if (StringUtils.isNotNullOrEmpty(currentGeometry.getDesc())) {
                    mETGeometryDescription.setText(currentGeometry.getDesc());
                }

                trip = BCTripInfoDBHelper.get(currentGeometry.getTripId());
                if (trip != null) {
                    mSearchTripBtn.setText(trip.getName());
                    if (StringUtils.isNotNullOrEmpty(currentGeometry.getGeoJSON())) {
                        mSearchTripBtn.setEnabled(false);
                    }
                } else {
                    mSearchTripBtn.setText(getString(R.string.search_trip));
                }
            } else {
                currentGeometry = new BCGeometry();
                currentGeometry.setImageUrl("point");
                if (trip != null) {
                    mSearchTripBtn.setText(trip.getName());
                } else {
                    mSearchTripBtn.setText(getString(R.string.search_trip));
                }
            }
        }

        if (gisController.drawMode == BC_DRAW_MODE.SKETCH) {
            mGeometryName.setVisibility(GONE);
            mETGeometryName.setVisibility(GONE);
            mGeometryDescription.setVisibility(GONE);
            mETGeometryDescription.setVisibility(GONE);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.search_trip_btn)
    public void onSearchTripClicked() {
        mListener.onSelectTripClicked(BCHomeActivity.ACTIVITY_SELECT_TRIP_ONLY);
    }

    @Override
    public void onTripSelected(String tripId) {
        trip = BCTripInfoDBHelper.get(tripId);
        mSearchTripBtn.setText(trip.getName());
    }

    @Override
    public void onTripSelectedAndSave(String tripId) {

    }

    @Override
    public void onSaveGeometry() {
        String tripId = this.trip.getId();
        if (StringUtils.isNotNullOrEmpty(tripId)) {
            try {
                currentGeometry.setName(mETGeometryName.getText().toString());
                currentGeometry.setDesc(mETGeometryDescription.getText().toString());
                (new BCGeometryDAO(currentGeometry.getTripId())).insertOrUpdate(currentGeometry);
                Logger.d(LOG_TAG, "Save geometry success :" + currentGeometry.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mListener.onSaveGeometriesCompleted();
        }
    }

    @Override
    public void onSaveGeometries(List<BCGeometry> geometries, int tripZoom) {
        String tripId = this.trip.getId();
        if (StringUtils.isNotNullOrEmpty(tripId)) {
            try {
                BCTrip trip = (new BCTripDAO(tripId)).findById(tripId);
                BCTripInfo bcTripInfo = BCTripInfoDBHelper.get(tripId);
                trip.setTripZoom(tripZoom);
                BC_TripsDBHelper.saveLocalTrip(trip, geometries, false);

                GraphicsOverlay graphicsOverlay = gisController.getTripLayerById(tripId);
                if (graphicsOverlay != null) { //exist pinned trip
                    for (BCGeometry geometry : geometries) {
                        Graphic graphic = gisController.getShapeGraphicFromBCGeometry(bcTripInfo, geometry);
                        graphicsOverlay.getGraphics().add(graphic);
                    }
                }
                gisController.safeClearSketchLayer();
                gisController.sketchEditor.stop();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mListener.onSaveGeometriesCompleted();
    }

    @Override
    public void showGraphic() {

    }

    public interface OnFragmentInteractionListener {
        void onSelectTripClicked(int activityResult);

        void onSaveGeometriesCompleted();
    }
}
