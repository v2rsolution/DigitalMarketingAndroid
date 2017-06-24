package com.wscubetech.seovideotutorials.custom;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by wscubetech on 1/4/16.
 */
public class CustomFont {

    public static final Typeface setFontRegular(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/sansation_regular.ttf");
    }

    public static final Typeface setFontBold(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/sansation_bold.ttf");
    }

    /*public static final Typeface setFontRegularDialog(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/Roboto-Regular.ttf");
    }*/


}
