package com.dtmining.latte.mk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.tools.Icon;


import java.util.ArrayList;

/**
 * Created by shikun on 18-6-19.
 */

public class HorizontalAdapter extends BaseAdapter{
    private ArrayList<Icon> data;
    private Context context;
    public HorizontalAdapter(ArrayList<Icon> data, Context context){

        this.data=data;
        this.context=context;
    }
    private static final RequestOptions REQUEST_OPTIONS=
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();
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

            convertView = LayoutInflater.from(context).inflate(R.layout.horizontal_item_medicine,parent,false);
            holder=new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.imageView=(ImageView) convertView.findViewById(R.id.img_icon);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        Log.d("url", data.get(position).url);
        holder.name.setText(data.get(position).name);
        Glide.with(context)
                .load(data.get(position).url)
                .apply(REQUEST_OPTIONS)
                .into(holder.imageView);
         return convertView;



    }
    static class ViewHolder{
        TextView name;
        ImageView imageView;

    }

}
