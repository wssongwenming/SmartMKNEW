package com.dtmining.latte.mk.main.aboutme.mymedicineboxes;

import android.provider.ContactsContract;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;

/**
 * author:songwenming
 * Date:2018/11/4
 * Description:
 */
public class MedicineBoxesMineDataConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        return null;
    }
    @Override
    public ArrayList<MultipleItemEntity> convert() {
        if(getJsonData()!=null) {
            final JSONObject jsonObject = JSON.parseObject(getJsonData());
            String tel = jsonObject.getString("tel");
            final JSONArray dataArray = jsonObject.getJSONArray("detail");
            final int size = dataArray.size();
            for (int i = 0; i <size ; i++) {
                JSONObject data = (JSONObject) dataArray.get(i);
                String onUse=data.getString("onUse");
                String pause=data.getString("pause");
                String boxId=data.getString("boxId");
                String overDue=data.getString("overDue");
                int type = ItemType.MEDICINE_BOX;
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, type)
                        .setField(MultipleFields.ONUSE,onUse)
                        .setField(MultipleFields.PAUSE,pause)
                        .setField(MultipleFields.TEL,tel)
                        .setField(MultipleFields.BOXID,boxId)
                        .setField(MultipleFields.OVERDUE,overDue)
                        .build();
                ENTITIES.add(entity);
            }
        }
        return ENTITIES;
    }
}
