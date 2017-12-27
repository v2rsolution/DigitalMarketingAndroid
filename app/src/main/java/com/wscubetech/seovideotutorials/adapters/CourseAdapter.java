package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
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

import com.google.android.gms.vision.text.Line;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.InterviewQuesActivity;
import com.wscubetech.seovideotutorials.activities.QuestionListActivity;
import com.wscubetech.seovideotutorials.activities.QuizPlayActivity;
import com.wscubetech.seovideotutorials.activities.SelectCourseActivity;
import com.wscubetech.seovideotutorials.activities.StudyMaterialQuesActivity;
import com.wscubetech.seovideotutorials.activities.VideoTutorialsTabActivity;
import com.wscubetech.seovideotutorials.model.CourseModel;
import com.wscubetech.seovideotutorials.model.NotificationModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 27/3/17.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    ArrayList<CourseModel> arrayCourseModel = new ArrayList<>();
    Activity act;

    private int lastPosition = -1;
    private int minDuration = 600;

    public CourseAdapter(Activity act, ArrayList<CourseModel> arrayCourseModel) {
        this.act = act;
        this.arrayCourseModel = arrayCourseModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(act).inflate(R.layout.row_select_course, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseModel model = arrayCourseModel.get(position);
        holder.txtCourse.setText(model.courseName.toUpperCase());
        holder.imgIcon.setImageResource(model.courseIcon);
        holder.relCard.setBackgroundResource(model.courseRes);
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return arrayCourseModel.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(minDuration);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
            minDuration += 100;
            if (minDuration > 1500) {
                minDuration = 600;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtCourse;
        RelativeLayout relCard;
        ImageView imgIcon;

        public ViewHolder(View v) {
            super(v);
            txtCourse=(TextView)v.findViewById(R.id.txtCourse);
            relCard=(RelativeLayout) v.findViewById(R.id.relCard);
            imgIcon=(ImageView)v.findViewById(R.id.imgIcon);

            relCard.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.relCard:
                    if(act instanceof SelectCourseActivity){
                        SelectCourseActivity activity=(SelectCourseActivity)act;
                        activity.onClickCourse(getAdapterPosition());
                    }
                    break;
            }
        }
    }
}
