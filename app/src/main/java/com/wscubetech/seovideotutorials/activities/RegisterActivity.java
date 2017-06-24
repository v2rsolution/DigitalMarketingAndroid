package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.custom.CustomFont;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView txtHeader, txtRegister;

    //Text Input and EditText
    TextInputLayout inpName, inpEmail, inpPassword, inpConfirmPassword;
    EditText etName, etEmail, etPassword, etConfirmPassword;
    Dialog progress;
    DialogMsg dialogMsg;

    public static RegisterActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        activity = this;

        toolbarOperation();
        onClickListeners();
        textChangeListeners();

        progress = new MyProgressDialog(this).getDialog();
        dialogMsg = new DialogMsg(this);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);

        inpName = (TextInputLayout) findViewById(R.id.inpName);
        inpEmail = (TextInputLayout) findViewById(R.id.inpEmail);
        inpPassword = (TextInputLayout) findViewById(R.id.inpPassword);
        inpConfirmPassword = (TextInputLayout) findViewById(R.id.inpConfirmPassword);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        txtRegister = (TextView) findViewById(R.id.txtRegister);
    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        txtHeader.setText("Registration");
    }

    private void onClickListeners() {
        txtRegister.setOnClickListener(this);
    }

    private void textChangeListeners() {
        ValidationsListeners listeners = new ValidationsListeners(this);
        etName.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpName));
        etEmail.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpEmail));
        etPassword.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpPassword));
        etConfirmPassword.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpConfirmPassword));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtRegister:
                String strName, strEmail, strPass, strConfPass;
                strName = etName.getText().toString().trim();
                strEmail = etEmail.getText().toString().trim();
                strPass = etPassword.getText().toString().trim();
                strConfPass = etConfirmPassword.getText().toString().trim();

                MyValidations validations = new MyValidations(this);

                if (strName.length() < 1 || !validations.checkName(strName)) {
                    inputOperationError(inpName, etName, "Please enter a valid name");
                    return;
                }

                if (strEmail.length() < 1 || !validations.checkEmail(strEmail)) {
                    inputOperationError(inpEmail, etEmail, "Please enter a valid e-mail");
                    return;
                }

                if (strPass.length() < 1) {
                    inputOperationError(inpPassword, etPassword, "Please enter a password");
                    return;
                }

                if (strConfPass.length() < 1) {
                    inputOperationError(inpConfirmPassword, etConfirmPassword, "Please enter confirm password");
                    return;
                }

                if (!strConfPass.equals(strPass)) {
                    inputOperationError(inpConfirmPassword, etConfirmPassword, "Both passwords do not match");
                    return;
                }

                UserModel model = new UserModel(strName, strEmail, strPass, false,true);
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    registerOkHttp(model);
                } else {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                }
                break;
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

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    private void registerOkHttp(final UserModel userModel) {
        progress.show();

        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("email", userModel.getUserEmail()));
        arrayKeyValueModel.add(new KeyValueModel("name", userModel.getUserName()));
        arrayKeyValueModel.add(new KeyValueModel("password", userModel.getUserPassword()));
        arrayKeyValueModel.add(new KeyValueModel("app_user","1"));
        OkHttpCalls calls = new OkHttpCalls(Urls.REGISTER, arrayKeyValueModel);
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
                        Log.v("ResponseRegister", response);

                        JSONObject json = new JSONObject(response);
                        if (json.getInt("result") == 1) {
                            String userId=json.getString("seo_users_id");
                            userModel.setUserId(userId);
                            dialogMsg.showSuccessDialog("Your registration is successful\nTap on OK to continue", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogMsg.getDialog().dismiss();
                                    goToHomeAfterLogin(userModel);
                                }
                            });
                        } else {
                            dialogMsg.showGeneralErrorDialog("User with this e-mail id already exists\nPlease enter a different email-id");
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this,"Parsing error",Toast.LENGTH_LONG).show();
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

        if (HomeActivity.activity != null) {
            HomeActivity.activity.finish();
        }

        if (LoginActivity.activity != null)
            LoginActivity.activity.finish();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        Toast.makeText(this,"Welcome "+userModel.getUserName(),Toast.LENGTH_LONG).show();
        finish();

    }
}
