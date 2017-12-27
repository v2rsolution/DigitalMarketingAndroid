package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.model.InterviewModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 22/8/16.
 */
public class InterviewQuesAdapter extends RecyclerView.Adapter<InterviewQuesAdapter.ViewHolder> {

    ArrayList<InterviewModel> arrayInterviewModel = new ArrayList<>();
    Activity activity;
    String subCatFlag = "1";// 1->Interview Ques   2->Technical Terms


    public InterviewQuesAdapter(Activity activity, ArrayList<InterviewModel> arrayInterviewModel, String subCatFlag) {
        this.activity = activity;
        this.arrayInterviewModel = arrayInterviewModel;
        this.subCatFlag = subCatFlag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_interview_ques, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InterviewModel model = arrayInterviewModel.get(position);
        int sNo = position + 1;
        //holder.txtQuesNo.setText(subCatFlag.equals("1") ? "Question " + sNo + "." : sNo + ".");
        String quesText = model.getQuesCode().trim().length() > 0 ? (position + 1) + ". " + model.getQues().trim() + "\n" + model.getQuesCode() : (position + 1) + ". " + model.getQues().trim();
        holder.txtQuesTitle.setText(quesText);
        holder.txtAns.setText(model.getAns().trim());
        holder.txtAnsCode.setText(model.getAnsCode().trim());

        holder.txtAns.setVisibility(model.getAns().trim().length() > 0 ? View.VISIBLE : View.GONE);
        holder.txtAnsCode.setVisibility(model.getAnsCode().trim().length() > 0 ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return arrayInterviewModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtQuesTitle, txtAns, txtAnsCode;
        ImageView imgTickMark;

        public ViewHolder(View v) {
            super(v);
            txtQuesTitle = (TextView) v.findViewById(R.id.txtQuesTitle);
            txtAns = (TextView) v.findViewById(R.id.txtAns);
            txtAnsCode = (TextView) v.findViewById(R.id.txtAnsCode);
            imgTickMark = (ImageView) v.findViewById(R.id.imgTickMark);

            txtQuesTitle.setTextColor(ContextCompat.getColor(activity, subCatFlag.equals("1") ? R.color.color_tile_1 : R.color.color_tile_3));
            //txtQuesNo.setTextColor(ContextCompat.getColor(activity, subCatFlag.equals("1") ? R.color.color_tile_1 : R.color.color_tile_3));
        }
    }
}
