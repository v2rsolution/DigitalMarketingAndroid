package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.InterviewQuesActivity;
import com.wscubetech.seovideotutorials.activities.QuizPlayActivity;
import com.wscubetech.seovideotutorials.activities.StudyMaterialQuesActivity;
import com.wscubetech.seovideotutorials.model.SubCategoryModel;
import com.wscubetech.seovideotutorials.model.TestPaperModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 13/9/16.
 */
public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
    ArrayList<SubCategoryModel> arraySubCategoryModel = new ArrayList<>();
    Activity activity;
    int comingFromColor = 0; //1->Interview Ques(color_tile_2)  2->Quiz Test(color_tile_3)  3->Technical Terms(color_tile_4)


    public SubCategoryAdapter(Activity activity, ArrayList<SubCategoryModel> arraySubCategoryModel, int comingFromColor) {
        this.activity = activity;
        this.arraySubCategoryModel = arraySubCategoryModel;
        this.comingFromColor = comingFromColor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_sub_category, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SubCategoryModel model = arraySubCategoryModel.get(position);

        holder.txtSubCatTitle.setText(model.getSubCatTitle());

        holder.imgCat.setBackgroundResource(comingFromColor);

        Log.v("SubCatImageUrl", "" + Urls.imageUrl + model.getSubCatImage());
        Glide.with(activity)
                .load(Urls.imageUrl + model.getSubCatImage())
                .crossFade(400)
                .thumbnail(0.1f)
                .into(holder.imgCat);

        holder.linCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                switch (comingFromColor) {
                    case R.color.color_tile_1:
                    case R.color.color_tile_3:
                        intent.setClass(activity, InterviewQuesActivity.class);
                        break;

                    case R.color.color_tile_2:
                        intent.setClass(activity, QuizPlayActivity.class);
                        break;
                    case R.color.color_tile_5:
                        intent.setClass(activity, StudyMaterialQuesActivity.class);
                        break;
                }

                intent.putExtra("SubCategoryModel", model);
                activity.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arraySubCategoryModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtSubCatTitle;
        ImageView imgCat;
        LinearLayout linCard;

        public ViewHolder(View v) {
            super(v);
            linCard = (LinearLayout) v.findViewById(R.id.linCard);
            imgCat = (ImageView) v.findViewById(R.id.imgCat);
            txtSubCatTitle = (TextView) v.findViewById(R.id.txtSubCatTitle);
        }
    }
}
