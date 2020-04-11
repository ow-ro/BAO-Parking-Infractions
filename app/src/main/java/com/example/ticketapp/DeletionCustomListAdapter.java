package com.example.ticketapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class DeletionCustomListAdapter extends BaseAdapter {

    private ArrayList<DeletionRecord> delListData;
    private LayoutInflater layoutInflater;
    DatabaseHelper mDatabaseHelper;

    public DeletionCustomListAdapter(Context context, ArrayList<DeletionRecord> delListData) {
        this.delListData = delListData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return delListData.size();
    }

    @Override
    public Object getItem(int position) {
        return delListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.deletion_list_row_layout, null);
            holder = new ViewHolder();
            holder.plateView = convertView.findViewById(R.id.delListPlate);
            holder.stateView = convertView.findViewById(R.id.delListState);
            holder.dateTimeView = convertView.findViewById(R.id.delListDateTime);
            holder.infractionView = convertView.findViewById(R.id.delListInfraction);
            holder.locationView = convertView.findViewById(R.id.delListLocation);
            holder.typeButtonView = convertView.findViewById(R.id.delTypeButton);
            holder.deletionDateTimeView = convertView.findViewById(R.id.delListDelDateTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.plateView.setText(delListData.get(position).getPlate());
        holder.stateView.setText(delListData.get(position).getState());
        holder.dateTimeView.setText("Creation time: " + delListData.get(position).getDateTime());
        holder.infractionView.setText(delListData.get(position).getInfraction());
        holder.locationView.setText(delListData.get(position).getLocation());
        if (delListData.get(position).getType() == 1) {
            //type: ticket
            holder.typeButtonView.setBackgroundResource(R.drawable.ic_assignment_24px);
        } else if (delListData.get(position).getType() == 2) {
            //type: exception
            holder.typeButtonView.setBackgroundResource(R.drawable.ic_how_to_reg_24px);
        }
        holder.deletionDateTimeView.setText("Deletion time: " + delListData.get(position).getDeletionTime());
        return convertView;
    }

    private static class ViewHolder {
        TextView plateView;
        TextView stateView;
        TextView dateTimeView;
        TextView infractionView;
        TextView locationView;
        TextView deletionDateTimeView;
        Button typeButtonView;
    }
}
