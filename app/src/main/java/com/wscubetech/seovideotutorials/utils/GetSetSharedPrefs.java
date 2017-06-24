package com.wscubetech.seovideotutorials.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wscubetech on 17/10/15.
 */
public class GetSetSharedPrefs {

    public SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context act;

    public GetSetSharedPrefs(Context act, String prefName) {
        this.act = act;
        prefs = this.act.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public void putData(String key, String value) {
        editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getData(String key) {
        String value = prefs.getString(key, "");
        return value;
    }



}
