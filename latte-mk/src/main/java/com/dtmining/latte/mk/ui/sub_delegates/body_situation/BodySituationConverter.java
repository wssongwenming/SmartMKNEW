package com.dtmining.latte.mk.ui.sub_delegates.body_situation;

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
 * Date:2018/11/30
 * Description:
 */
public class BodySituationConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        return null;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        if(getJsonData()!=null) {
            final JSONObject jsonObject = JSON.parseObject(getJsonData());
            int code=jsonObject.getIntValue("code");
            final JSONObject detail = jsonObject.getJSONObject("detail");
            final JSONArray situations=detail.getJSONArray("medicines");
            final int size = situations.size();
            for (int i = 0; i < size; i++) {
                JSONObject data = (JSONObject) situations.get(i);
                final String medicineUseTime=data.getString("medicineUseTime");
                final String medicineName=data.getString("medicineName");
                final String reaction=data.getString("reaction");
                int type = ItemType.MEDICINE_MINE;
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, type)
                        .setField(MultipleFields.MEDICINE_USETIME, medicineUseTime)
                        .setField(MultipleFields.MEDICINE_NAME, medicineName)
                        .setField(MultipleFields.REACTION, reaction)
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
