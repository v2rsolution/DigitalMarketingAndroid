package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.wscubetech.seovideotutorials.R;


/**
 * Created by wscubetech on 2/11/15.
 */
public class InitializeFragment {

    Activity act;
    String addOrReplace;
    String backStackYesNo;
    String tagName = "";


    public InitializeFragment(Activity act, String addOrReplace, String backStackYesNo) {
        this.act = act;
        this.addOrReplace = addOrReplace;
        this.backStackYesNo = backStackYesNo;
    }

    public InitializeFragment(Activity act, String addOrReplace, String backStackYesNo, String tagName) {
        this(act, addOrReplace, backStackYesNo);
        this.tagName = tagName;
    }

    public void initFragment(Fragment fragment, FragmentManager manager) {
        if (fragment != null) {
            FragmentManager fragmentManager = manager;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (addOrReplace.equalsIgnoreCase("add"))
                fragmentTransaction.add(R.id.frameContainer, fragment);
            else
                fragmentTransaction.replace(R.id.frameContainer, fragment);


            if (backStackYesNo.equalsIgnoreCase("yes"))
                fragmentTransaction.addToBackStack(tagName);

            fragmentTransaction.commit();
        }
    }





}
