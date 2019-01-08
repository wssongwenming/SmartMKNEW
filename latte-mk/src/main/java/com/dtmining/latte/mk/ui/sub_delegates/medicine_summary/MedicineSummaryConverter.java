package com.dtmining.latte.mk.ui.sub_delegates.medicine_summary;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/11/25
 * Description:
 */
public class MedicineSummaryConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        return null;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        return null;
    }

    public ArrayList<MultipleItemEntity> getMedicineSummary() {
        //getMedicineHistoryMore();
        convertMedicineSummary();
        return ENTITIES;
    }

    private ArrayList<MultipleItemEntity> convertMedicineSummary() {
        ENTITIES.clear();
        if(getJsonData()!=null) {
            JSONObject jsonobject = JSON.parseObject(getJsonData());
            JSONObject jsonObject1 = jsonobject.getJSONObject("detail");
            JSONArray jsonArray= jsonObject1.getJSONArray("medicines");
            int size=jsonArray.size();
            for (int i = 0; i <size ; i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                int medicine_userCount=jsonObject.getIntValue("useCount");
                int medicineType=jsonObject.getIntValue("medicine_type");
                //long medicine_userTime=jsonObject.getLong("medicine_userTime");
                int days=jsonObject.getIntValue("days");
                String medicine_name=jsonObject.getString("medicine_name");

                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, ItemType.MEDICINE_SUMMARY)
                        .setField(MultipleFields.MEDICINE_NAME,medicine_name)
                        .setField(MultipleFields.MEDICINE_USERTIME,days)
                        .setField(MultipleFields.MEDICINETYPE,medicineType)
                        .setField(MultipleFields.MEDICINE_USERCOUNT,medicine_userCount)
                         .build();
                ENTITIES.add(entity);
            }
        }
        return ENTITIES;
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
