package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/2
 * Description:
 */
public class MedicinePlanAddChoiceDelegate extends LatteDelegate {
    @OnClick(R2.id.add_plan_by_drugs)
    void addPlanByDrugs(){
        startWithPop(new AddPlanByDrugDelegate());
    }
    @OnClick(R2.id.add_plan_by_time)
    void addPlanByTime(){
        startWithPop(new AddPlanByTimeDelegate());
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan_add_choice;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
