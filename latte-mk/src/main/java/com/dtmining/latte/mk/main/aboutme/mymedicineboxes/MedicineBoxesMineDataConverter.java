package com.dtmining.latte.mk.main.aboutme.mymedicineboxes;

import android.content.Context;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;
import java.util.List;

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
            JSONObject object = JSON.parseObject(getJsonData());
            int code = object.getIntValue("code");
            switch (code) {
                case 1:
                    final JSONObject jsonObject = JSON.parseObject(getJsonData());
                    String tel = jsonObject.getString("tel");
                    final JSONArray dataArray = jsonObject.getJSONArray("detail");
                    final int size = dataArray.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject data = (JSONObject) dataArray.get(i);
                        String onUse = data.getString("onuse");
                        String pause = data.getString("pause");
                        String boxId = data.getString("boxid");
                        String overDue = data.getString("overdue");
                        int type = ItemType.MEDICINE_BOX;
                        final MultipleItemEntity entity = MultipleItemEntity.builder()
                                .setField(MultipleFields.ITEM_TYPE, type)
                                .setField(MultipleFields.ONUSE, onUse)
                                .setField(MultipleFields.PAUSE, pause)
                                .setField(MultipleFields.TEL, tel)
                                .setField(MultipleFields.BOXID, boxId)
                                .setField(MultipleFields.OVERDUE, overDue)
                                .build();
                        ENTITIES.add(entity);
                    }
                    break;
                case 17:
                    Toast.makeText((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY),"当前用户没有药品信息",Toast.LENGTH_LONG).show();
                    break;

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
