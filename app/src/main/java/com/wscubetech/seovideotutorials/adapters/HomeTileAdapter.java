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

import java.util.ArrayList;

/**
 * Created by wscubetech on 10/3/17.
 */

public class HomeTileAdapter extends RecyclerView.Adapter<HomeTileAdapter.ViewHolder> {

    String[] arrayTitles;
    int[] arrayBgColors;
    ArrayList<Integer> arrayImages = new ArrayList<>();
    Activity activity;


    private int lastPosition = -1;
    private int minDuration = 600;

    public HomeTileAdapter(Activity activity, String[] arrayTitles, int[] arrayBgColors, ArrayList<Integer> arrayImages) {
        this.activity = activity;
        this.arrayTitles = arrayTitles;
        this.arrayImages = arrayImages;
        this.arrayBgColors = arrayBgColors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_main_home_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.linMain.setBackgroundColor(arrayBgColors[position]);
        holder.txt.setText(arrayTitles[position].toUpperCase().trim());
        holder.img.setImageResource(arrayImages.get(position));


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
            if (minDuration > 1500) {
                minDuration = 600;
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayTitles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linMain;
        TextView txt;
        ImageView img;

        public ViewHolder(View v) {
            super(v);
            linMain = (LinearLayout) v.findViewById(R.id.linMain);
            txt = (TextView) v.findViewById(R.id.txt);
            img = (ImageView) v.findViewById(R.id.img);

        }
    }

}
