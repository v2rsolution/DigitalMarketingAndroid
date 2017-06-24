/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.custom.CustomFont;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.MyValidations;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;
import com.wscubetech.seovideotutorials.utils.ValidationsListeners;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    Toolbar toolbar;
    TextView txtHeader, txtLogin, txtRegister, txtForgotPassword;

    //For Google log in
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnGoogleLogIn;

    //Text Input and EditText
    TextInputLayout inpEmail, inpPassword;
    EditText etEmail, etPassword;
    Dialog progress;
    DialogMsg dialogMsg;

    public static LoginActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        activity = this;

        toolbarOperation();
        onClickListeners();
        textChangeListeners();

        googleSignInPreWork();

        progress = new MyProgressDialog(this).getDialog();
        dialogMsg = new DialogMsg(this);

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        btnGoogleLogIn = (SignInButton) findViewById(R.id.btnGoogleLogIn);

        inpEmail = (TextInputLayout) findViewById(R.id.inpEmail);
        inpPassword = (TextInputLayout) findViewById(R.id.inpPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtRegister = (TextView) findViewById(R.id.txtRegister);
    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        txtHeader.setText("Log in");
    }

    private void onClickListeners() {
        txtLogin.setOnClickListener(this);
        btnGoogleLogIn.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
    }

    private void textChangeListeners() {
        inpEmail.setTypeface(CustomFont.setFontRegular(getAssets()));
        inpPassword.setTypeface(CustomFont.setFontRegular(getAssets()));
        etEmail.setTypeface(CustomFont.setFontRegular(getAssets()));
        etPassword.setTypeface(CustomFont.setFontRegular(getAssets()));

        ValidationsListeners listeners = new ValidationsListeners(this);
        etEmail.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpEmail));
        etPassword.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpPassword));
    }

    private void googleSignInPreWork() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnGoogleLogIn.setSize(SignInButton.SIZE_WIDE);
        btnGoogleLogIn.setColorScheme(SignInButton.COLOR_DARK);
        //btnGoogleLogIn.setScopes(gso.getScopeArray());
    }

    private void googleSignIn() {
        progress.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //updateUI(false);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progress.dismiss();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.v(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String email = acct.getEmail();

            Log.v(TAG, "Name: " + personName + ", email: " + email);

            try {
                String personPhotoUrl = acct.getPhotoUrl().toString();

                Log.e(TAG, "Image: " + personPhotoUrl);
            } catch (Exception e) {
                Log.v("NullPointerImage", "" + e);
            }

            UserModel model = new UserModel(personName, email, "", true,true);
            if (new ConnectionDetector(this).isConnectingToInternet()) {
                loginOkHttp(model);
            } else {
                dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
            }


            /*txtName.setText(personName);
            txtEmail.setText(email);
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);*/

            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtForgotPassword:
                showEmailDialog();
                break;
            case R.id.txtRegister:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.txtLogin:
                String strEmail, strPass;
                strEmail = etEmail.getText().toString().trim();
                strPass = etPassword.getText().toString().trim();

                MyValidations validations = new MyValidations(this);
                if (strEmail.length() < 1 || !validations.checkEmail(strEmail)) {
                    inputOperationError(inpEmail, etEmail, "Please enter a valid e-mail");
                    return;
                }

                if (strPass.length() < 1) {
                    inputOperationError(inpPassword, etPassword, "Please enter a password");
                    return;
                }

                UserModel model = new UserModel("", strEmail, strPass, false,true);
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    loginOkHttp(model);
                } else {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                }


                break;
            case R.id.btnGoogleLogIn:
                googleSignIn();
                break;
        }
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    private void loginOkHttp(final UserModel userModel) {
        progress.show();

        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("email", userModel.getUserEmail()));
        arrayKeyValueModel.add(new KeyValueModel("name", userModel.getUserName()));
        arrayKeyValueModel.add(new KeyValueModel("password", userModel.getUserPassword()));
        arrayKeyValueModel.add(new KeyValueModel("flag", userModel.isGoogleLogIn() ? "1" : "0"));
        arrayKeyValueModel.add(new KeyValueModel("app_user","1"));
        OkHttpCalls calls = new OkHttpCalls(Urls.LOGIN, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponse("", true, userModel);
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = res.body().string();
                handleResponse(response, false, userModel);
            }
        });
    }

    private void handleResponse(final String response, final boolean failed, final UserModel userModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (failed) {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.networkError));
                } else {
                    try {
                        Log.v("ResponseLogin", response);

                        JSONObject json = new JSONObject(response);
                        if (json.getInt("result") == 1) {
                            String userId = json.getString("seo_users_id");
                            userModel.setUserId(userId);
                            revokeAccess();
                            goToHomeAfterLogin(userModel);
                        } else {
                            dialogMsg.showGeneralErrorDialog("Either e-mail id or password is invalid");
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Parsing error", Toast.LENGTH_LONG).show();
                        Log.v("ExceptionJsonLogin", "" + e);
                    }
                }

                if (progress.isShowing())
                    progress.dismiss();
            }
        });
    }

    private void goToHomeAfterLogin(UserModel userModel) {
        UserDetailsPrefs prefs = new UserDetailsPrefs(this);
        prefs.setUserModel(userModel);

        if(QuestionListActivity.activity!=null)
            QuestionListActivity.activity.finish();

        if(AnswerListActivity.activity!=null)
            AnswerListActivity.activity.finish();

        if (HomeActivity.activity != null) {
            HomeActivity.activity.finish();
        }

        if (RegisterActivity.activity != null)
            RegisterActivity.activity.finish();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Welcome " + userModel.getUserName(), Toast.LENGTH_LONG).show();
        finish();

    }

    private void showEmailDialog(){
        final Dialog dialog=new MyDialog(this).getMyDialog(R.layout.dialog_email);
        TextView txtEmailTitle,txtSend,txtSkip;
        ImageView imgTitle;
        final TextInputLayout inpEmail;
        final EditText etEmail;
        imgTitle=(ImageView)dialog.findViewById(R.id.imgTitle);
        txtEmailTitle=(TextView)dialog.findViewById(R.id.txtEmailTitle);
        inpEmail=(TextInputLayout)dialog.findViewById(R.id.inpEmail);
        etEmail=(EditText) dialog.findViewById(R.id.etEmail);
        txtSend=(TextView)dialog.findViewById(R.id.txtSend);
        txtSkip=(TextView)dialog.findViewById(R.id.txtSkip);

        txtEmailTitle.setText("Recover Password");
        txtSkip.setVisibility(View.GONE);

        ValidationsListeners listeners=new ValidationsListeners(this);
        etEmail.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpEmail));

        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail=etEmail.getText().toString().trim();
                if(strEmail.length()<1){
                    inputOperationError(inpEmail,etEmail,"Please enter your e-mail address");
                    return;
                }

                MyValidations validations=new MyValidations(LoginActivity.this);
                if(!validations.checkEmail(strEmail)){
                    inputOperationError(inpEmail,etEmail,"Please enter a valid e-mail address");
                    return;
                }

                if(new ConnectionDetector(LoginActivity.this).isConnectingToInternet()){
                    dialog.dismiss();
                    okHttpForgotPassword(strEmail);
                }else{
                    Toast.makeText(LoginActivity.this,getString(R.string.networkError),Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    private void okHttpForgotPassword(String strEmail){
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel=new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("email",strEmail));
        arrayKeyValueModel.add(new KeyValueModel("app_user","1"));
        OkHttpCalls calls=new OkHttpCalls(Urls.FORGOT_PASSWORD,arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        dialogMsg.showNetworkErrorDialog(getString(R.string.networkError));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                final String response=res.body().string();
                Log.v("ResponseForgotPass",response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean success=true;
                        try{
                            JSONObject json=new JSONObject(response);
                            success=json.getInt("response")==1;
                        }catch (Exception e){
                            Log.v("ParseException",""+e);
                        }
                        progress.dismiss();
                        if(success){
                            dialogMsg.showSuccessDialog("A password recovery link has been sent to your e-mail address\nPlease click on that link to reset your password", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogMsg.getDialog().dismiss();
                                }
                            });
                        }else{
                            dialogMsg.showGeneralErrorDialog("Oops!\nNo user is registered with this e-mail address");
                        }
                    }
                });

            }
        });
    }
}
