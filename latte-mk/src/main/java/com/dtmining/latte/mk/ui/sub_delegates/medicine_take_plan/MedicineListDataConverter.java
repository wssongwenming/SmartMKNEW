package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;

/**
 * author:songwenming
 * Date:2018/10/23
 * Description:
 */
public class MedicineListDataConverter {
    protected final LinkedList<MedicineModel> MEDICINES=new LinkedList<>();
    private String mJsonData=null;
    public LinkedList<MedicineModel> getEntities() {
        convert();
        return MEDICINES;
    }


    public LinkedList<MedicineModel> convert(){
        JSONObject jsonobject= JSON.parseObject(getJsonData());
        MedicineModel medicineModel=new MedicineModel();
        medicineModel.setMedicineName("请选择药品");
        MEDICINES.add(medicineModel);
        if(getJsonData()!=null) {
            final JSONObject jsonObject = JSON.parseObject(getJsonData());
            String tel = jsonObject.getString("tel");
            final JSONArray dataArray = jsonObject.getJSONArray("detail");
            final int size = dataArray.size();
            for (int i = 0; i <size ; i++) {
                MedicineModel medicineModel1 = new MedicineModel();
                JSONObject jsondata = (JSONObject) dataArray.get(i);
                medicineModel1.setMedicineName(jsondata.getString("medicineName"));
                medicineModel1.setBoxId(jsondata.getString("boxId"));
                medicineModel1.setMedicineId(jsondata.getString("medicineId"));
                JSONArray planArray = jsondata.getJSONArray("medicinePlan");
                if (planArray != null) {
                    final int size1 = planArray.size();
                        if (size1 > 0) {
                        MedicinePlans medicinePlans = new MedicinePlans();
                        for (int j = 0; j < size1; j++) {
                            MedicinePlan medicinePlan = new MedicinePlan();
                            JSONObject planobject = (JSONObject) planArray.get(j);
                            String atime = planobject.getString("atime");
                            String medicineId = planobject.getString("medicineId");
                            //int medicineUseCount=planobject.getInteger("medicineUseCount");//等待后期加入该返回字段
                            String id=planobject.getString("id");
                            medicinePlan.setId(id);
                            medicinePlan.setAtime(atime);
                            //medicinePlan.setMedicineUseCount(medicineUseCount);//等待后期加入该返回字段
                            medicinePlans.addMedicinePlan(medicinePlan);
                        }
                            Log.d("size", medicinePlans.plans.get(0).getAtime());
                        medicineModel1.setMedicinePlans(medicinePlans);
                    }
                    MEDICINES.add(medicineModel1);
                }
            }
        }
        return MEDICINES;
    }
    public MedicineListDataConverter setJsonData(String json){
        this.mJsonData=json;
        return this;
    }
    protected String getJsonData(){
        if(mJsonData==null||mJsonData.isEmpty())
        {
         //   throw new NullPointerException("DATA IS NULL");
        }
        return mJsonData;
    }
}
