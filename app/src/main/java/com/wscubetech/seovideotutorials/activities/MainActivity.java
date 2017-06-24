package com.wscubetech.seovideotutorials.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.model.VideoModel;
import com.wscubetech.seovideotutorials.utils.AdClass;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout lin1, lin2, lin3;
    CardView card1, card2, card3;
    Toolbar toolbar;
    TextView txtHeader;

    AdClass ad;

    int comingfrom = 0;

    //FCM Work
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String regId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        toolbarOperation();
        onClickListeners();
        animateNow();


        ad = new AdClass(this);
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            ad.showAd();
        }

        checkIfComingFromNotify();
        checkIfComingFromNotifyInterviewQues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ad.resumeAd();
    }

    @Override
    protected void onDestroy() {
        ad.destroyAd();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ad.pauseAd();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onStart() {
        super.onStart();
        decideRateDialogShowUp();
    }

    private void decideRateDialogShowUp(){
        GetSetSharedPrefs prefs=new GetSetSharedPrefs(this,"TimesOfUse");
        String timesUse=prefs.getData(Constants.TIMES_USE);
        if(timesUse.trim().length()<1){
            timesUse="1";
        }else{
            try{
                int times=Integer.parseInt(timesUse);
                if(times%6==0){
                    /*DialogMsg dialogRate=new DialogMsg(this);
                    dialogRate.showSureQuitOrRateDialog(false);*/
                }
                times+=1;
                timesUse=String.valueOf(times);
            }catch (Exception e){

            }
        }
        prefs.putData(Constants.TIMES_USE,timesUse);
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader = (TextView) findViewById(R.id.txtHeader);
        lin1 = (RelativeLayout) findViewById(R.id.lin1);
        lin2 = (RelativeLayout) findViewById(R.id.lin2);
        lin3 = (RelativeLayout) findViewById(R.id.lin3);

        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
    }

    public void toolbarOperation() {

        setSupportActionBar(toolbar);
        txtHeader.setText(getString(R.string.app_name));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
    }

    public void onClickListeners() {
        lin1.setOnClickListener(this);
        lin2.setOnClickListener(this);
        lin3.setOnClickListener(this);
    }

    public void animateNow() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_overshoot);
        card1.startAnimation(animation);
        card2.startAnimation(animation);
        card3.startAnimation(animation);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lin1:
                intent = new Intent(this, VideoTutorialsTabActivity.class);
                startActivity(intent);
                break;
            case R.id.lin2:

                break;
            case R.id.lin3:
                intent = new Intent(this, TestListingActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void checkIfComingFromNotify() {

        try {
            VideoModel model = (VideoModel) getIntent().getExtras().getSerializable("VideoModel");
            if (model != null) {
                Intent intent=new Intent(this,VideoTutorialsTabActivity.class);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.v("ExceptionComing", "" + e);
        }
    }

    private void checkIfComingFromNotifyInterviewQues(){
        try{
            String strIq="",strSubCatId="",strSubCatTitle="";
            strIq=getIntent().getExtras().getString("InterviewQuestion");
            strSubCatTitle=getIntent().getExtras().getString("SubCategoryTitle");
            strSubCatId=getIntent().getExtras().getString("SubCategoryId");
            if(strIq!=null && strIq.equalsIgnoreCase("Yes")){
                SubCategoryModel model=new SubCategoryModel();
                model.setSubCatId(strSubCatId);
                model.setSubCatTitle(strSubCatTitle);
                Intent intent=new Intent(this,InterviewQuesActivity.class);
                intent.putExtra("SubCategoryModel",model);
                startActivity(intent);
            }
        }catch (Exception e){
            Log.v("ExceptionComingIQ",""+e);
        }
    }

}
