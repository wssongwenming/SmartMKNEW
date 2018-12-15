package com.dtmining.latte.mk.ui.sub_delegates.hand_add.model;

import java.io.Serializable;

/**
 * author:songwenming
 * Date:2018/11/18
 * Description:
 */
public class MedicineModel implements Serializable {
    public String boxId;
    public String medicineId;
    public int medicineCount;
    public String medicineName;
    public String medicineCode;
    public String medicineValidity;
    public String startRemind;
    public String endRemind;
    public int dayInterval;
    public int timesOnDay;
    public int medicineUseCount;
    public String medicineImage;
    public String tel;

    public String getBoxId() {
        return boxId;
    }

    public int getMedicineCount() {
        return medicineCount;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public String getMedicineValidity() {
        return medicineValidity;
    }

    public String getStartRemind() {
        return startRemind;
    }

    public String getEndRemind() {
        return endRemind;
    }

    public int getDayInterval() {
        return dayInterval;
    }

    public int getTimesOnDay() {
        return timesOnDay;
    }

    public int getMedicineUseCount() {
        return medicineUseCount;
    }

    public String getMedicineImage() {
        return medicineImage;
    }

    public String getTel() {
        return tel;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public void setMedicineCount(int medicineCount) {
        this.medicineCount = medicineCount;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public void setMedicineValidity(String medicineValidity) {
        this.medicineValidity = medicineValidity;
    }

    public void setStartRemind(String startRemind) {
        this.startRemind = startRemind;
    }

    public void setEndRemind(String endRemind) {
        this.endRemind = endRemind;
    }

    public void setDayInterval(int dayInterval) {
        this.dayInterval = dayInterval;
    }

    public void setTimesOnDay(int timesOnDay) {
        this.timesOnDay = timesOnDay;
    }

    public void setMedicineUseCount(int medicineUseCount) {
        this.medicineUseCount = medicineUseCount;
    }

    public void setMedicineImage(String medicineImage) {
        this.medicineImage = medicineImage;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }
}
