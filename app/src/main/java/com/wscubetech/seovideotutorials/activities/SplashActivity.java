/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    TextView txtDigital, txtSplashTitle;
    LinearLayout linMain;

    ImageView imgLogoSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_splash);
        init();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_overshoot);
        imgLogoSplash.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow);
        txtDigital.startAnimation(animation);
        txtSplashTitle.startAnimation(animation);

        GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "Device");
        Log.v("DeviceId", prefs.getData("DeviceId"));
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                synchronized (this) {
                    try {
                        wait(3200);
                        Intent intent = new Intent(SplashActivity.this, SelectCourseActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {

                    }

                }
            }
        };
        thread.start();

        if (new ConnectionDetector(getApplicationContext()).isConnectingToInternet())
            okHttpViewCurrentAppVersion();

    }

    public void init() {
        txtSplashTitle = (TextView) findViewById(R.id.txtSplashTitle);
        imgLogoSplash = (ImageView) findViewById(R.id.imgLogoSplash);
        linMain = (LinearLayout) findViewById(R.id.linMain);
        txtDigital = (TextView) findViewById(R.id.txtDigital);
    }

    private void okHttpViewCurrentAppVersion() {
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        OkHttpCalls calls = new OkHttpCalls(Urls.CURRENT_APP_VERSION, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = res.body().string();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("response") == 1) {
                        JSONObject obj = json.getJSONObject("message");
                        String serverAppVersion = obj.getString("account_app_version");
                        String serverAppMandatory = obj.getString("account_app_status"); //value 0 for not mandatory, value 1 for mandatory
                        GetSetSharedPrefs prefs = new GetSetSharedPrefs(getApplicationContext(), "App_Version_");
                        prefs.putData("ServerAppVersion", serverAppVersion);
                        prefs.putData("ServerAppMandatory", serverAppMandatory);
                    }
                } catch (Exception e) {
                    Log.v("CurrentVersionException", "" + e);
                }
            }
        });
    }


}
