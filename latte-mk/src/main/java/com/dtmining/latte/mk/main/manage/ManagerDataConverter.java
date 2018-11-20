package com.dtmining.latte.mk.main.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.tools.Icon;

import java.util.ArrayList;

/**
 * author:songwenming
 * Date:2018/11/19
 * Description:
 */
public class ManagerDataConverter {
    public ManagerDataConverter(String jsonString) {
        this.jsonString = jsonString;
    }

    String jsonString =null;

    public ArrayList<Icon> convert() {
        ArrayList<Icon> medicineIconList = new ArrayList<>();
        if (jsonString != null) {
            final JSONObject jsonObject = JSON.parseObject(jsonString);
            JSONObject data = jsonObject.getJSONObject("detail");
            JSONArray dataArray = data.getJSONArray("medicines");
            int size = dataArray.size();

            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = (JSONObject) dataArray.get(i);
                Icon icon = new Icon();
                String medicineName = jsonObject1.getString("medicineNames");
                String boxId = jsonObject1.getString("boxId");
                String imgUrl = jsonObject1.getString("medicineImage");
                icon.setName(medicineName);
                icon.setUrl(imgUrl);
                icon.setBoxId(boxId);
                medicineIconList.add(icon);


            }

        }
        return medicineIconList;
    }
}
