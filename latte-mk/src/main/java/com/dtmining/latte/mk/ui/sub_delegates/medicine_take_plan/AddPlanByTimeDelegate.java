package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;
import com.dtmining.latte.mk.ui.sub_delegates.views.CheckMedicinesDialog;
import com.dtmining.latte.mk.ui.sub_delegates.views.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/2
 * Description:
 */
public class AddPlanByTimeDelegate extends LatteDelegate implements View.OnClickListener, CheckMedicinesDialog.ClickListenerInterface{
    private CheckMedicinesDialog checkMedicinesDialog;
    private CustomDatePicker customDatePicker;
    @OnClick(R2.id.tv_plan_add_by_time_set_time)
    void setTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker.show(now);
    }
    @BindView(R2.id.tv_plan_add_by_time_set_time)
    TextView mMedicineUseTime=null;
    @OnClick(R2.id.tv_plan_set_by_time_set_medicines)
    void setMedicine(){
        checkMedicinesDialog = new CheckMedicinesDialog(getContext(),AddPlanByTimeDelegate.this);
        checkMedicinesDialog.show();
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan_add_by_time;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatePicker();
    }
    private void initDatePicker(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker = new CustomDatePicker(this.getContext(), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                Log.d("time", time);
                mMedicineUseTime.setText(time);
                //time_list.add(time);
                //Toast.makeText(getContext(),time,Toast.LENGTH_LONG).show();

            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(true); // 显示时和分
        customDatePicker.setIsLoop(true); // 允许循环滚动
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void doConfirm(List<MedicineState> input) {

    }
}
