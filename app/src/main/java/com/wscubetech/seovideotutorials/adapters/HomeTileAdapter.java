package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.model.HomeTileModel;

import java.util.ArrayList;

/**
 * Created by wscubetech on 10/3/17.
 */

public class HomeTileAdapter extends RecyclerView.Adapter<HomeTileAdapter.ViewHolder> {


    Activity activity;
    ArrayList<HomeTileModel> arrayHomeTileModel = new ArrayList<>();

    private int lastPosition = -1;
    private int minDuration = 600;

    public HomeTileAdapter(Activity activity, ArrayList<HomeTileModel> arrayHomeTileModel) {
        this.activity = activity;
        this.arrayHomeTileModel = arrayHomeTileModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_main_home_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeTileModel model = arrayHomeTileModel.get(position);
        holder.itemView.setVisibility(View.VISIBLE);
        holder.linMain.setBackgroundColor(model.bgColor);
        if (position % 2 == 0) {
            holder.linLeft.setVisibility(View.VISIBLE);
            holder.linRight.setVisibility(View.GONE);

            holder.txtTitleLeft.setText(model.title.toUpperCase());
            holder.txtDescriptionLeft.setText(model.description);
            holder.imgLeft.setImageResource(model.icon);
        } else {
            holder.linLeft.setVisibility(View.GONE);
            holder.linRight.setVisibility(View.VISIBLE);

            holder.txtTitleRight.setText(model.title.toUpperCase());
            holder.txtDescriptionRight.setText(model.description);
            holder.imgRight.setImageResource(model.icon);
        }
        setAnimation(holder.itemView, position);

    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(minDuration);
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
            minDuration += 100;
            if (minDuration > 1100) {
                minDuration = 600;
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayHomeTileModel.size();
    }

    public HomeTileModel getModel(int position) {
        return arrayHomeTileModel.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linMain, linLeft, linRight;
        TextView txtTitleLeft, txtDescriptionLeft, txtTitleRight, txtDescriptionRight;
        ImageView imgLeft, imgRight;

        public ViewHolder(View v) {
            super(v);
            linMain = (LinearLayout) v.findViewById(R.id.linMain);
            txtTitleLeft = (TextView) v.findViewById(R.id.txtTitleLeft);
            txtDescriptionLeft = (TextView) v.findViewById(R.id.txtDescriptionLeft);
            imgLeft = (ImageView) v.findViewById(R.id.imgLeft);
            linLeft = (LinearLayout) v.findViewById(R.id.linLeft);

            txtTitleRight = (TextView) v.findViewById(R.id.txtTitleRight);
            txtDescriptionRight = (TextView) v.findViewById(R.id.txtDescriptionRight);
            imgRight = (ImageView) v.findViewById(R.id.imgRight);
            linRight = (LinearLayout) v.findViewById(R.id.linRight);

        }
    }

}
