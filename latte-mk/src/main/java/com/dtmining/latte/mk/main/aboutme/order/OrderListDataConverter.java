package com.dtmining.latte.mk.main.aboutme.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;




import java.util.ArrayList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/10/21
 * Description:
 */
public class OrderListDataConverter extends DataConverter {

    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        return null;
    }

    @Override
    public ArrayList<MultipleItemEntity> convert() {
        final JSONArray array = JSON.parseObject(getJsonData()).getJSONArray("data");
        int size =array.size();
        for (int i = 0; i <size ; i++) {
            final  JSONObject data=array.getJSONObject(i);
            final String thumb=data.getString("Thumb");
            final String title=data.getString("title");
            final int id=data.getInteger("id");
            final Double price=data.getDouble("price");
            final MultipleItemEntity entity=MultipleItemEntity.builder()
                    .setItemType(OrderListItemType.ITEM_ORDER_LIST)
                    .setField(MultipleFields.ID,id)
                    .setField(MultipleFields.IMAGE_URL,thumb)
                    .setField(MultipleFields.TITLE,title)
                    .setField(OrderItemFields.PRICE,price)
                    .setField(OrderItemFields.TIME,title)
                    .build();
            ENTITIES.add(entity);
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
