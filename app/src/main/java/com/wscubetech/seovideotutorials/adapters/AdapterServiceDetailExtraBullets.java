package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.wscubetech.seovideotutorials.R;

import java.util.ArrayList;

/**
 * Created by wscubetech on 3/6/16.
 */
public class AdapterServiceDetailExtraBullets extends RecyclerView.Adapter<AdapterServiceDetailExtraBullets.ViewHolder> {

    Activity activity;
    ArrayList<String> arrayList = new ArrayList<>();

    public AdapterServiceDetailExtraBullets(Activity activity, ArrayList arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(activity).inflate(R.layout.row_service_detail_extra_bullets,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String strInfo=arrayList.get(position);
        holder.txtInfo.setText(strInfo);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtInfo;

        public ViewHolder(View v) {
            super(v);
            txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        }
    }
}
