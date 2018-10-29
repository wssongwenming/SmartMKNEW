package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model;

/**
 * author:songwenming
 * Date:2018/10/29
 * Description:
 */
public class MedicineState {
    public String caseId;
    public String medicinId;
    public String medicine_pause;
    public String boxId;
    public String tel;

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getMedicinId() {
        return medicinId;
    }

    public String getMedicine_pause() {
        return medicine_pause;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setMedicinId(String medicinId) {
        this.medicinId = medicinId;
    }

    public void setMedicine_pause(String medicine_pause) {
        this.medicine_pause = medicine_pause;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }
}
