package com.crittermap.backcountrynavigator.xe.ui.home.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.data.model.map.BCMap;
import com.crittermap.backcountrynavigator.xe.data.model.map.MapUtils;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.mapSource.adapter.BCMapSourcePinnedAdapter;
import com.crittermap.backcountrynavigator.xe.ui.trips.adapter.BCTripPinnedAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class BCQuickAccessFragment extends AppCompatDialogFragment implements BCMapSourcePinnedAdapter.OnPinnedMapChangedListener {

    public static final String TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.BCQuickAccessFragment";
    Unbinder mUnbinder;
    @BindView(R.id.close_button)
    ImageButton mCloseBtn;

    @BindView(R.id.map_source_rcv)
    RecyclerView mMapSourcePinnedRcv;

    @BindView(R.id.trip_rcv)
    RecyclerView mTripPinnedRcv;

    @BindView(R.id.overlay_rcv)
    RecyclerView mOverlayRcv;

    @BindView(R.id.pin_trip_text)
    TextView tvPinTrip;

    @BindView(R.id.pin_map_source_text)
    TextView tvPinMapSource;

    @BindView(R.id.overlay_text)
    TextView tvOverlay;

    @BindView(R.id.switch_offline)
    SwitchCompat switchOffline;
    @BindView(R.id.switch_show_compass)
    SwitchCompat switchShowCompass;

    private CompoundButton.OnCheckedChangeListener mTogglePinnedTripListener;

    private OnQuickAccessFragmentInteractionListener quickAccessFragmentInteractionListener;

    private BCSettings settings;

    public BCQuickAccessFragment() {
    }

    public static BCQuickAccessFragment newInstance(BCSettings settings) {
        BCQuickAccessFragment fragment = new BCQuickAccessFragment();
        fragment.settings = settings;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map_source, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        tvPinTrip.setPaintFlags(tvPinTrip.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvPinMapSource.setPaintFlags(tvPinMapSource.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvOverlay.setPaintFlags(tvOverlay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        setClickableText(tvPinTrip, new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                quickAccessFragmentInteractionListener.onClickTripItem();
                quickAccessFragmentInteractionListener.closeQuickAccessFragment();
            }
        });

        setClickableText(tvPinMapSource, new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                quickAccessFragmentInteractionListener.onClickMapItem();
                quickAccessFragmentInteractionListener.closeQuickAccessFragment();
            }
        });

        setClickableText(tvOverlay, new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                quickAccessFragmentInteractionListener.closeQuickAccessFragment();
            }
        });

        switchOffline.setChecked(settings.isOffline());
        if (BCUtils.getCurrentUser() != null) {
            switchOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    quickAccessFragmentInteractionListener.switchOfflineMode(isChecked);
                }
            });
        } else {
            switchOffline.setEnabled(false);
        }

        switchShowCompass.setChecked(settings.isShowCompass());
        switchShowCompass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                quickAccessFragmentInteractionListener.switchShowCompass(isChecked);
            }
        });

        List<BCMap> maps = MapUtils.getPinnedMaps();
        BCMapSourcePinnedAdapter mapSourcePinnedAdapter = new BCMapSourcePinnedAdapter(maps, this);
        mMapSourcePinnedRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMapSourcePinnedRcv.setAdapter(mapSourcePinnedAdapter);

        BCTripPinnedAdapter tripPinnedAdapter = new BCTripPinnedAdapter(BCTripInfoDBHelper.loadPinnedTrip(), getContext(), new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mTogglePinnedTripListener != null) {
                    mTogglePinnedTripListener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
        tripPinnedAdapter.setNavigatePinnedTripListener(new BCTripPinnedAdapter.OnNavigatePinnedTripListener() {
            @Override
            public void onClick(BCTripInfo tripInfo) {
                quickAccessFragmentInteractionListener.navigateToPinnedTrip(tripInfo);
            }
        });

        mTripPinnedRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTripPinnedRcv.setAdapter(tripPinnedAdapter);

        List<BCTripInfo> overlays = new ArrayList<>();
        BCTripPinnedAdapter tripPinnedAdapter2 = new BCTripPinnedAdapter(overlays, getContext(), null);
        mOverlayRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOverlayRcv.setAdapter(tripPinnedAdapter2);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.close_button)
    public void onCloseButtonClicked() {
        quickAccessFragmentInteractionListener.closeQuickAccessFragment();
    }

    public void setQuickAccessFragmentInteractionListener(OnQuickAccessFragmentInteractionListener quickAccessFragmentInteractionListener) {
        this.quickAccessFragmentInteractionListener = quickAccessFragmentInteractionListener;
    }

    public void setTogglePinnedTripListener(CompoundButton.OnCheckedChangeListener mTogglePinnedTripListener) {
        this.mTogglePinnedTripListener = mTogglePinnedTripListener;
    }

    private void setClickableText(TextView textView, ClickableSpan span) {
        String clickableString = textView.getText().toString();
        SpannableString ss = new SpannableString(textView.getText());
        ss.setSpan(span, 0, clickableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onPinnedMapChanged(BCMap bcMap) {
        quickAccessFragmentInteractionListener.onPinnedMapChanged(bcMap);
    }

    public interface OnQuickAccessFragmentInteractionListener {

        void onClickTripItem();

        void onClickMapItem();

        void closeQuickAccessFragment();

        void switchOfflineMode(boolean isChecked);

        void navigateToPinnedTrip(BCTripInfo tripInfo);

        void switchShowCompass(boolean isChecked);

        void onPinnedMapChanged(BCMap map);
    }
}
