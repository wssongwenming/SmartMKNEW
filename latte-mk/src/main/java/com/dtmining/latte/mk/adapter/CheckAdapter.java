package com.dtmining.latte.mk.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;

import java.util.ArrayList;
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
    private List<Integer> seletor;
    private List<MedicineState> seletorString = new ArrayList<>();
    private List<String>doseUnit=new ArrayList<>();


    public CheckAdapter(Context mContext, List<MedicineState> mDatas, CheckItemListener mCheckListener) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        seletor = new ArrayList<>();
        this.mCheckListener = mCheckListener;
    }

    public void addData(MedicineState str) {
        mDatas.add(mDatas.size(), str);
        notifyItemInserted(mDatas.size());
    }

    public void addDatas(List<MedicineState> strs) {
        mDatas.addAll(mDatas.size(), strs);
        //方法一
        notifyItemInserted(mDatas.size());
        //方法二
//        notifyItemRangeInserted(datas.size(),strs.size());
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
        holder.tv_add_plan_by_time_medicine_name.setText(medicineState.getMedicineName()+"("+medicineState.getDoseUnit()+")");

        if (holder.et_add_plan_by_time_medicine_usecount.getTag() instanceof TextWatcher) {
            holder.et_add_plan_by_time_medicine_usecount.removeTextChangedListener((TextWatcher) holder.et_add_plan_by_time_medicine_usecount.getTag());
        }
        holder.et_add_plan_by_time_medicine_usecount.setText(mDatas.get(position).getMedicineUseCount());
        holder.cb_add_plan_by_time_checkbox.setTag(new Integer(position));//设置tag 否则划回来时选中消失
        //checkbox  复用问题
        if (seletor != null) {
            holder.cb_add_plan_by_time_checkbox.setChecked((seletor.contains(new Integer(position)) ? true : false));
        } else {
            holder.cb_add_plan_by_time_checkbox.setChecked(false);
        }
        holder.cb_add_plan_by_time_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!seletor.contains(holder.cb_add_plan_by_time_checkbox.getTag())) {
                        seletor.add(new Integer(position));
                        seletorString.add(mDatas.get(position));
                    }
                } else {
                    if (seletor.contains(holder.cb_add_plan_by_time_checkbox.getTag())) {
                        seletor.remove(new Integer(position));
                        seletorString.remove(mDatas.get(position));
                    }
                }
                if (null != mCheckListener) {
                    mCheckListener.itemChecked(seletor,seletorString);
                }
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mDatas.get(position).setMedicineUseCount(holder.et_add_plan_by_time_medicine_usecount.getText().toString().trim());
            }
        };
        holder.et_add_plan_by_time_medicine_usecount.addTextChangedListener(textWatcher);
        holder.et_add_plan_by_time_medicine_usecount.setTag(textWatcher);


        //holder.et_add_plan_by_time_medicine_usecount.setText(null);
        //holder.cb_add_plan_by_time_checkbox.setChecked(false);
        //点击实现选择功能，当然可以把点击事件放在item_cb对应的CheckBox上，只是焦点范围较小

    }

    /**
     * 获取选中数据源
     */
    public List<MedicineState> getSeletorData() {
        return seletorString;
    }

    /**
     * 获取选中位置
     */
    public List<Integer> getSeletorDataPos() {
        return seletor;
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
        void itemChecked(List<Integer> seletor,List<MedicineState> seletorString);
    }
}