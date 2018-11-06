package com.dtmining.latte.mk.ui.sub_delegates.hand_add;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.main.aboutme.list.ListBean;
import com.dtmining.latte.mk.main.aboutme.list.ListItemType;
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
public class BoxListDataConverter  {
    protected final LinkedList<String> BOXES=new LinkedList<>();
    private String mJsonData=null;
    public LinkedList<String> getEntities() {
        convert();
        return BOXES;
    }


    public LinkedList<String> convert(){

        JSONObject jsonobject= JSON.parseObject(getJsonData());
        BOXES.add("请选择药箱Id");
        //JSONObject jsonobject1=jsonobject.getJSONObject("detail");
        if(jsonobject!=null) {
            JSONArray jsonarray = jsonobject.getJSONArray("detail");

            int size = jsonarray.size();
            for (int i = 0; i < size; i++) {
                JSONObject jsonobject3 = jsonarray.getJSONObject(i);
                String boxid = jsonobject3.getString("boxid");

                BOXES.add(boxid);
            }
        }
        Log.d("boxid", BOXES.toString());
        return BOXES;
    }
    public BoxListDataConverter setJsonData(String json){
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
