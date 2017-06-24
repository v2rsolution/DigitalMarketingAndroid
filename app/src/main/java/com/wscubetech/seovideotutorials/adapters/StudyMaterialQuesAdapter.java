package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.StudyMaterialAnsActivity;
import com.wscubetech.seovideotutorials.activities.StudyMaterialQuesActivity;
import com.wscubetech.seovideotutorials.model.StudyMaterialModel;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 25/3/17.
 */

public class StudyMaterialQuesAdapter extends RecyclerView.Adapter<StudyMaterialQuesAdapter.ViewHolder> {
    ArrayList<StudyMaterialModel> arrayModel = new ArrayList<>();
    Activity act;
    SubCategoryModel subCategoryModel;

    public StudyMaterialQuesAdapter(Activity act, ArrayList<StudyMaterialModel> arrayModel, SubCategoryModel subCategoryModel) {
        this.act = act;
        this.arrayModel = arrayModel;
        this.subCategoryModel = subCategoryModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(act).inflate(R.layout.row_study_material_ques, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final StudyMaterialModel model = arrayModel.get(position);
        holder.txtQues.setText(model.getStudyQues().trim());
        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, StudyMaterialAnsActivity.class);
                intent.putExtra("ArrayStudyMaterialModel", arrayModel);
                intent.putExtra("SerialNo", position);
                intent.putExtra("SubCategoryModel", subCategoryModel);
                act.startActivity(intent);
            }
        });

        holder.txtQuesSerialNo.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return arrayModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtQues;
        TextView txtQuesSerialNo;
        RelativeLayout relMain;

        public ViewHolder(View v) {
            super(v);
            txtQues = (TextView) v.findViewById(R.id.txtQuesTitle);
            txtQuesSerialNo = (TextView) v.findViewById(R.id.txtQuesSerialNo);
            relMain = (RelativeLayout) v.findViewById(R.id.relMain);
        }
    }


}
