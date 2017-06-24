package com.wscubetech.seovideotutorials.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.model.TrainingPointsModel;

import java.util.ArrayList;

/**
 * Created by wscube on 15/12/15.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {

    Context context;

    ArrayList<TrainingPointsModel> arrOfData = new ArrayList<>();

    String description = "";

    public ExpandableAdapter(Context context, ArrayList<TrainingPointsModel> arrOfData, String desc) {
        this.context = context;
        this.arrOfData = arrOfData;
        this.description = desc;

    }

    @Override
    public int getGroupCount() {
        return arrOfData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return arrOfData.get(i).getArrayOfPoints().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return arrOfData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return arrOfData.get(groupPosition).getArrayOfPoints().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        TrainingPointsModel data = (TrainingPointsModel) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_parent_point, null);
        }

        final View vu = convertView;

        TextView txtHeading = (TextView) vu.findViewById(R.id.textview);
        TextView txtDesc = (TextView) vu.findViewById(R.id.txt);


        if (groupPosition == 0) {
            txtDesc.setVisibility(View.VISIBLE);
            txtDesc.setText(description);
        } else {
            txtDesc.setVisibility(View.GONE);
            txtDesc.setText("");
        }


        txtHeading.setText(data.getHeading().toString().trim());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String data = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_child_point, null);
        }


        TextView txtPoint = (TextView) convertView.findViewById(R.id.textview);
        txtPoint.setText(data);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
