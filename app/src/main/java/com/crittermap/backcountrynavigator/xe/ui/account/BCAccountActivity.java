package com.crittermap.backcountrynavigator.xe.ui.account;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.BCDatabase;
import com.crittermap.backcountrynavigator.xe.data.model.BCDatabaseHelper;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.eventbus.BCGetProfilePictureSuccess;
import com.crittermap.backcountrynavigator.xe.eventbus.BCGetUserProfileSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCLogoutSuccessEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCSystemErrorEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCUpdateUserProfilePictureFailedEvent;
import com.crittermap.backcountrynavigator.xe.eventbus.BCUploadProfilePictureSuccess;
import com.crittermap.backcountrynavigator.xe.service.BCIntendService;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.share.Logger;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.custom.BCProgressImageViewCircle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.crittermap.backcountrynavigator.xe.share.BCConstant.IMAGE_HEIGHT;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.IMAGE_WIDTH;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.REQUEST_CAMERA;
import static com.crittermap.backcountrynavigator.xe.share.BCConstant.REQUEST_PICK_IMAGE;

/**
 * Created by henryhai on 3/27/18.
 */

public class BCAccountActivity extends BCBaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_BASIC_INFO = 100;
    private static final String TAG = BCAccountActivity.class.getSimpleName();
    @BindView(R.id.user_image)
    BCProgressImageViewCircle mUserImage;
    @BindView(R.id.camera_button)
    ImageButton mCameraButton;
    @BindView(R.id.username_txt)
    TextView mUsername;
    @BindView(R.id.email_txt)
    TextView mUserEmail;
    @BindView(R.id.basic_info)
    RelativeLayout mBasicInfo;
    //    @BindView(R.id.change_email)
//    RelativeLayout mChangeEmail;
    @BindView(R.id.change_password)
    RelativeLayout mChangePassword;
    @BindView(R.id.membership)
    RelativeLayout mMembership;
    @BindView(R.id.my_wallet)
    RelativeLayout mMyWallet;
    @BindView(R.id.logout)
    RelativeLayout mLogout;

    private BCUser mCurrentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        mCurrentUser = BCUtils.getCurrentUser();
        configToolbar();
        makeStatusBarNotTransparent();
        showImageToView(R.drawable.ic_account);
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (mCurrentUser != null) {
            if (BCUtils.isNetworkAvailable(this)) {
                showProgress(getResources().getString(R.string.loading_progress));
                BCIntendService.startActionGetUserProfile(this, mCurrentUser.getUserName());
                BCIntendService.startActionGetProfilePicture(this, mCurrentUser.getUserName());
            } else {
                BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
                loadCacheInfo();
            }
        }
    }

    private void configToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCacheInfo() {
        mCurrentUser = BCUtils.getCurrentUser();
        mUsername.setText(mCurrentUser.getFirstName());
        mUserEmail.setText(mCurrentUser.getUserName());
        if (mCurrentUser.getImage() != null) {
            File imgFile = new File(mCurrentUser.getImage());
            if (imgFile.exists()) {
                showImageToView(imgFile);
            }
        }
    }

    private <T> void showImageToView(T imgFile) {
        Glide.with(this)
                .load(imgFile)
                .apply(new RequestOptions()
                        .override(IMAGE_WIDTH, IMAGE_HEIGHT)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .apply(RequestOptions.circleCropTransform())
                .into(mUserImage.mImageView);
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @OnClick(R.id.user_image)
    public void onUserImageClicked() {
        if (!BCUtils.isNetworkAvailable(this)) {
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
            return;
        }

        onClickUserImage();
    }

    @OnClick(R.id.camera_button)
    public void onCameraButtonClicked() {
        if (!BCUtils.isNetworkAvailable(this)) {
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
            return;
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                }
            }
        }
    }

    @OnClick(R.id.basic_info)
    public void onBasicInfoClicked() {
        Intent intent = new Intent(this, BCBasicInformationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, REQUEST_CODE_BASIC_INFO);
    }

    //    @OnClick(R.id.change_email)
//    public void onChangeEmailClicked() {
//        Intent intent = new Intent(this, BCChangeEmailActivity.class);
//        startActivity(intent);
//    }

    @OnClick(R.id.change_password)
    public void onChangePasswordClicked() {
        Intent intent = new Intent(this, BCChangePasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.membership)
    public void onMembershipClicked() {
        Intent intent = new Intent(this, BCMembershipActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.my_wallet)
    public void onMyWalletClicked() {
        Intent intent = new Intent(this, BCMyWalletActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.logout)
    public void onLogoutClicked() {
        if (BCUtils.isNetworkAvailable(this)) {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            showProgress("Log out...");
                            BCIntendService.startActionLogout(getApplicationContext());
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            BCAlertDialogHelper.showAlertWithConfirm(this, BCAlertDialogHelper.BCDialogType.LOG_OUT, onClickListener, null, "Are you want to logout?");
        } else {
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.NETWORK_ERROR, "");
        }
    }

    public void onClickUserImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    String imagePath = saveImagePath(imageUri);
                    uploadPicture(imagePath);
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        String imagePath = saveImagePathAfterCapture(bitmap);
                        uploadPicture(imagePath);
                    } catch (Exception e) {
                        Logger.e("Error:", e.getMessage());
                    }
                }
                break;
            case REQUEST_CODE_BASIC_INFO:
                mCurrentUser = BCUtils.getCurrentUser();
                mUsername.setText(mCurrentUser.getFirstName());
                mUserEmail.setText(mCurrentUser.getUserName());
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutSuccess(BCLogoutSuccessEvent bcLogoutSuccessEvent) {
        dismissProgress();
        BCUtils.clearUserShareRef();
        BCUtils.clearLastMapPref();
        BCUtils.clearUserSettingShareRef();
        BCUtils.clearLastLocation();
        BCDatabase.clearAllData();
        BCUtils.goToMain(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserProfileSuccess(final BCGetUserProfileSuccessEvent bcGetUserProfileSuccessEvent) {
        if (bcGetUserProfileSuccessEvent.getUser() != null) {
            dismissProgress();
            BCUser user = bcGetUserProfileSuccessEvent.getUser();
            BCUtils.saveUserShareRef(user);
            mCurrentUser = BCUtils.getCurrentUser();
            loadCacheInfo();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetProfilePictureSuccess(BCGetProfilePictureSuccess bcGetProfilePictureSuccess) {
        try {
            if (bcGetProfilePictureSuccess != null) {
                String path = saveImagePathAfterCapture(bcGetProfilePictureSuccess.getBitmap());
                mCurrentUser.setImage(path);
                BCDatabaseHelper.save(mCurrentUser);
                showImageToView(bcGetProfilePictureSuccess.getBitmap());
            } else {
                dismissProgress();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, e.getMessage());
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadProfilePictureSuccess(BCUploadProfilePictureSuccess successEvent) {
        dismissProgress();
        if (successEvent != null && successEvent.getFilePath() != null) {
            String filePath = successEvent.getFilePath();

            showImageToView(filePath);
            mCurrentUser.setImage(filePath);
            BCDatabaseHelper.save(mCurrentUser);
            BCUtils.saveUserShareRef(mCurrentUser);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadProfilePictureFailed(BCUpdateUserProfilePictureFailedEvent failedEvent) {
        dismissProgress();

        if (failedEvent != null) {
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.CHANGE_PASSWORD_ERROR, "System error! Please try again later.");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemError(final BCSystemErrorEvent errorEvent) {
        dismissProgress();

        if (errorEvent != null) {
            BCAlertDialogHelper.showErrorAlert(this, BCErrorType.CHANGE_PASSWORD_ERROR, "System error! Please try again later.");
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

    private String saveImagePathAfterCapture(Bitmap capturedPhoto) throws IOException {
        String root = Environment.getExternalStorageDirectory().toString();
        File saveDirectory = new File(root + "/bcnavxe");
        if (!saveDirectory.exists()) {
            saveDirectory.mkdirs();
        }
        File finalFile = new File(saveDirectory, "user_profile.png");
        if (finalFile.exists()) {
            finalFile.delete();
        }
        FileOutputStream out = null;
        out = new FileOutputStream(finalFile);
        capturedPhoto.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
        return finalFile.getAbsolutePath();
    }

    private void uploadPicture(String imageUri) {
        showProgress(null);
        BCIntendService.startActionUpdateProfilePicture(this,
                mCurrentUser.getUserName(), imageUri);
    }
}
