package com.dtmining.latte.mk.ui.recycler;


import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/9/21
 * Description:
 */
public abstract class DataConverter {
    protected final ArrayList<MultipleItemEntity> ENTITIES=new ArrayList<>();
    private String mJsonData=null;
    public abstract ArrayList<MultipleItemEntity> getEntities();
    public abstract ArrayList<MultipleItemEntity> convert();
    public abstract ArrayList<MultipleItemEntity> convertMedicineHistory();
    public abstract ArrayList<MultipleItemEntity> convertMedicinePlan();
    public DataConverter setJsonData(String json){
        this.mJsonData=json;
        return this;
    }
    protected String getJsonData(){
        if(mJsonData==null||mJsonData.isEmpty())
        {
            throw new NullPointerException("DATA IS NULL");
        }
        return mJsonData;
    }

    public abstract List<MultipleItemEntity> getTop();
    public void clearData(){
        ENTITIES.clear();
    }
}
