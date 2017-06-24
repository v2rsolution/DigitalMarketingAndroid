package com.wscubetech.seovideotutorials.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;


/**
 * Created by wscubetech on 16/10/15.
 */
public class MyDialog {

    Activity act;

    public MyDialog(Activity act){
        this.act=act;
    }

    public Dialog getMyDialog(int layout) {
        Dialog d = new Dialog(act);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(layout);
        return d;
    }
}
