package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import java.io.Serializable;

/**
 * author:songwenming
 * Date:2018/11/5
 * Description:
 * //
 */
public class MedicinePlan implements Serializable {
    private String id;//planId
    private int dayInterval;// 服药的周期间隔是每天、隔天或其它
    private String atime=null;
    private int medicineUseCount;
    private String medicineName=null;
    private String endRemind=null;
    private String startRemind=null;
    private String medicineId=null;
    private String boxId=null;

    public String getId() {
        return id;
    }

    public int getDayInterval() {
        return dayInterval;
    }

    public String getAtime() {
        return atime;
    }

    public int getMedicineUseCount() {
        return medicineUseCount;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getEndRemind() {
        return endRemind;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getStartRemind() {
        return startRemind;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDayInterval(int dayInterval) {
        this.dayInterval = dayInterval;
    }

    public void setAtime(String atime) {
        this.atime = atime;
    }

    public void setMedicineUseCount(int medicineUseCount) {
        this.medicineUseCount = medicineUseCount;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setEndRemind(String endRemind) {
        this.endRemind = endRemind;
    }

    public void setStartRemind(String startRemind) {
        this.startRemind = startRemind;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public MedicinePlan() {
    }
}
