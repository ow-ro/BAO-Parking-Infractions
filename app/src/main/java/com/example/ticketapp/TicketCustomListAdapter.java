package com.example.ticketapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TicketCustomListAdapter extends BaseAdapter {

    private ArrayList<Ticket> ticketListData;
    private LayoutInflater layoutInflater;
    DatabaseHelper mDatabaseHelper;

    public TicketCustomListAdapter(Context context, ArrayList<Ticket> ticketListData) {
        this.ticketListData = ticketListData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ticketListData.size();
    }

    @Override
    public Object getItem(int position) {
        return ticketListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.ticket_list_row_layout_rev, null);
            holder = new ViewHolder();
            holder.plateView = convertView.findViewById(R.id.ticketListPlate);
            holder.stateView = convertView.findViewById(R.id.ticketListState);
            holder.dateTimeView = convertView.findViewById(R.id.ticketListDateTime);
            holder.infractionView = convertView.findViewById(R.id.ticketListInfraction);
            holder.locationView = convertView.findViewById(R.id.ticketListLocation);
            holder.typeButtonView = convertView.findViewById(R.id.typeButton);
            holder.towedIcon = convertView.findViewById(R.id.towIcon);
            holder.numberIcon = convertView.findViewById(R.id.ticketNumber);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.numberIcon.setVisibility(View.VISIBLE);

        holder.plateView.setText(ticketListData.get(position).getPlate());
        holder.stateView.setText(ticketListData.get(position).getState());
        holder.dateTimeView.setText(ticketListData.get(position).getDateTime());
        holder.infractionView.setText(ticketListData.get(position).getInfraction());
        holder.locationView.setText(ticketListData.get(position).getLocation());
        if (ticketListData.get(position).getIsTowed() == 1) {
            //vehicle was towed
            holder.towedIcon.setVisibility(View.VISIBLE);
        } else if (ticketListData.get(position).getIsTowed() == 0) {
            holder.towedIcon.setVisibility(View.INVISIBLE);
        } else if (ticketListData.get(position).getIsTowed() == 3) {
            //exception, so not required
            holder.towedIcon.setVisibility(View.INVISIBLE);
        }

        if (ticketListData.get(position).getType() == 1) {
            //type: ticket
            holder.typeButtonView.setBackgroundResource(R.drawable.ic_assignment_24px);
        } else if (ticketListData.get(position).getType() == 2) {
            //type: exception
            holder.typeButtonView.setBackgroundResource(R.drawable.ic_how_to_reg_24px);
            holder.numberIcon.setVisibility(View.INVISIBLE);
        }

        switch(ticketListData.get(position).getCount()) {
            case 1:
                holder.numberIcon.setBackgroundResource(R.drawable.one);
                break;
            case 2:
                holder.numberIcon.setBackgroundResource(R.drawable.two);
                break;
            case 3:
                holder.numberIcon.setBackgroundResource(R.drawable.three);
                break;
            case 4:
                holder.numberIcon.setBackgroundResource(R.drawable.four);
                break;
            case 5:
                holder.numberIcon.setBackgroundResource(R.drawable.five);
                break;
            default:
                holder.numberIcon.setBackgroundResource(R.drawable.one);
        }


        return convertView;
    }

    private static class ViewHolder {
        TextView plateView;
        TextView stateView;
        TextView dateTimeView;
        TextView infractionView;
        TextView locationView;
        Button typeButtonView;
        ImageView towedIcon;
        ImageView numberIcon;
    }
}
