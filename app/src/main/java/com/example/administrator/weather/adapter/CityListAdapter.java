package com.example.administrator.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.weather.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class CityListAdapter extends BaseAdapter {

    private List<String> list;
    private LayoutInflater mInflater;

    public CityListAdapter(Context context, List<String> list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        if (convertView == null) {
            rowView = mInflater.inflate(R.layout.item_city_list, null);
        } else {
            rowView = convertView;
        }
        TextView tv_city = (TextView) rowView.findViewById(R.id.tv_city);
        tv_city.setText(getItem(position));

        return rowView;
    }
}
