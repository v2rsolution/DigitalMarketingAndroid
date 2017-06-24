package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.custom.CustomFont;

/**
 * Created by wscubetech on 12/4/17.
 */

public class FirstTimeTargetView {

    Activity act;
    GetSetSharedPrefs prefs;
    String prefValue = "",prefName="";

    public FirstTimeTargetView(Activity act, String prefName) {
        this.act = act;
        this.prefName=prefName;
        prefs = new GetSetSharedPrefs(act, "FirstTimeUse");
        prefValue = prefs.getData(prefName);
    }

    public void firstTimeDemonstration(View view, String title, String description, int radius, boolean transparentTarget,TapTargetView.Listener listener) {
        if (prefValue.trim().length() < 1) {
            TapTargetView.showFor(act,
                    TapTarget.forView(view, title, description)
                            // All options below are optional
                            .outerCircleColor(R.color.color_tile_4)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.9f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(android.R.color.white)   // Specify a color for the target circle
                            .titleTextSize(20)                  // Specify the size (in sp) of the title text
                            .titleTextColor(android.R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(android.R.color.white)  // Specify the color of the description text
                            .textTypeface(CustomFont.setFontRegular(act.getAssets()))  // Specify a typeface for the text
                            .dimColor(android.R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(false)
                            .transparentTarget(transparentTarget)// Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(radius), listener);
            //prefs.putData(prefName,"no");
        }
    }

    public void firstTimeCompletion(){
        prefs.putData(prefName,"no");
    }

}
