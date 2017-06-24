/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
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

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;
import com.wscubetech.seovideotutorials.utils.ValidationsListeners;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout inpOldPass, inpPassword, inpConfirmPassword;
    EditText etOldPass, etPassword, etConfirmPassword;

    TextView txtHeader, txtChange;
    Toolbar toolbar;

    UserModel userModel;
    Dialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        progress = new MyProgressDialog(this).getDialog();
        userModel = new UserDetailsPrefs(this).getUserModel();
        toolbarOperation();
        textChangeListeners();
        onClickListeners();
        decidePasswordBlank();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);
        txtChange = (TextView) findViewById(R.id.txtChange);
        inpOldPass = (TextInputLayout) findViewById(R.id.inpOldPass);
        inpPassword = (TextInputLayout) findViewById(R.id.inpPassword);
        inpConfirmPassword = (TextInputLayout) findViewById(R.id.inpConfirmPassword);
        etOldPass = (EditText) findViewById(R.id.etOldPass);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText("Change Password");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void decidePasswordBlank() {
        if (userModel.isPasswordBlank()) {
            inpOldPass.setVisibility(View.GONE);
            etOldPass.setVisibility(View.GONE);
        } else {
            inpOldPass.setVisibility(View.VISIBLE);
            etOldPass.setVisibility(View.VISIBLE);
        }
    }

    private void textChangeListeners() {
        ValidationsListeners listeners = new ValidationsListeners(this);
        if (!userModel.isGoogleLogIn())
            etOldPass.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpOldPass));
        etPassword.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpPassword));
        etConfirmPassword.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpConfirmPassword));
    }

    private void onClickListeners() {
        txtChange.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtChange:
                String strOldPass, strPass, strConfPass;
                strOldPass = etOldPass.getText().toString().trim();
                strPass = etPassword.getText().toString().trim();
                strConfPass = etConfirmPassword.getText().toString().trim();

                if (!userModel.isGoogleLogIn()) {
                    if (strOldPass.length() < 1) {
                        inputOperationError(inpOldPass, etOldPass, "Please enter your old password");
                        return;
                    }
                }

                if (strPass.length() < 1) {
                    inputOperationError(inpPassword, etPassword, "Please enter your new password");
                    return;
                }

                if (strConfPass.length() < 1) {
                    inputOperationError(inpConfirmPassword, etConfirmPassword, "Please confirm your new password");
                    return;
                }

                if (!strPass.equals(strConfPass)) {
                    inputOperationError(inpConfirmPassword, etConfirmPassword, "Both passwords do not match");
                    return;
                }

                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    okHttpChangePassword(strOldPass, strConfPass);
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    private void okHttpChangePassword(String... passwords) {
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("user_id", userModel.getUserId()));
        arrayKeyValueModel.add(new KeyValueModel("current_pass", passwords[0]));
        arrayKeyValueModel.add(new KeyValueModel("new_pass", passwords[1]));
        arrayKeyValueModel.add(new KeyValueModel("flag", userModel.isPasswordBlank() ? "1" : ""));
        OkHttpCalls calls = new OkHttpCalls(Urls.CHANGE_PASSWORD, arrayKeyValueModel);
        calls.initiateCallPost(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponse(true, "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(false, response.body().string());
            }
        });
    }

    private void handleResponse(final boolean failed, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress.isShowing())
                    progress.dismiss();
                if (failed) {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.networkError), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Log.v("ResponsePass", response);
                        JSONObject json = new JSONObject(response);
                        final DialogMsg dialogMsg=new DialogMsg(ChangePasswordActivity.this);
                        if(json.getInt("result")==1){
                            dialogMsg.showSuccessDialog("Password successfully updated", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogMsg.getDialog().dismiss();
                                    finish();
                                }
                            });
                        }else{
                            dialogMsg.showGeneralErrorDialog("Authentication Error\nOld Password is invalid");
                        }
                    } catch (Exception e) {
                        Toast.makeText(ChangePasswordActivity.this,"Parsing error",Toast.LENGTH_LONG).show();
                        Log.v("ParsingError", "" + e);
                    }
                }
            }
        });
    }
}
