package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wscubetech.seovideotutorials.R;

/**
 * Created by wscubetech on 7/4/17.
 */

public class LoadUserImage {

    public void loadImageInImageView(final Activity act, String completePath, final ImageView img) {
        try {
            if (completePath.length() > 1)
                Glide.with(act).load(completePath).asBitmap().placeholder(R.drawable.circle_user).into(new BitmapImageViewTarget(img) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(act.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        img.setImageDrawable(circularBitmapDrawable);
                        img.setColorFilter(null);
                    }
                });
        } catch (Exception e) {

        }

    }
}
