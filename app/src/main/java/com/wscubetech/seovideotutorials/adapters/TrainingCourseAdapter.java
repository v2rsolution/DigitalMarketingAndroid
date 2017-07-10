package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.EnquiryTrainingActivity;
import com.wscubetech.seovideotutorials.activities.TrainingCourseDetailActivity;
import com.wscubetech.seovideotutorials.fragments.TrainingCoursesFragment;
import com.wscubetech.seovideotutorials.model.TrainingModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 3/6/16.
 */
public class TrainingCourseAdapter extends RecyclerView.Adapter<TrainingCourseAdapter.ViewHolder> {

    Activity activity;
    ArrayList<TrainingModel> arrayTrainingModel = new ArrayList<>();
    FragmentManager fragmentManager;


    public TrainingCourseAdapter(Activity activity, ArrayList<TrainingModel> arrayTrainingModel, FragmentManager fragmentManager) {
        this.activity = activity;
        this.arrayTrainingModel = arrayTrainingModel;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.row_training_course, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TrainingModel model = arrayTrainingModel.get(position);

        holder.txtTrainingName.setText(model.getName());
        holder.txtTrainingDesc.setText(model.getDescription());
        holder.txtTrainingDuration.setText(model.getDuration());
        holder.txtTrainingEligibility.setText(model.getEligibility());

        holder.imgTraining.setImageResource(model.getImage());

        holder.txtEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new InitializeFragment(activity, "add").initFragment(new FragmentTrainingEnquiry(), true, fragmentManager);
                Intent intent = new Intent(activity, EnquiryTrainingActivity.class);
                intent.putExtra("SelectedIndex", position);
                activity.startActivity(intent);

            }
        });

        holder.txtSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll.performClick();
            }
        });

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, TrainingCourseDetailActivity.class);
                TrainingCoursesFragment.lastPosition=position;
                i.putExtra("Position", position + "");
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayTrainingModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll;

        ImageView imgTraining;
        TextView txtTrainingName, txtTrainingDesc, txtTrainingDuration, txtTrainingEligibility;

        TextView txtEnquiry, txtSeeMore;

        public ViewHolder(View v) {
            super(v);

            ll = (LinearLayout) v.findViewById(R.id.ll);
            imgTraining = (ImageView) v.findViewById(R.id.imgTraining);
            txtTrainingName = (TextView) v.findViewById(R.id.txtTrainingName);
            txtTrainingDesc = (TextView) v.findViewById(R.id.txtTrainingDesc);
            txtTrainingDuration = (TextView) v.findViewById(R.id.txtTrainingDuration);
            txtTrainingEligibility = (TextView) v.findViewById(R.id.txtTrainingEligibilty);
            txtEnquiry = (TextView) v.findViewById(R.id.btnEnquiry);
            txtSeeMore = (TextView) v.findViewById(R.id.btnSeeMore);

        }
    }




}
