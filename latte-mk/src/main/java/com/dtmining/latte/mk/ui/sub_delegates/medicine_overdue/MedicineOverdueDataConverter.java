package com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/11/19
 * Description:
 */
public class MedicineOverdueDataConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        return null;
    }
    public ArrayList<MultipleItemEntity> getMedicineOverdue() {
        //getMedicineHistoryMore();
        convertMedicineOverdue();
        return ENTITIES;
    }

    private ArrayList<MultipleItemEntity> convertMedicineOverdue() {
        if(getJsonData()!=null) {
            JSONObject jsonobject = JSON.parseObject(getJsonData());
            JSONArray jsonArray = jsonobject.getJSONArray("detail");
            int size=jsonArray.size();
            for (int i = 0; i <size ; i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String medicineImg=jsonObject.getString("medicineImage");
                String medicineName=jsonObject.getString("medicineName");
                String validity=jsonObject.getString("medicineValidity");
                String medicineCount=jsonObject.getString("medicineCount");
                String medicineId=jsonObject.getString("medicineId");
                int medicineType=jsonObject.getIntValue("medicineType");
                String boxId=jsonObject.getString("boxId");
                String tel=jsonObject.getString("tel");
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE,ItemType.MEDICINE_OVER_DUE)
                        .setField(MultipleFields.MEDICINEIMGURL, UploadConfig.UPLOAD_IMG+medicineImg)
                        .setField(MultipleFields.MEDICINENAME,medicineName)
                        .setField(MultipleFields.MEDICINEVALIDITY,validity)
                        .setField(MultipleFields.MEDICINECOUNT,medicineCount)
                        .setField(MultipleFields.MEDICINETYPE,medicineType)
                        .setField(MultipleFields.MEDICINEID,medicineId)
                        .setField(MultipleFields.TEL,tel)
                        .setField(MultipleFields.BOXID,boxId)
                        .build();
                ENTITIES.add(entity);
            }
        }
        return ENTITIES;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        return null;
    }

    @Override
    public ArrayList<MultipleItemEntity> convertMedicineHistory() {
        return null;
    }

    @Override
    public ArrayList<MultipleItemEntity> convertMedicinePlan() {
        return null;
    }

    @Override
    public List<MultipleItemEntity> getTop() {
        return null;
    }
}
