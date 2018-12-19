package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

/**
 * author:songwenming
 * Date:2018/11/5
 * Description:
 */
/*
"medicineValidity": "2018-07-20",
        "medicineId": "24c7e5416d9547e6a2ab410387e5d4bb",
        "medicineCount": 50,
        "medicineName": "牛黄上清片1",
        "boxId": "102",
        "medicinePause": 2,
        "medicinePlan":*/

public class MedicineModel {
    private String medicineValidity;
    private String medicineName;
    private String medicineId;
    private String boxId;
    private int medicintType;
    private String medicinePause;
    private String medicineCount;
    private String tel;
    private MedicinePlans medicinePlans;

    public MedicineModel(String medicineName, String medicineId, String boxId, String medicinePause, String medicineCount, String tel, MedicinePlans medicinePlans) {
        this.medicineName = medicineName;
        this.medicineId = medicineId;
        this.boxId = boxId;
        this.medicinePause = medicinePause;
        this.medicineCount = medicineCount;
        this.tel = tel;
        this.medicinePlans = medicinePlans;
    }

    @Override
    public String toString() {
        return  medicineName;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getBoxId() {
        return boxId;
    }

    public String getMedicinePause() {
        return medicinePause;
    }

    public String getMedicineCount() {
        return medicineCount;
    }

    public String getTel() {
        return tel;
    }

    public MedicinePlans getMedicinePlans() {
        return medicinePlans;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public void setMedicinePause(String medicinePause) {
        this.medicinePause = medicinePause;
    }

    public void setMedicineCount(String medicineCount) {
        this.medicineCount = medicineCount;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMedicineValidity() {
        return medicineValidity;
    }

    public int getMedicintType() {
        return medicintType;
    }

    public void setMedicineValidity(String medicineValidity) {
        this.medicineValidity = medicineValidity;
    }

    public void setMedicintType(int medicintType) {
        this.medicintType = medicintType;
    }

    public void setMedicinePlans(MedicinePlans medicinePlans) {
        this.medicinePlans = medicinePlans;
    }

    public MedicineModel() {
    }
}
