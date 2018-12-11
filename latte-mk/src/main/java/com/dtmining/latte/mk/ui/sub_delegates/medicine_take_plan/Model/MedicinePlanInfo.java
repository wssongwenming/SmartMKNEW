package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/12/11
 * Description:
 */
public class MedicinePlanInfo {
    private String timeString;
    private List<com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicinePlan> datas;

    public String getTimeString() {
        return timeString;
    }

    public List<com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicinePlan> getDatas() {
        return datas;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public void setDatas(List<com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicinePlan> datas) {
        this.datas = datas;
    }
}
