package com.crittermap.backcountrynavigator.xe.ui.saveTrip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.controller.database.BC_TripsDBHelper;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCGeometryDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTrackingPointDAO;
import com.crittermap.backcountrynavigator.xe.controller.database.dao.BCTripDAO;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCGeometry;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrackingPoint;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTrip;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfo;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.BCTripInfoDBHelper;
import com.crittermap.backcountrynavigator.xe.data.model.trip_new.TripUtils;
import com.crittermap.backcountrynavigator.xe.service.trip.BCTripIntentService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.share.TripStatus;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.custom.BCSpinner;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.raizlabs.android.dbflow.StringUtils;
import com.raizlabs.android.dbflow.data.Blob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.crittermap.backcountrynavigator.xe.controller.BC_ArcGisController.DB_TRACKING_TEMP;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.IMAGE_HEIGHT;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.IMAGE_WIDTH;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.REQUEST_CAMERA;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.REQUEST_PICK_IMAGE;
import static com.crittermap.backcountrynavigator.xe.ui.selecttrip.BCSelectTripActivity.KEY_SELECT_TRIP;

public class BCSaveTripActivity extends BCBaseActivity implements View.OnClickListener {

    public static final String DATA = "data";
    private static final String TAG = BCSaveTripActivity.class.getSimpleName();
    @BindView(R.id.et_trip_name)
    EditText etTripName;

    @BindView(R.id.til_trip_name)
    TextInputLayout tilTipName;

    @BindView(R.id.spinner_folder)
    BCSpinner spinner;
    @BindView(R.id.trip_image_button)
    ImageButton mTripImageButton;

    @BindView(R.id.trip_image)
    FrameLayout mTripImage;

    @BindView(R.id.btn_save)
    Button saveButton;

    @BindView(R.id.tv_folder_error)
    TextView folderError;

    @BindView(R.id.rl_camera_container)
    RelativeLayout rl_camera_container;

    private boolean isFromTrack = false;
    private boolean isFromDrawing = false;
    private BCSaveTripActivity mSaveTripActivity;
    private ArrayList<BCGeometry> geometries = new ArrayList<>();
    private String selectedFolder;
    private List<String> folders;

    private String imagePath = "";
    private String tripId = "";
    private BCTripInfo tripInfo;

    private boolean isFromSelectTrip = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_trip);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Save Trip");
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mSaveTripActivity = this;

        isFromSelectTrip = getIntent().getBooleanExtra(KEY_SELECT_TRIP, false);
        bindingData();
        initView();
    }

    @SuppressLint("DefaultLocale")
    private void initView() {
        etTripName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tilTipName.setErrorEnabled(false);
                tilTipName.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (isFromSelectTrip) {
            etTripName.setText(String.format("%s-TRIP", TripUtils.createTripNamePrefix()));
            spinner.setSelection(0);
        }
    }

    private void setUpSpinner() {
        folders.add(getString(R.string.txt_create_new));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, folders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (StringUtils.isNotNullOrEmpty(tripId) && tripInfo != null) {
            int idx = 0;
            for (int i = 0; i < folders.size(); i++) {
                String folder = folders.get(i);
                if (StringUtils.isNotNullOrEmpty(folder)) {
                    if (folders.get(i).equals(tripInfo.getTrekFolder())) {
                        idx = i;
                        break;
                    }
                }
            }
            spinner.setSelection(idx);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (getString(R.string.txt_create_new).equals(folders.get(position))) {
                    BCAlertDialogHelper.showCreateFolderDialog(BCSaveTripActivity.this,
                            new BCAlertDialogHelper.OnCreateFolderListener() {
                                @Override
                                public void onCreate(String folderName) {
                                    selectedFolder = folderName;
                                    folders.add(0, selectedFolder);
                                    adapter.notifyDataSetChanged();
                                    spinner.setSelection(folders.indexOf(selectedFolder));
                                }
                            });
                    int lastPosition = folders.indexOf(selectedFolder);
                    if (lastPosition > -1 && lastPosition != position)
                        spinner.setSelection(lastPosition);
                } else {
                    selectedFolder = folders.get(position);
                }
                folderError.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    void bindingData() {
        isFromTrack = getIntent().getBooleanExtra("saveTrack", false);

        geometries = (ArrayList<BCGeometry>) getIntent().getSerializableExtra(DATA);

        isFromDrawing = geometries != null && geometries.size() > 0;

        tripId = getIntent().getStringExtra("tripId");

        if (StringUtils.isNotNullOrEmpty(tripId)) {
            tripInfo = BCTripInfoDBHelper.get(tripId);
            etTripName.setText(tripInfo.getName());
            if (tripInfo.getImage() != null) {
                showImageToView(tripInfo.getImage().getBlob());
            }
        }

        showProgress("Loading trips folder information");
        BCTripIntentService.startGetAllTripsByUser(this, BCUtils.getCurrentUser().getUserName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static List<BCGeometry> mergeTrackingGeometries() throws IllegalAccessException {
        List<BCGeometry> geometries = new ArrayList<>();
        BCTrackingPointDAO trackingPointDAO = new BCTrackingPointDAO(DB_TRACKING_TEMP);
        List<BCTrackingPoint> trackingPoints = trackingPointDAO.getAll();
        Map<Integer, PointCollection> pointMap = new HashMap<>();
        for (BCTrackingPoint trackingPoint : trackingPoints) {
            if (pointMap.containsKey(trackingPoint.getGroupId())) {
                pointMap.get(trackingPoint.getGroupId()).add((Point) Geometry.fromJson(trackingPoint.getJsonPoint()));
            } else {
                PointCollection points = new PointCollection(SpatialReferences.getWgs84());
                points.add((Point) Geometry.fromJson(trackingPoint.getJsonPoint()));
                pointMap.put(trackingPoint.getGroupId(), points);
            }
        }

        for (PointCollection pointCollection : pointMap.values()) {
            Polyline polyline = new Polyline(pointCollection);
            BCGeometry bcGeometry = new BCGeometry();
            bcGeometry.setGeoJSON(polyline.toJson());
            bcGeometry.setWidth(5);
            bcGeometry.setName("Track");
            bcGeometry.setType("polyline");
            bcGeometry.setDesc("tracking");
            bcGeometry.setTimestamp(Calendar.getInstance().getTimeInMillis());
            bcGeometry.setColor(Color.argb(255, 0, 0, 0));

            geometries.add(bcGeometry);
        }

        BCGeometryDAO geometryDAO = new BCGeometryDAO(DB_TRACKING_TEMP);
        List<BCGeometry> temp = geometryDAO.getNonTrackingGeometries();

        geometries.addAll(temp);
        return geometries;
    }

    private void onSaveComplete() {
        setResult(RESULT_OK);
        finish();
    }

    private void onSaveError(Throwable throwable) {
        Logger.e(TAG, throwable.getMessage());
        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        saveButton.setEnabled(true);
        saveButton.setText(R.string.save);

    }

    @OnClick(R.id.btn_save)
    public void onSaveTrip() {
        if (!validateTripName()) return;

        if (TextUtils.isEmpty(selectedFolder)) {
            folderError.setVisibility(View.VISIBLE);
            return;
        }

        showProgress("Saving...");
        saveButton.setEnabled(false);
        saveButton.setText(R.string.saving);

        try {

            if (StringUtils.isNotNullOrEmpty(tripId)) {
                Long lastSync = (new Date()).getTime();
                BCTripDAO bcTripDAO = new BCTripDAO(String.format("%s", tripInfo.getId()));
                BCTrip bcTrip = bcTripDAO.findById(tripId);

                tripInfo.setName(etTripName.getText().toString());
                tripInfo.setTrekFolder(selectedFolder);
                if (StringUtils.isNotNullOrEmpty(imagePath)) {
                    byte[] bytes = BCUtils.imageToBytes(imagePath);
                    tripInfo.setImage(new Blob(bytes));
                    bcTrip.setImage(bytes);
                }
                tripInfo.setTimestamp(lastSync);
                tripInfo.setTripStatus(TripStatus.NOT_UPLOADED);
                tripInfo.setOwnerId(BCUtils.getCurrentUser().getUserName());
                BCTripInfoDBHelper.save(tripInfo);

                bcTrip.setName(tripInfo.getName());
                bcTrip.setFolder(tripInfo.getTrekFolder());
                bcTrip.setTimestamp(lastSync);
                bcTrip.setOwnerId(BCUtils.getCurrentUser().getUserName());
                bcTripDAO.insertOrUpdate(bcTrip);
                onSaveComplete();
            } else {
                final BCTrip trip = new BCTrip();
                trip.setName(etTripName.getText().toString());
                trip.setFolder(selectedFolder);
                trip.setImage(BCUtils.imageToBytes(imagePath));
                trip.setOwnerId(BCUtils.getCurrentUser().getUserName());
                trip.setTimestamp((new Date()).getTime());
                trip.setTripZoom(getIntent().getIntExtra("tripZoom", 5));
                if (isFromTrack) {
                    List<BCGeometry> geometries = mergeTrackingGeometries();
                    BC_TripsDBHelper.saveLocalTrip(trip, geometries);
                    BC_TripsDBHelper.createInstance(DB_TRACKING_TEMP).dropDatabse();
                }

                if (isFromDrawing) {
                    BC_TripsDBHelper.saveLocalTrip(trip, geometries);
                }
                dismissProgress();

                if (isFromSelectTrip) {
                    trip.setId(UUID.randomUUID().toString());
                    BC_TripsDBHelper.saveLocalTrip(trip, new ArrayList<BCGeometry>());
                    Intent data = new Intent();
                    data.putExtra("tripId", trip.getId());
                    setResult(RESULT_OK, data);
                    finish();
                    return;
                }
                TripUtils.actionPinTrip(BCTripInfoDBHelper.get(trip.getId()), this, new PinTripCallback() {
                    @Override
                    public void onSave() {
                        onSaveComplete();
                    }
                });
            }
        } catch (Exception ex) {
            onSaveError(ex);
        }

    }

    @Override
    public void onBackPressed() {
        BCAlertDialogHelper.showAlertWithConfirm(this, BCAlertDialogHelper.BCDialogType.EXIT, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    BC_TripsDBHelper.createInstance(DB_TRACKING_TEMP).dropDatabse();
                    BCSaveTripActivity.super.onBackPressed();
                }
            }
        }, null, null);

    }

    private boolean validateTripName() {
        if (TextUtils.isEmpty(etTripName.getText().toString())) {
            tilTipName.setErrorEnabled(true);
            tilTipName.setError(getString(R.string.msg_error_no_trip_name));
            return false;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadTripSuccess(BCTripIntentService.BCLoadTripsSuccessEvent loadTripsSuccessEvent) {
        dismissProgress();
        if (loadTripsSuccessEvent != null && loadTripsSuccessEvent.data != null) {
            Map<String, BCTripInfo> localTrips = BCTripInfoDBHelper.loadAll();
            for (BCTripInfo tripInfo : loadTripsSuccessEvent.data) {
                if (!localTrips.containsKey(tripInfo.getId())) {
                    BCTripInfoDBHelper.save(tripInfo);
                }
            }
        }

        folders = BCTripInfoDBHelper.getFolders();
        setUpSpinner();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadTripFailed(BCTripIntentService.BCLoadTripsFailedEvent error) {
        dismissProgress();
        folders = BCTripInfoDBHelper.getFolders();
        setUpSpinner();
    }

    @OnClick(R.id.trip_image)
    public void onTripImageClicked() {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (!BCUtils.isNetworkAvailable(getApplicationContext())) {
                            BCAlertDialogHelper.showErrorAlert(mSaveTripActivity, BCErrorType.NETWORK_ERROR, "");
                            return;
                        }

                        if (ContextCompat.checkSelfPermission(mSaveTripActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(mSaveTripActivity, Manifest.permission.CAMERA)) {

                            } else {
                                ActivityCompat.requestPermissions(mSaveTripActivity,
                                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                            }
                        } else {
                            ActivityCompat.requestPermissions(mSaveTripActivity,
                                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
                        break;
                }
            }
        };
        BCAlertDialogHelper.showAlertWithConfirm(this, BCAlertDialogHelper.BCDialogType.CAPTURE_TRIP, onClickListener, null, "Choose type for capture trip?");
    }

    @OnClick(R.id.trip_image_button)
    public void onTripImageButtonClicked() {
        if (!BCUtils.isNetworkAvailable(getApplicationContext())) {
            BCAlertDialogHelper.showErrorAlert(mSaveTripActivity, BCErrorType.NETWORK_ERROR, "");
            return;
        }

        if (ContextCompat.checkSelfPermission(mSaveTripActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mSaveTripActivity, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(mSaveTripActivity,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        } else {
            ActivityCompat.requestPermissions(mSaveTripActivity,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    imagePath = saveImagePath(imageUri);
                    showImageToView(imagePath);
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        imagePath = saveImagePathAfterCapture(bitmap);
                        showImageToView(imagePath);
                    } catch (Exception e) {
                        Logger.e(TAG, e.getMessage());
                    }
                }
                break;
        }
    }

    private String saveImagePath(Uri imageUri) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = BCUtils.getRealPathFromURIBelowAPI11(this, imageUri);
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = BCUtils.getRealPathFromURIAPI11to18(this, imageUri);
        }
        // SDK > 19 (Android 4.4)
        else {
            realPath = BCUtils.getRealPathFromURIAPI19(this, imageUri);
        }
        return realPath;
    }

    private String saveImagePathAfterCapture(Bitmap capturedPhoto) {
        Uri tempUri = BCUtils.compressImage(getApplicationContext(), capturedPhoto, 100);
        File finalFile = new File(BCUtils.getRealPathFromURI(getApplicationContext(), tempUri));
        return finalFile.getAbsolutePath();
    }

    private <T> void showImageToView(T imgFile) {
        Glide.with(this)
                .load(imgFile)
                .apply(new RequestOptions()
                        .override(IMAGE_WIDTH, IMAGE_HEIGHT))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mTripImage.setBackground(resource);
                    }
                });
        rl_camera_container.setVisibility(View.GONE);
    }

    public interface PinTripCallback {
        void onSave();
    }
}
