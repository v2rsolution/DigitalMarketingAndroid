package com.wscubetech.seovideotutorials.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.HomeActivity;


/**
 * Created by wscubetech on 7/6/16.
 */
public class DialogMsg {

    Activity activity;
    Dialog dialog;

    public DialogMsg(Activity activity) {
        this.activity = activity;
    }

    public void showNetworkErrorDialog(final String msg) {
        final Dialog dialog = new MyDialog(activity).getMyDialog(R.layout.dialog_internet);
        dialog.setCancelable(false);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt);
        TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        LinearLayout linParent = (LinearLayout) dialog.findViewById(R.id.linParent);
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (msg.equalsIgnoreCase(activity.getResources().getString(R.string.networkError))) {
                    if (activity instanceof HomeActivity) {
                        activity.onBackPressed();
                    }
                }
            }
        });

        txtMsg.setText(msg);

        linParent.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_up));

        dialog.show();

    }

    public void showGeneralErrorDialog(final String msg) {
        final Dialog dialog = new MyDialog(activity).getMyDialog(R.layout.dialog_internet);
        dialog.setCancelable(false);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt);
        TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        ImageView img = (ImageView) dialog.findViewById(R.id.img);
        img.setImageResource(R.drawable.ic_general_error);

        int padding = activity.getResources().getDimensionPixelSize(R.dimen.dim_7);
        img.setPadding(padding, padding, padding, padding);
        LinearLayout linParent = (LinearLayout) dialog.findViewById(R.id.linParent);
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        txtMsg.setText(msg);


        linParent.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_up));

        dialog.show();

    }

    public void showSuccessDialog(final String msg, View.OnClickListener onClickListener) {
        dialog = new MyDialog(activity).getMyDialog(R.layout.dialog_internet);
        dialog.setCancelable(false);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt);
        TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        ImageView img = (ImageView) dialog.findViewById(R.id.img);
        img.setImageResource(R.drawable.ic_tick_circle);

        int padding = activity.getResources().getDimensionPixelSize(R.dimen.dim_7);
        img.setPadding(padding, padding, padding, padding);
        LinearLayout linParent = (LinearLayout) dialog.findViewById(R.id.linParent);
        txtOk.setOnClickListener(onClickListener);

        txtMsg.setText(msg);

        linParent.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_up));

        dialog.show();

    }

    public Dialog getDialog() {
        return dialog;
    }

    public void showSuccessOrLoadingDialog() {
        final Dialog dialog = new MyDialog(activity).getMyDialog(R.layout.dialog_sent);
        dialog.setCancelable(true);
        TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        RelativeLayout relParent = (RelativeLayout) dialog.findViewById(R.id.relParent);
        LinearLayout linSent = (LinearLayout) dialog.findViewById(R.id.linSent);
        ProgressWheel progressWheel = (ProgressWheel) dialog.findViewById(R.id.progressBar);

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        relParent.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_up));
        dialog.show();
    }

    //true in case of QuizPlay, false in case rate us dialog
    public void showSureQuitDialog(final String message, View.OnClickListener listener) {
        dialog = new MyDialog(activity).getMyDialog(R.layout.dialog_sure_quit);
        dialog.setCancelable(true);
        TextView txtYes, txtCancel, txtMessage;
        RelativeLayout relParent;
        txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        txtYes = (TextView) dialog.findViewById(R.id.txtYes);
        txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        relParent = (RelativeLayout) dialog.findViewById(R.id.relParent);
        //imgLogo.setVisibility(comingFromQuizQuit?View.GONE:View.VISIBLE);
        /*String myMsg = "Liked our app?\nPlease rate us and help us to improve further";
        String instaMsg = "If you enjoy using " + activity.getString(R.string.app_name) + ", would you mind taking a moment to rate it? It won't take more than a minute. Thanks for your support!";*/
        txtMessage.setText(message);

        txtYes.setText("Yes");
        txtCancel.setText("No");

        txtYes.setOnClickListener(listener);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        relParent.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_up));

        dialog.show();

    }

    public void showRateUsDialog() {
        final Dialog dialog = new MyDialog(activity).getMyDialog(R.layout.dialog_rate_us);
        dialog.setCanceledOnTouchOutside(false);
        TextView txtRateNow, txtLater;
        txtRateNow = (TextView) dialog.findViewById(R.id.txtRateNow);
        txtLater = (TextView) dialog.findViewById(R.id.txtLater);

        txtRateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                final String appPackageName = activity.getPackageName();
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }

        });

        txtLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


}
