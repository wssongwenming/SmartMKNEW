package com.dtmining.latte.mk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dtmining.latte.mk.R;

import java.util.ArrayList;

/**
 * Created by shikun on 18-5-12.
 */

public class MedicineSimpleAdapter extends BaseAdapter implements View.OnClickListener{
    private ArrayList<String> time_list;
    private ArrayList<String> count_list;
    private Context context;
    private String method;
    private Callback mcallback;

    public interface Callback {
        public void click(View v);
    }
    public MedicineSimpleAdapter(ArrayList<String> time_list,ArrayList<String> count_list, Context context, String method, Callback callback){

        this.time_list=time_list;
        this.count_list=count_list;
        this.method=method;
        this.context=context;
        this.mcallback=callback;
    }

    @Override
    public int getCount(){
        return time_list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent){
        if(this.method.equals("1")){
            Holder_Time_Add holder=null;
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.simple_item_list,parent,false);
                holder=new Holder_Time_Add();
                holder.time = (TextView) convertView.findViewById(R.id.tv_time_for_plan_set);
                holder.medicineUseCount=(EditText)convertView.findViewById(R.id.et_medicine_usecount_for_plan_set);
                holder.btn_delete = (LinearLayout) convertView.findViewById(R.id.btn_delete);
                holder.change_time = (LinearLayout) convertView.findViewById(R.id.layout_change_time);
                convertView.setTag(holder);
            }
            else
            {
                holder = (Holder_Time_Add)convertView.getTag();
            }

            holder.time.setText(time_list.get(position));
            if(count_list.size()>position) {
                holder.medicineUseCount.setText(count_list.get(position));
            }
            holder.btn_delete.setOnClickListener(this);
            holder.btn_delete.setTag(position);
            holder.change_time.setOnClickListener(this);
            holder.change_time.setTag(position);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        mcallback.click(v);
    }


    private static class Holder_Time_Add{
        TextView time;
        EditText medicineUseCount;
        LinearLayout btn_delete;
        LinearLayout change_time;
    }
}
