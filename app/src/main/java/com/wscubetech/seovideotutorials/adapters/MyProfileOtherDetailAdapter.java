package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.ChangePasswordActivity;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.activities.MyProfileActivity;
import com.wscubetech.seovideotutorials.activities.QuestionListActivity;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyDialog;
import com.wscubetech.seovideotutorials.model.MyProfileOtherDetailModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.LogOutUser;
import com.wscubetech.seovideotutorials.utils.SendDeviceIdToServer;

import java.util.ArrayList;

/**
 * Created by wscubetech on 4/4/17.
 */

public class MyProfileOtherDetailAdapter extends RecyclerView.Adapter<MyProfileOtherDetailAdapter.ViewHolder> {

    Activity act;
    ArrayList<MyProfileOtherDetailModel> arrayModel = new ArrayList<>();

    private int lastPosition = -1;
    private int minDuration = 600;

    public MyProfileOtherDetailAdapter(Activity act, ArrayList<MyProfileOtherDetailModel> arrayModel) {
        this.act = act;
        this.arrayModel = arrayModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(act).inflate(R.layout.row_profile_other_detail, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyProfileOtherDetailModel model = arrayModel.get(position);
        holder.txtTitle.setText(model.getTitle().trim());
        holder.img.setImageResource(model.getResDrawable());
        holder.txtInitial.setText(model.getInitial());
        holder.txtInitial.setBackgroundResource(model.getResInitialBgRes());
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return arrayModel.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(minDuration);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
            minDuration += 100;
            if (minDuration > 1000) {
                minDuration = 600;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTitle, txtInitial;
        ImageView img;
        RelativeLayout relParent;

        public ViewHolder(View v) {
            super(v);
            relParent = (RelativeLayout) v.findViewById(R.id.relParent);
            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtInitial = (TextView) v.findViewById(R.id.txtInitial);
            img = (ImageView) v.findViewById(R.id.img);

            relParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.relParent:
                    MyProfileOtherDetailModel model = arrayModel.get(getAdapterPosition());
                    switch (model.getInitial()) {
                        case "Q":
                            intent = new Intent(act, QuestionListActivity.class);
                            intent.putExtra("ComingFrom", "myQuestions");
                            act.startActivity(intent);
                            break;
                        case "A":
                            intent = new Intent(act, QuestionListActivity.class);
                            intent.putExtra("ComingFrom", "myAnswers");
                            act.startActivity(intent);
                            break;
                        case "N":
                            showToggleNotifyDialog();
                            break;
                        case "C":
                            intent = new Intent(act, ChangePasswordActivity.class);
                            act.startActivity(intent);
                            break;
                        case "L":
                            if (act instanceof MyProfileActivity) {
                                //((MyProfileActivity)act).sureLogOutDialog();
                                LogOutUser logOutUser = new LogOutUser(act);
                                logOutUser.sureLogOutDialog();
                            }
                            break;
                    }
                    break;
            }
        }
    }

    private void showToggleNotifyDialog() {
        final Dialog dialog = new MyDialog(act).getMyDialog(R.layout.dialog_notification_on_off);
        LinearLayout linNotifyToggle;
        final TextView txtDone;
        final TextView txtOnOff;
        linNotifyToggle = (LinearLayout) dialog.findViewById(R.id.linNotifyToggle);
        txtDone = (TextView) dialog.findViewById(R.id.txtDone);
        txtOnOff = (TextView) dialog.findViewById(R.id.txtOnOff);

        final UserModel userModel = new UserDetailsPrefs(act).getUserModel();
        toggle(userModel, txtOnOff);

        linNotifyToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userModel.setNotify(!userModel.isNotify());
                toggle(userModel, txtOnOff);
            }
        });

        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!new ConnectionDetector(act).isConnectingToInternet()){
                    Toast.makeText(act,act.getString(R.string.connectionError),Toast.LENGTH_LONG).show();
                    return;
                }
                dialog.dismiss();
                UserDetailsPrefs prefs=new UserDetailsPrefs(act);
                prefs.setUserModel(userModel);
                SendDeviceIdToServer server=new SendDeviceIdToServer(act);
                server.sendDeviceIdAndNotifyStatusToServer();
            }
        });

        dialog.show();

    }

    private void toggle(UserModel userModel, TextView txtOnOff) {
        txtOnOff.setBackgroundResource(userModel.isNotify() ? R.drawable.circle_primary : R.drawable.circle_text_grey);
        txtOnOff.setText(userModel.isNotify() ? "On" : "Off");
    }
}
