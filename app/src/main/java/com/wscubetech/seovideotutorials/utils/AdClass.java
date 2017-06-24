package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.wscubetech.seovideotutorials.R;


/**
 * Created by wscubetech on 16/7/15.
 */
public class AdClass {

    Activity act;
    AdView mAdView;

    public AdClass(Activity act) {
        this.act = act;
        mAdView = (AdView) act.findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
    }

    public AdClass(Activity act, View v) {
        this.act = act;
        mAdView = (AdView) v.findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
    }

    public void showAd() {

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("BD3283C27497C81E239401C45466B63F")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("A00DBFD189AF28AF4969BA3BAE72C566")
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

    }

    public void pauseAd() {
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    public void resumeAd() {
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    public void destroyAd() {
        if (mAdView != null)
            mAdView.destroy();
    }
}

