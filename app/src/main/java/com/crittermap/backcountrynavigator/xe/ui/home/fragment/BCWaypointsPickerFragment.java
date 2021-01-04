package com.crittermap.backcountrynavigator.xe.ui.home.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.share.SvgSoftwareLayerSetter;

import java.io.IOException;
import java.util.HashMap;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.FILE_ANDROID_ASSET_WAYPOINTS;

public class BCWaypointsPickerFragment extends Fragment {
    HashMap<String, BCGeometry> waypoints = new HashMap<>();

    private OnWaypointSelectedListener mListener;

    public BCWaypointsPickerFragment() {
        // Required empty public constructor
    }

    public static BCWaypointsPickerFragment newInstance() {
        return new BCWaypointsPickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picker_bottom, container, false);
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(5);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);
        try {
            getWaypointsDrawableList(inflater, gridLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getWaypointsDrawableList(LayoutInflater inflater, GridLayout view) throws IOException {
        AssetManager assetManager = getContext().getAssets();
        String[] imgPaths = assetManager.list("waypoints");
        for (String path : imgPaths) {
            final BCGeometry geometry = new BCGeometry();
            geometry.setImageUrl(path.replace(".svg", ""));
            geometry.setType(TripUtils.POINT);
            waypoints.put(path, geometry);
            ImageView imb = (ImageView) inflater.inflate(R.layout.waypoint_item, null);
            if (path.endsWith("svg")) {
                RequestBuilder<PictureDrawable> requestBuilder;
                requestBuilder = Glide.with(view.getContext())
                        .as(PictureDrawable.class)
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
                    mListener.onWayPointPickedOnPicker(geometry);
                }
            });
            view.addView(imb);
        }
    }

    public void setListener(OnWaypointSelectedListener listener) {
        this.mListener = listener;
    }

    public interface OnWaypointSelectedListener {
        void onWayPointPickedOnPicker(BCGeometry geometry);
    }
}
