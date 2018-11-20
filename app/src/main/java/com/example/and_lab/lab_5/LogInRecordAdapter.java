package com.example.and_lab.lab_5;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LogInRecordAdapter extends ArrayAdapter<LogInRecord> {

    private Context mContext;
    private List<LogInRecord> recordList = new ArrayList<>();

    public LogInRecordAdapter(Context context, ArrayList<LogInRecord> list) {
        super(context, 0 , list);
        mContext = context;
        recordList = list;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.db_item,parent,false);

        LogInRecord currentRecord = recordList.get(position);

        TextView id = (TextView) listItem.findViewById(R.id.id_t);
        id.setText(currentRecord.get_id());

        TextView username = (TextView) listItem.findViewById(R.id.username_t);
        username.setText(currentRecord.getUsername());

        TextView timestamp = (TextView) listItem.findViewById(R.id.timestamp_t);
        timestamp.setText(currentRecord.getTimestamp());

        TextView longitude = (TextView) listItem.findViewById(R.id.longitude_t);
        longitude.setText(currentRecord.getLongitude());

        TextView latitude = (TextView) listItem.findViewById(R.id.latitude_t);
        latitude.setText(currentRecord.getLatitude());

        return listItem;
    }

}
