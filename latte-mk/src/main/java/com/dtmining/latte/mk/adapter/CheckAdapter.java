package com.dtmining.latte.mk.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/11/23
 * Description:
 */
public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {
    private Context mContext;
    private List<MedicineState> mDatas;
    private CheckItemListener mCheckListener;

    public CheckAdapter(Context mContext, List<MedicineState> mDatas, CheckItemListener mCheckListener) {
        this.mContext = mContext;
        this.mDatas = mDatas;

        this.mCheckListener = mCheckListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_check_box, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MedicineState medicineState = mDatas.get(position);
        holder.tv_add_plan_by_time_medicine_name.setText(medicineState.getMedicineName());
        //holder.et_add_plan_by_time_medicine_usecount.setText(null);
        //holder.cb_add_plan_by_time_checkbox.setChecked(false);
        //点击实现选择功能，当然可以把点击事件放在item_cb对应的CheckBox上，只是焦点范围较小
        holder.cb_add_plan_by_time_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medicineState.setChecked(!medicineState.isChecked());
                holder.cb_add_plan_by_time_checkbox.setChecked(medicineState.isChecked());
                if (null != mCheckListener) {
                    mCheckListener.itemChecked(medicineState,position,holder.cb_add_plan_by_time_checkbox.isChecked());
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //
        private AppCompatTextView tv_add_plan_by_time_medicine_name;
        private AppCompatEditText et_add_plan_by_time_medicine_usecount;
        //选择
        private CheckBox cb_add_plan_by_time_checkbox;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_add_plan_by_time_medicine_name = (AppCompatTextView) itemView.findViewById(R.id.tv_add_plan_by_time_medicine_name);
            et_add_plan_by_time_medicine_usecount = (AppCompatEditText) itemView.findViewById(R.id.et_add_plan_by_time_medicine_usecount);
            cb_add_plan_by_time_checkbox = (CheckBox) itemView.findViewById(R.id.cb_add_plan_by_time_checkbox);
        }
    }

    public interface CheckItemListener {

        void itemChecked(MedicineState checkBean,int position, boolean isChecked);
    }
}
