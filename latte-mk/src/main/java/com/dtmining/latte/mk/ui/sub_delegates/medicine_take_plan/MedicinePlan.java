package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import java.io.Serializable;

/**
 * author:songwenming
 * Date:2018/11/5
 * Description:
 */
public class MedicinePlan implements Serializable {
    private String startRemind=null;//开始提示时间
    private String interval=null;// 服药的周期间隔是每天、隔天或其它
    private String atime=null;
    private String medicineUseCount=null;
    private String medicineName=null;
    private String medicineId=null;



    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getAtime() {
        return atime;
    }

    public String getMedicineUseCount() {
        return medicineUseCount;
    }

    public void setMedicineUseCount(String medicineUseCount) {
        this.medicineUseCount = medicineUseCount;
    }

    public String getMedicineName() {
        return medicineName;
    }
    public String getMedicineId() {
        return medicineId;
    }

    public void setAtime(String atime) {
        this.atime = atime;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public MedicinePlan(String atime, String medicineName, String medicineCount, String medicineId) {
        this.atime = atime;
        this.medicineName = medicineName;
        this.medicineId = medicineId;
    }

    public MedicinePlan() {
    }
}
