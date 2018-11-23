package com.dtmining.latte.mk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by shikun on 18-7-17.
 */

public class CheckBoxAdapter extends BaseAdapter {
    private Context context;
    private String[] beans;
    private LinkedList<MedicineState> medicines;//借用了MedicineState,字段名满足需要

    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> selected;


    public CheckBoxAdapter(Context context, LinkedList<MedicineState> medicines) {
        // TODO Auto-generated constructor stub
        this.medicines = medicines;
        this.context = context;
        selected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < medicines.size(); i++) {
            getSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return medicines.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return medicines.get(position);
    }

    public HashMap<Integer,Boolean> getHashMap(){
        return selected;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // 页面
        ViewHolder holder;
        MedicineState medicine=medicines.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.item_check_box, null);
            holder = new ViewHolder();
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb_add_plan_by_time_checkbox);
            holder.medicineName = (TextView) convertView
                    .findViewById(R.id.tv_add_plan_by_time_medicine_name);
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }

        holder.medicineName.setText(medicine.getMedicineName());
        // 监听checkBox并根据原来的状态来设置新的状态
        holder.cb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (selected.get(position)) {
                    selected.put(position, false);
                    setSelected(selected);
                } else {
                    selected.put(position, true);
                    setSelected(selected);
                }

            }
        });

        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getSelected().get(position));

        return convertView;
    }

    public static HashMap<Integer, Boolean> getSelected() {
        return selected;
    }

    public static void setSelected(HashMap<Integer, Boolean> selected) {
        CheckBoxAdapter.selected = selected;
    }
    class ViewHolder {
        EditText medicineUseCount;
        TextView medicineName;
        CheckBox cb;
    }

}
