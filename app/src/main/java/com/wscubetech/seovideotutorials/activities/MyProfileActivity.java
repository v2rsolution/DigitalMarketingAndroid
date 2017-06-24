/*Designed and Developed by V2R Solution*/

package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.adapters.MyProfileOtherDetailAdapter;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.MyProfileOtherDetailModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.user_model.ViewUserDetailsServer;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.LoadUserImage;

import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    TextView txtName, txtEmail;
    ImageView imgProfile, imgEdit;
    Toolbar toolbar;
    TextView txtHeader;
    UserModel userModel;
    RecyclerView recyclerView;

    ArrayList<MyProfileOtherDetailModel> arrayOptionModel = new ArrayList<>();
    MyProfileOtherDetailAdapter adapter;

    private GoogleApiClient mGoogleApiClient;

    Dialog progress;

    boolean active;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        init();
        toolbarOperation();

        active = true;

        progress = new MyProgressDialog(this).getDialog();

        fillProfileOptionsArray();
        onClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSetUserDetails();
        sendProfileDetailRequest();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        imgEdit = (ImageView) findViewById(R.id.imgEdit);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText("My Profile");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void getSetUserDetails() {
        userModel = new UserDetailsPrefs(this).getUserModel();
        txtName.setText(userModel.getUserName());
        txtEmail.setText(userModel.getUserEmail());

        try {
            if (userModel.getUserImage().trim().length() > 1)
                new LoadUserImage().loadImageInImageView(this, Urls.imageUrl + userModel.getUserImage(), imgProfile);
            googleAccountInitiate();
        } catch (Exception e) {
            Log.v("ExceptionProfileDetail", "" + e);
        }

    }

    private void fillProfileOptionsArray() {
        arrayOptionModel.clear();
        arrayOptionModel.add(getModel("My posted questions", R.drawable.ic_arrow_forward, "Q", R.drawable.circle_bg_ques));
        arrayOptionModel.add(getModel("My posted answers", R.drawable.ic_arrow_forward, "A", R.drawable.circle_bg_ans));
        arrayOptionModel.add(getModel("Notification Preference", R.drawable.ic_arrow_forward, "N", R.drawable.circle_primary));
        arrayOptionModel.add(getModel("Change Password", R.drawable.ic_arrow_forward, "C", R.drawable.circle_red));
        arrayOptionModel.add(getModel("Log out", R.drawable.ic_arrow_forward, "L", R.drawable.circle_text_grey));

        adapter = new MyProfileOtherDetailAdapter(this, arrayOptionModel);
        recyclerView.setAdapter(adapter);
    }

    private MyProfileOtherDetailModel getModel(String title, int resImage, String initial, int resBgInitial) {
        MyProfileOtherDetailModel model = new MyProfileOtherDetailModel();
        model.setTitle(title);
        model.setResDrawable(resImage);
        model.setInitial(initial);
        model.setResInitialBgRes(resBgInitial);
        return model;
    }

    private void onClickListeners() {
        imgEdit.setOnClickListener(this);
    }

    private void googleAccountInitiate() {
        if (userModel.isGoogleLogIn()) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgEdit:
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                break;
        }
    }


    public void sureLogOutDialog() {
        final DialogMsg dialogMsg = new DialogMsg(this);
        dialogMsg.showSureQuitDialog("Are you sure you want to log out?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMsg.getDialog().dismiss();
                signOutAndGoToHome();
            }
        });
    }

    private void signOutAndGoToHome() {
        userModel = new UserModel();
        UserDetailsPrefs prefs = new UserDetailsPrefs(this);
        prefs.setUserModel(userModel);
        if (HomeActivity.activity != null)
            HomeActivity.activity.finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Google Error", connectionResult.getErrorMessage());
    }

    private void sendProfileDetailRequest() {
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ViewUserDetailsServer detailsServer = new ViewUserDetailsServer(this, active);
            detailsServer.okHttpViewUserDetailsAndSave();
        }
    }


}
