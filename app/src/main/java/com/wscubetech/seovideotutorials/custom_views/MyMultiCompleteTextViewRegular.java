package com.wscubetech.seovideotutorials.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;

import com.wscubetech.seovideotutorials.custom.CustomFont;

/**
 * Created by wscubetech on 11/4/17.
 */

public class MyMultiCompleteTextViewRegular extends MultiAutoCompleteTextView {

    public MyMultiCompleteTextViewRegular(Context context) {
        super(context);
        init();
    }

    public MyMultiCompleteTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyMultiCompleteTextViewRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = CustomFont.setFontRegular(getContext().getAssets());
        setTypeface(tf);
    }
}
