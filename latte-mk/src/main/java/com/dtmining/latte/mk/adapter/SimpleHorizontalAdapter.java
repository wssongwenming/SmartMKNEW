package com.dtmining.latte.mk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.dtmining.latte.mk.R;

import java.util.ArrayList;

/**
 * Created by shikun on 18-7-17.
 */

public class SimpleHorizontalAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private Context context;

    public SimpleHorizontalAdapter(ArrayList<String> data, Context context){
        this.data=data;
        this.context=context;
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder=null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_single_wrap_content,parent,false);
            holder=new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.wrap_content_single_item_tv);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.name.setText(data.get(position));
        return convertView;
    }
    static class ViewHolder{
        TextView name;
    }

}
