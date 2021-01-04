package com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.detector.RotationGestureDetector;
import com.crittermap.backcountrynavigator.xe.data.model.BCSettings;
import com.crittermap.backcountrynavigator.xe.share.FragmentHelper;
import com.crittermap.backcountrynavigator.xe.share.SquareImageView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatDialogFragment;

public class CompassFragment extends DaggerAppCompatDialogFragment implements RotationGestureDetector.OnRotationGestureListener {

    public static final String TAG = "com.crittermap.backcountrynavigator.xe.ui.home.fragment.compass.CompassFragment";
    private static final String LOG_TAG = "CompassFragment";
    @Inject
    public ViewModelProvider.Factory viewModelFactory;
    int requestPermissionCode = 11;
    @BindView(R.id.compass_red_arrow)
    SquareImageView redArrow;

    @BindView(R.id.compass_needle)
    SquareImageView needle;

    @BindView(R.id.compass_face)
    SquareImageView dial;

    @BindView(R.id.compass_container)
    FrameLayout compassContainer;

    @Inject
    FragmentHelper fragmentHelper;
    @Inject
    @Named("applicationContext")
    Context applicationContext;

    private CompassViewModel compassViewModel;
    private Float currentAzimuth = 0f;
    RotationGestureDetector rotationGestureDetector;

    public CompassFragment() {
        // Required empty public constructor
    }

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compassViewModel = ViewModelProviders.of(this, viewModelFactory).get(CompassViewModel.class);
        compassViewModel.getPermissionsLiveData().observe(this, new android.arch.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean hasPermissions) {
                if (hasPermissions != null && hasPermissions) {
                    compassViewModel.resume();
                } else {
                    //FIXME permission errors handling
                    Log.e(LOG_TAG, "Has no permission to get sensors");
                    compassViewModel.getPermissions(getActivity(), requestPermissionCode);
                }
            }
        });

        compassViewModel.getSettingsMutableLiveData().observe(this, new android.arch.lifecycle.Observer<BCSettings>() {
            @Override
            public void onChanged(@Nullable final BCSettings settings) {
                if (settings != null) {
                    if (!settings.isShowCompass()) {
                        fragmentHelper.removeFragmentByTag(getActivity(), TAG);
                    } else {
                        compassViewModel.pause();
                        redArrow.clearAnimation();
                        needle.clearAnimation();
                        dial.clearAnimation();
                        redArrow.animate().rotation(0);
                        needle.animate().rotation(0);
                        dial.animate().rotation(0);
                        switch (settings.getCompassStyle()) {
                            case ORIENTEERING:
                                Glide.with(applicationContext)
                                        .load(R.drawable.ic_compass1_red_arrow)
                                        .into(redArrow);
                                Glide.with(applicationContext)
                                        .load(R.drawable.ic_compass1_needle)
                                        .into(needle);
                                Glide.with(applicationContext)
                                        .load(R.drawable.ic_compass1_face)
                                        .into(dial);
                                break;
                            case BEARING:
                                Glide.with(applicationContext)
                                        .load(R.drawable.ic_compass2_red_arrow)
                                        .into(redArrow);
                                Glide.with(applicationContext)
                                        .load(R.drawable.ic_compass1_needle)
                                        .into(needle);
                                Glide.with(applicationContext)
                                        .load(R.drawable.compass2_face)
                                        .into(dial);
                                break;
                        }
                        compassViewModel.checkPermissions();
                    }
                }
            }
        });

        compassViewModel.getAngleLiveData().observe(this, new android.arch.lifecycle.Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                Log.d(LOG_TAG, String.valueOf(aFloat));
                animateCompass(aFloat);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        ButterKnife.bind(this, view);
        rotationGestureDetector = new RotationGestureDetector(this);
        compassContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return rotationGestureDetector.onTouchEvent(v, event);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        compassViewModel.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        compassViewModel.fetchUserSettings();
    }

    private void animateCompass(Float azimuth) {
        Log.d(LOG_TAG, "will set rotation from " + currentAzimuth + " to "
                + azimuth);

        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        BCSettings settings = compassViewModel.getSettingsMutableLiveData().getValue();
        if (settings != null) {
            switch (settings.getCompassStyle()) {
                case ORIENTEERING:
                    needle.startAnimation(an);
                    break;
                case BEARING:
                    needle.startAnimation(an);
                    dial.startAnimation(an);
                    break;
            }
        }
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        BCSettings settings = compassViewModel.getSettingsMutableLiveData().getValue();
        if (settings != null) {
            switch (settings.getCompassStyle()) {
                case ORIENTEERING:
                    animate(dial, rotationDetector.getPrevRealAngle(), rotationDetector.getRealAngle(), 0);
                    break;
                case BEARING:
                    animate(redArrow, rotationDetector.getPrevRealAngle(), rotationDetector.getRealAngle(), 0);
                    break;
            }
        }
    }

    public void stopCompass() {
        compassViewModel.pause();
    }

    @SuppressWarnings("SameParameterValue")
    private void animate(@NonNull View src, double fromDegrees, double toDegrees, long durationMillis) {
        RotateAnimation rotate = new RotateAnimation((float) fromDegrees, (float) toDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        src.startAnimation(rotate);
    }
}
