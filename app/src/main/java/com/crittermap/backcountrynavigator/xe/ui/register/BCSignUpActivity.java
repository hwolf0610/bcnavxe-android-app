package com.crittermap.backcountrynavigator.xe.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crittermap.backcountrynavigator.xe.R;
import com.crittermap.backcountrynavigator.xe.data.model.BCUser;
import com.crittermap.backcountrynavigator.xe.service.WebServiceCallBack;
import com.crittermap.backcountrynavigator.xe.service.social.BCSocialNetworkApiService;
import com.crittermap.backcountrynavigator.xe.service.social.BCSocialNetworkProfile;
import com.crittermap.backcountrynavigator.xe.service.social.BCSocialNetworkUser;
import com.crittermap.backcountrynavigator.xe.share.BCAlertDialogHelper;
import com.crittermap.backcountrynavigator.xe.share.BCErrorType;
import com.crittermap.backcountrynavigator.xe.share.BCUtils;
import com.crittermap.backcountrynavigator.xe.ui.BCBaseActivity;
import com.crittermap.backcountrynavigator.xe.ui.login.BCLoginActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by henry on 3/6/2018.
 */

public class BCSignUpActivity extends BCBaseActivity implements View.OnClickListener {
    private static int GOOGLE_SIGN_IN = 0;
    @BindView(R.id.tool_bar) Toolbar mToolBar;
    @BindView(R.id.sign_up_button) Button mCreateAccountButton;
    @BindView(R.id.facebook_button) Button mFacebookButton;
    @BindView(R.id.google_button) Button mGoogleButton;
    @BindView(R.id.log_in_button) TextView mLoginButton;
    @BindView(R.id.skip_button) TextView mSkipButton;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private WebServiceCallBack<BCUser> webServiceCallBack;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        configToolbar();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        webServiceCallBack =
                new WebServiceCallBack<BCUser>() {
                    @Override
                    public void onSuccess(BCUser user) {
                        BCUtils.saveUserShareRef(user);
                        BCUtils.goToHome(BCSignUpActivity.this);
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        BCAlertDialogHelper.showErrorAlert(BCSignUpActivity.this, BCErrorType.NETWORK_ERROR, "Unknown error occurs");
                    }
                };
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            BCSocialNetworkApiService.getInstance().postSocialNetworkUser(
                                                    new BCSocialNetworkUser(object.getString("email"),
                                                            new BCSocialNetworkProfile(
                                                                    object.getString("id"),
                                                                    object.getString("first_name"),
                                                                    object.getString("last_name"),
                                                                    "facebook",
                                                                    accessToken.getToken()
                                                            )),
                                                    webServiceCallBack
                                            );
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(BCSignUpActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(BCSignUpActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void configToolbar() {
        mToolBar.setNavigationIcon(R.drawable.icn_white_back);
        mToolBar.setNavigationOnClickListener(this);
    }

    /*
        Handle on back pressed
     */
    @Override public void onClick(View view) {
        onBackPressed();
    }

    /*
       Create account button clicked
    */
    @OnClick(R.id.sign_up_button) public void onCreateAccountButtonClicked() {
        Intent intent = new Intent(this, BCCreateAccountActivity.class);
        startActivity(intent);
    }

    /*
        Facebook button clicked
     */
    @OnClick(R.id.facebook_button) public void onFacebookButtonClicked() {
        if (BCUtils.isNetworkAvailable(this)) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        }
    }

    /*
        Google button clicked
     */
    @OnClick(R.id.google_button) public void onGoogleButtonClicked() {
        if (BCUtils.isNetworkAvailable(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        }
    }

    /*
       Login button clicked
    */
    @OnClick(R.id.log_in_button) public void onLoginButtonClicked() {
        Intent intent = new Intent(this, BCLoginActivity.class);
        startActivity(intent);
    }

    /*
        Skip button clicked
     */
    @OnClick(R.id.skip_button) public void onSkipButtonClicked() {
        BCUtils.goToHome(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            BCSocialNetworkApiService.getInstance().postSocialNetworkUser(
                    new BCSocialNetworkUser(account.getEmail(),
                            new BCSocialNetworkProfile(
                                    account.getId(),
                                    account.getGivenName(),
                                    account.getFamilyName(),
                                    "google",
                                    account.getIdToken()
                            )),
                    webServiceCallBack
            );
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error:", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
