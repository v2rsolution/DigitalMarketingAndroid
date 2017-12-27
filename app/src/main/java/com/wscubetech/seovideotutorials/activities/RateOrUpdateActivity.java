package com.wscubetech.seovideotutorials.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.BuildConfig;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;

public class RateOrUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRateUs, btnUpdate;
    LinearLayout linUpdate, linRating;
    TextView txtCancel;
    int comingFrom = 0; //0-> rate us, 1->update mandatory, 2->update optional

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_or_update);
        init();
        comingFrom = getIntent().getExtras().getInt("ComingFrom");
        linRating.setVisibility(comingFrom == 0 ? View.VISIBLE : View.GONE);
        linUpdate.setVisibility(comingFrom >= 1 ? View.VISIBLE : View.GONE);
        txtCancel.setVisibility(comingFrom == 2 ? View.VISIBLE : View.GONE);
        onClickListeners();
    }

    private void init() {
        linUpdate = (LinearLayout) findViewById(R.id.linUpdate);
        linRating = (LinearLayout) findViewById(R.id.linRating);
        btnRateUs = (Button) findViewById(R.id.btnRateUs);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        txtCancel = (TextView) findViewById(R.id.txtCancel);
    }

    private void onClickListeners() {
        btnRateUs.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (comingFrom == 1)
            return;
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRateUs:
            case R.id.btnUpdate:
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                HomeActivity.showRatingActivity = true;

                switch (comingFrom){
                    case 0:
                    case 2:
                        finish();
                        break;
                    case 1:
                        break;
                }
                break;
            case R.id.txtCancel:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //returning into app finish it if updated
        if(comingFrom==1 && isUpdated()){
            finish();
        }
    }

    private boolean isUpdated() {
        GetSetSharedPrefs prefs = new GetSetSharedPrefs(getApplicationContext(), "App_Version_");
        try {
            double serverAppVersion = Double.parseDouble(prefs.getData("ServerAppVersion"));
            int mandatoryUpdate = Integer.parseInt(prefs.getData("ServerAppMandatory")); //0-> not mandatory, 1->mandatory

            double myAppCurVersion = Double.parseDouble(BuildConfig.VERSION_NAME);

            return myAppCurVersion >= serverAppVersion;

        } catch (Exception e) {

        }

        return true;
    }
}
