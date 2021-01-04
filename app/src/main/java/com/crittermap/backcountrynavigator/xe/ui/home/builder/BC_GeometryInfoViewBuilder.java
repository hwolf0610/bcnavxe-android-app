package com.crittermap.backcountrynavigator.xe.ui.home.builder;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.utils.DrawUtils;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.ui.home.fragment.statistic.share.BC_StatsUtils;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.raizlabs.android.dbflow.StringUtils;

import java.text.DecimalFormat;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.esri.arcgisruntime.geometry.GeometryType.POINT;

public class BC_GeometryInfoViewBuilder {
    private View view;
    private Context context;
    private View.OnClickListener onBtnEditClickedListener;
    private View.OnClickListener onBtnDeleteClickedListener;
    private DecimalFormat df;
    private String tripName;
    private ImageView logo;
    private BCGeometry bcGeometry;
    private Geometry geometry;
    private BCSettings settings;

    public static boolean isWaypoint(BCGeometry bcGeometry, GeometryType geoType) {
        return geoType.equals(POINT) && StringUtils.isNotNullOrEmpty(bcGeometry.getImageUrl());
    }

    public BC_GeometryInfoViewBuilder withView(View view) {
        this.view = view;
        return this;
    }

    public BC_GeometryInfoViewBuilder withContext(Context context) {
        this.context = context;
        return this;
    }

    public BC_GeometryInfoViewBuilder setOnBtnEditClickedListener(View.OnClickListener listener) {
        this.onBtnEditClickedListener = listener;
        return this;
    }

    public BC_GeometryInfoViewBuilder setOnBtnDeleteClickedListener(View.OnClickListener listener) {
        this.onBtnDeleteClickedListener = listener;
        return this;
    }

    public BC_GeometryInfoViewBuilder withTripName(String tripName) {
        this.tripName = tripName;
        return this;
    }

    public BC_GeometryInfoViewBuilder withSettings(BCSettings settings) {
        this.settings = settings;
        return this;
    }

    public BC_GeometryInfoViewBuilder withDecimalFormat(DecimalFormat df) {
        this.df = df;
        return this;
    }

    public BC_GeometryInfoViewBuilder withLogo(ImageView logo) {
        this.logo = logo;
        return this;
    }

    public BC_GeometryInfoViewBuilder withBCGeometry(BCGeometry bcGeometry) {
        this.bcGeometry = bcGeometry;
        this.geometry = Geometry.fromJson(bcGeometry.getGeoJSON());
        return this;
    }

    public View build() {
        buildGeneralInformation();
        GeometryType geoType = geometry.getGeometryType();
        if (isWaypoint(bcGeometry, geoType)) {
            view = this.buildWaypointInfoView();
        } else {
            switch (geoType) {
                case POINT:
                    view = this.buildPointInfoView();
                    break;
                case POLYLINE:
                    view = this.buildPolylineInfoView();
                    break;
                case POLYGON:
                    view = this.buildPolygonInfoView();
                    break;
                case MULTIPOINT:
                    view = this.buildMultipointInfoView();
                    break;
                default:
                    Glide.with(context).load(R.drawable.ic_freehand_polygon_green).into(logo);
            }
        }
        return view;
    }

    private View buildWaypointInfoView() {
        DrawUtils.getWaypointPictureMarkerSymbolFromString(context, bcGeometry.getImageUrl())
                .doOnNext(new Consumer<BitmapDrawable>() {
            @Override
            public void accept(BitmapDrawable bitmapDrawable) {
                Glide.with(context).load(bitmapDrawable).into(logo);
            }
                })
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
        view.findViewById(R.id.lb_attitude).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_attitude).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.tv_attitude))
                .setText(df.format(geometry.getExtent().getCenter().getZ()));

        return view;
    }

    private void buildGeneralInformation() {
        view.findViewById(R.id.btn_edit).setOnClickListener(onBtnEditClickedListener);
        view.findViewById(R.id.btn_delete).setOnClickListener(onBtnDeleteClickedListener);
        ((TextView) view.findViewById(R.id.tv_geometry_name)).setText(StringUtils.isNotNullOrEmpty(bcGeometry.getName()) ? bcGeometry.getName() : "N/A");
        ((TextView) view.findViewById(R.id.tv_trip)).setText(tripName);
        ((TextView) view.findViewById(R.id.tv_location))
                .setText(BC_StatsUtils.buildLocationStringBySettings(geometry.getExtent().getCenter(), settings));
        ((TextView) view.findViewById(R.id.tv_note))
                .setText(StringUtils.isNotNullOrEmpty(bcGeometry.getDesc()) ?
                        bcGeometry.getDesc() : "N/A");
    }

    private View buildPointInfoView() {
        Glide.with(context).load(R.drawable.ic_point_green).into(logo);
        return view;
    }

    private View buildPolylineInfoView() {
        Glide.with(context).load(R.drawable.ic_polyline_green).into(logo);
        view.findViewById(R.id.lb_length).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_length).setVisibility(View.VISIBLE);
        TextView tv_length = view.findViewById(R.id.tv_length);
        Polyline polyline = (Polyline) GeometryEngine.project(geometry, SpatialReferences.getWebMercator());
        double polyLineLengthInKilometer = GeometryEngine.length(polyline) / 1000;
        tv_length.setText(BC_StatsUtils.buildDistanceStringBySettings(polyLineLengthInKilometer, settings));
        return view;
    }

    private View buildPolygonInfoView() {
        Glide.with(context).load(R.drawable.ic_polygon_green).into(logo);
        view.findViewById(R.id.lb_area).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_area).setVisibility(View.VISIBLE);
        view.findViewById(R.id.lb_perimeter).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_perimeter).setVisibility(View.VISIBLE);
        Polygon polygon = (Polygon) GeometryEngine.project(geometry, SpatialReferences.getWebMercator());
        double polygonLengthInKilometer = GeometryEngine.lengthGeodetic(polygon, null, GeodeticCurveType.GEODESIC) / 1000;
        double polygonAreaInKilometer = GeometryEngine.area(polygon) / 1000;
        ((TextView) view.findViewById(R.id.tv_area))
                .setText(BC_StatsUtils.buildAreaStringBySettings(polygonAreaInKilometer, settings));
        ((TextView) view.findViewById(R.id.tv_perimeter))
                .setText(BC_StatsUtils.buildDistanceStringBySettings(polygonLengthInKilometer, settings));
        return view;
    }

    private View buildMultipointInfoView() {
        Glide.with(context).load(R.drawable.ic_multipoint_green).into(logo);
        view.findViewById(R.id.lb_points).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_points).setVisibility(View.VISIBLE);
        Multipoint multipoint = (Multipoint) geometry;
        ((TextView) view.findViewById(R.id.tv_points))
                .setText(String.valueOf(multipoint.getPoints().size()));
        return view;
    }
}
