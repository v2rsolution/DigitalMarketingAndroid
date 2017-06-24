package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.VideoTutorialsTabActivity;

/**
 * Created by wscubetech on 25/8/16.
 */
public class NoRecordFound {

    Activity activity;
    LinearLayout linNoRecordFound;
    TextView txtMsg;

    public NoRecordFound(Activity activity,View view) {
        this.activity = activity;
        if (activity instanceof VideoTutorialsTabActivity) {
            initFragment(view);
        } else {
            init();
        }
    }

    public NoRecordFound(Activity activity) {
        this.activity = activity;
        init();

    }

    public void init() {
        linNoRecordFound = (LinearLayout) activity.findViewById(R.id.linNoRecordFound);
        txtMsg = (TextView) activity.findViewById(R.id.txtMsg);
    }

    public void initFragment(View view) {
        linNoRecordFound = (LinearLayout) view.findViewById(R.id.linNoRecordFound);
        txtMsg = (TextView) view.findViewById(R.id.txtMsg);
    }

    public void showNoRecordFound(String msg) {
        linNoRecordFound.setVisibility(View.VISIBLE);
        txtMsg.setText(msg);

        linNoRecordFound.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_down_fast));
    }

    public void hideUi(){
        //Toast.makeText(activity,"Hide",Toast.LENGTH_LONG).show();
        linNoRecordFound.setVisibility(View.GONE);
    }

}
