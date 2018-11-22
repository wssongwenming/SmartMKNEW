package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model;

/**
 * author:songwenming
 * Date:2018/11/22
 * Description:
 */
public class Detail {
    String medicineId=null;
    MedicinePlan medicine_plan=null;

    public String getMedicineId() {
        return medicineId;
    }

    public MedicinePlan getMedicine_plan() {
        return medicine_plan;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public void setMedicine_plan(MedicinePlan medicine_plan) {
        this.medicine_plan = medicine_plan;
    }
}
