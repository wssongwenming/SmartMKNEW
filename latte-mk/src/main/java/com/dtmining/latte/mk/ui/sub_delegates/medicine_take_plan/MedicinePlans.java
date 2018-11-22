package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import java.util.ArrayList;

/**
 * author:songwenming
 * Date:2018/11/5
 * Description:
 */
public class MedicinePlans {
    public ArrayList<MedicinePlan> plans;

    public void Medicineplans(ArrayList<MedicinePlan> plans) {
        this.plans = plans;
    }
    public void addMedicinePlan(MedicinePlan medicinePlan){
        if(plans==null){
            plans=new ArrayList<MedicinePlan>();
        }
        this.plans.add(medicinePlan);
    }
    public ArrayList<String> getTime() {
        ArrayList<String> medicine_time=new ArrayList<>();
        for(int i=0;i<plans.size();i++){
            medicine_time.add(plans.get(i).getAtime());
        }
        return medicine_time;
    }
    public ArrayList<String> getUseCount() {
        ArrayList<String> use_count=new ArrayList<>();
        for(int i=0;i<plans.size();i++){
            use_count.add(plans.get(i).getMedicineUseCount()+"");
        }
        return use_count;
    }
}
