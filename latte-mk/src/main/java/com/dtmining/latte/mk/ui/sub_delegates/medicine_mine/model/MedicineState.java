package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model;

/**
 * author:songwenming
 * Date:2018/10/29
 * Description:
 */
public class MedicineState {
    public String medicineId;
    public String medicinePause;
    public String boxId;
    public String tel;
    private boolean isChecked;
    private String medicineUseCount;
   public String medicineName;

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicinePause() {
        return medicinePause;
    }

    public void setMedicinePause(String medicinePause) {
        this.medicinePause = medicinePause;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getBoxId() {
        return boxId;
    }

    public String getMedicineUseCount() {
        return medicineUseCount;
    }

    public void setMedicineUseCount(String medicineUseCount) {
        this.medicineUseCount = medicineUseCount;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }
}
