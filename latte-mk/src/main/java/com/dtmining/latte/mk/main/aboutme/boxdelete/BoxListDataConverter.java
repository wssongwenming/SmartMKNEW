package com.dtmining.latte.mk.main.aboutme.boxdelete;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/10/23
 * Description:
 */
public class BoxListDataConverter extends DataConverter {

    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        return null;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        return null;
    }
    public ArrayList<MultipleItemEntity> convertResponse(String response) {
        if(response!=null) {
            JSONObject jsonobject = JSON.parseObject(response);
            JSONArray jsonArray = jsonobject.getJSONArray("detail");
            int size=jsonArray.size();
            for (int i = 0; i <size ; i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String boxId=jsonObject.getString("boxId");
                String tel=jsonObject.getString("tel");
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, ItemType.BOXLIST)
                        .setField(MultipleFields.TEL,tel)
                        .setField(MultipleFields.BOXID,boxId)
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
