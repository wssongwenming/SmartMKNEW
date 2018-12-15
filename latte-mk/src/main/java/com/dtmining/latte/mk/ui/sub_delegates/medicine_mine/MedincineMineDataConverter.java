package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.util.Log;

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
 * Date:2018/10/24
 * Description:
 */
public class MedincineMineDataConverter extends DataConverter {
    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        return null;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        if(getJsonData()!=null) {
            System.out.print("json="+getJsonData());
            final JSONObject jsonObject = JSON.parseObject(getJsonData());
            String tel = jsonObject.getString("tel");
            int code=jsonObject.getIntValue("code");
            System.out.print("code="+code);
            final JSONArray dataArray = jsonObject.getJSONArray("detail");

            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                JSONObject data = (JSONObject) dataArray.get(i);
                final String endRemind=data.getString("endRemind");
                final String medicineCode=data.getString("medicineCode");
                final String medicineValidity=data.getString("medicineValidity");
                final String medicineId=data.getString("medicineId");
                final int medicineCount = data.getInteger("medicineCount");
                final int medicineUseCount=data.getIntValue("medicineUsecount");
                final int timesonday=data.getIntValue("timesonday");
                final int dayInterval=data.getIntValue("dayInterval");
                final String startRemind=data.getString("startRemind");
                final String medicineName = data.getString("medicineName");
                final String medicine_img_url = data.getString("medicineUrl");
                final String boxId = data.getString("boxId");
                final int medicinePause = data.getInteger("medicinePause");
                int type = ItemType.MEDICINE_MINE;
                final MultipleItemEntity entity = MultipleItemEntity.builder()
                        .setField(MultipleFields.ITEM_TYPE, type)
                        .setField(MultipleFields.TEL, tel)
                        .setField(MultipleFields.MEDICINEENDREMIND,endRemind)
                        .setField(MultipleFields.MEDICINECODE,medicineCode)
                        .setField(MultipleFields.MEDICINEVALIDITY,medicineValidity)
                        .setField(MultipleFields.MEDICINEID, medicineId)
                        .setField(MultipleFields.MEDICINECOUNT, medicineCount)
                        .setField(MultipleFields.MEDICINETIMESONDAY,timesonday)
                        .setField(MultipleFields.MEDICINEINTERVAL,dayInterval)
                        .setField(MultipleFields.MEDICINESTARTREMIND,startRemind)
                        .setField(MultipleFields.MEDICINENAME, medicineName)
                        .setField(MultipleFields.MEDICINEIMGURL, medicine_img_url)
                        .setField(MultipleFields.MEDICINEUSECOUNT,medicineUseCount)
                        .setField(MultipleFields.BOXID, boxId)
                        .setField(MultipleFields.MEDICINEPAUSE, medicinePause)
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
