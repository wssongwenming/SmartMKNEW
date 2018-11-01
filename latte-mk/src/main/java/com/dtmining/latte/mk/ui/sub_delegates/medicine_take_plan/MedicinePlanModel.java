package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

/**
 * author:songwenming
 * Date:2018/10/31
 * Description:
 */
public class MedicinePlanModel  {
    private  String medicine_useCount;
    private String atime;
    private String medicine_name;

    public String getMedicineUseCount() {
        return medicine_useCount;
    }

    public String getAtime() {
        return atime;
    }

    public String getMedicineNname() {
        return medicine_name;
    }

    public void setMedicineUseCount(String medicine_useCount) {
        this.medicine_useCount = medicine_useCount;
    }

    public void setAtime(String atime) {
        this.atime = atime;
    }

    public void setMedicineName(String medicine_name) {
        this.medicine_name = medicine_name;
    }
}
