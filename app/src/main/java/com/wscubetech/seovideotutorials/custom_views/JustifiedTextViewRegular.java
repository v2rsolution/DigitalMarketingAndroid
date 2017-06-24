package com.wscubetech.seovideotutorials.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.uncopt.android.widget.text.justify.JustifiedTextView;
import com.wscubetech.seovideotutorials.custom.CustomFont;

/**
 * Created by wscubetech on 14/3/17.
 */

public class JustifiedTextViewRegular extends JustifiedTextView {
    public JustifiedTextViewRegular(Context context) {
        super(context);
        init();
    }

    public JustifiedTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JustifiedTextViewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public void init() {
        Typeface tf = CustomFont.setFontRegular(getContext().getAssets());
        setTypeface(tf);
    }

}
