package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.InterviewQuesActivity;
import com.wscubetech.seovideotutorials.activities.QuestionListActivity;
import com.wscubetech.seovideotutorials.activities.QuizPlayActivity;
import com.wscubetech.seovideotutorials.activities.StudyMaterialQuesActivity;
import com.wscubetech.seovideotutorials.activities.VideoTutorialsTabActivity;
import com.wscubetech.seovideotutorials.model.NotificationModel;
import com.wscubetech.seovideotutorials.model.StudyMaterialModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 27/3/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    ArrayList<NotificationModel> arrayNotificationModel = new ArrayList<>();
    Activity act;

    public NotificationAdapter(Activity act, ArrayList<NotificationModel> arrayNotificationModel) {
        this.act = act;
        this.arrayNotificationModel = arrayNotificationModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(act).inflate(R.layout.row_notification, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationModel model = arrayNotificationModel.get(position);
        holder.txtNotification.setText(model.getNotificationTitle());

        //1->Video Tutorial
        switch (model.getNotificationFor()) {
            case "1":
                holder.imgNotificationIcon.setBackgroundResource(R.drawable.bg_notification_curved_tile_0);
                holder.imgNotificationIcon.setImageResource(R.drawable.ic_menu_video_tutorials);
                break;
            case "2":
                holder.imgNotificationIcon.setBackgroundResource(R.drawable.bg_notification_curved_tile_1);
                holder.imgNotificationIcon.setImageResource(R.drawable.ic_menu_interview_ques);
                break;
            case "3":
                holder.imgNotificationIcon.setBackgroundResource(R.drawable.bg_notification_curved_tile_2);
                holder.imgNotificationIcon.setImageResource(R.drawable.ic_menu_quiz_test);
                break;
            case "4":
                holder.imgNotificationIcon.setBackgroundResource(R.drawable.bg_notification_curved_tile_3);
                holder.imgNotificationIcon.setImageResource(R.drawable.ic_menu_technical_terms);
                break;
            case "5":
                holder.imgNotificationIcon.setBackgroundResource(R.drawable.bg_notification_curved_tile_5);
                holder.imgNotificationIcon.setImageResource(R.drawable.ic_tile_study_material);
                break;
            case "7":
                holder.imgNotificationIcon.setBackgroundResource(R.drawable.bg_notification_curved_tile_4);
                holder.imgNotificationIcon.setImageResource(R.drawable.ic_menu_ques_answer);
                break;
            default:
                holder.imgNotificationIcon.setBackgroundResource(R.drawable.bg_notification_curved_tile_primary);
                holder.imgNotificationIcon.setImageResource(R.drawable.ic_menu_notification);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return arrayNotificationModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtNotification;
        LinearLayout linNotification;
        ImageView imgNotificationIcon;

        public ViewHolder(View v) {
            super(v);
            imgNotificationIcon = (ImageView) v.findViewById(R.id.imgNotificationIcon);
            linNotification = (LinearLayout) v.findViewById(R.id.linNotification);
            txtNotification = (TextView) v.findViewById(R.id.txtNotification);

            linNotification.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.linNotification:
                    Intent intent;
                    NotificationModel model=arrayNotificationModel.get(getAdapterPosition());
                    String notifyGo = model.getNotificationFor();
                    //1->Video Tutorial
                    //2->InterviewQues
                    //3->Quiz Tests
                    //4->Technical Terms (Interview Ques)
                    //5->StudyMaterial
                    switch (notifyGo) {
                        case "1":
                            intent= new Intent(act, VideoTutorialsTabActivity.class);
                            intent.putExtra("SubCatId", "");
                            intent.putExtra("SubCatName", "");
                            intent.putExtra("SubCatHindiCount","0");
                            intent.putExtra("SubCatEnglishCount","0");
                            act.startActivity(intent);
                            break;
                        case "2":
                            intent= new Intent(act, InterviewQuesActivity.class);
                            intent.putExtra("SubCategoryModel",model.getSubCategoryModel());
                            act.startActivity(intent);
                            break;
                        case "3":
                            intent= new Intent(act, QuizPlayActivity.class);
                            intent.putExtra("SubCategoryModel",model.getSubCategoryModel());
                            act.startActivity(intent);
                            break;
                        case "4":
                            intent= new Intent(act, InterviewQuesActivity.class);
                            intent.putExtra("SubCategoryModel",model.getSubCategoryModel());
                            act.startActivity(intent);
                            break;
                        case "5":
                            intent= new Intent(act, StudyMaterialQuesActivity.class);
                            intent.putExtra("SubCategoryModel",model.getSubCategoryModel());
                            act.startActivity(intent);
                            break;
                        case "7":
                            intent=new Intent(act, QuestionListActivity.class);
                            intent.putExtra("ComingFrom","all");
                            act.startActivity(intent);
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }
}
