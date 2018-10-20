package com.dtmining.latte.mk.main.index;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.ui.recycler.DataConverter;
import com.dtmining.latte.ui.recycler.ItemType;
import com.dtmining.latte.ui.recycler.MultipleEntityBuilder;
import com.dtmining.latte.ui.recycler.MultipleFields;
import com.dtmining.latte.ui.recycler.MultipleItemEntity;

import java.util.ArrayList;

public class IndexDataConverter extends DataConverter {


    //获取Banner数据并封装为MutipleItemEntity
    private void getBanner(){
        ArrayList<Integer> bannerImages=new ArrayList<>();
        bannerImages.add(R.mipmap.banner_01);
        bannerImages.add(R.mipmap.banner_02);
        bannerImages.add(R.mipmap.banner_03);
            final MultipleItemEntity entity=MultipleItemEntity.builder()
                    .setFiled(MultipleFields.ITEM_TYPE,ItemType.BANNER)
                    .setFiled(MultipleFields.SPAN_SIZE,3)
                    .setFiled(MultipleFields.BANNERS_INTEGER,bannerImages)
                    .build();
        ENTITIES.add(entity);
    }

    //获取分割线加到ENTITES
    private void getSeperator(){
        final MultipleItemEntity entity=MultipleItemEntity.builder()
                .setFiled(MultipleFields.ITEM_TYPE,ItemType.SEPERATOR)
                .setFiled(MultipleFields.SPAN_SIZE,3)
                .build();
        ENTITIES.add(entity);
    }
    //获取“用药记录  更多> ”
    private void getMedicineHistoryMore(){
        final MultipleItemEntity entity= MultipleItemEntity.builder()
                .setFiled(MultipleFields.ITEM_TYPE,ItemType.TEXT_MORE_FOR_TAKE_MEDICINE_HISTORY)
                .setFiled(MultipleFields.SPAN_SIZE,3)
                .build();
        ENTITIES.add(entity);
    }
    //获取文字图标按钮
    private void getImage_TextButton(){
        final MultipleItemEntity entity=MultipleItemEntity.builder()
                .setFiled(MultipleFields.ITEM_TYPE,ItemType.IMAGE_TEXT_COMMAND_BUTTON)
                .setFiled(MultipleFields.ID,1)
                .setFiled(MultipleFields.SPAN_SIZE,1)
                .setFiled(MultipleFields.BUTTON_NAME,"扫码添加")
                .setFiled(MultipleFields.BUTTON_IMAGE,R.mipmap.icon_medicine_scan_add)
                .build();
        ENTITIES.add(entity);
        final MultipleItemEntity entity2=MultipleItemEntity.builder()
                .setFiled(MultipleFields.ITEM_TYPE,ItemType.IMAGE_TEXT_COMMAND_BUTTON)
                .setFiled(MultipleFields.SPAN_SIZE,1)
                .setFiled(MultipleFields.ID,2)
                .setFiled(MultipleFields.BUTTON_NAME,"手动添加")
                .setFiled(MultipleFields.BUTTON_IMAGE,R.mipmap.icon_medicine_hand_add)
                .build();
        ENTITIES.add(entity2);
        final MultipleItemEntity entity3=MultipleItemEntity.builder()
                .setFiled(MultipleFields.ITEM_TYPE,ItemType.IMAGE_TEXT_COMMAND_BUTTON)
                .setFiled(MultipleFields.SPAN_SIZE,1)
                .setFiled(MultipleFields.ID,3)
                .setFiled(MultipleFields.BUTTON_NAME,"我的药品")
                .setFiled(MultipleFields.BUTTON_IMAGE,R.mipmap.icon_mdicine_mine)
                .build();
        ENTITIES.add(entity3);
        final MultipleItemEntity entity4=MultipleItemEntity.builder()
                .setFiled(MultipleFields.ITEM_TYPE,ItemType.IMAGE_TEXT_COMMAND_BUTTON)
                .setFiled(MultipleFields.SPAN_SIZE,1)
                .setFiled(MultipleFields.BUTTON_NAME,"用药计划")
                .setFiled(MultipleFields.ID,4)
                .setFiled(MultipleFields.BUTTON_IMAGE,R.mipmap.icon_medicine_take_plan)
                .build();
        ENTITIES.add(entity4);
        final MultipleItemEntity entity5=MultipleItemEntity.builder()
                .setFiled(MultipleFields.ITEM_TYPE,ItemType.IMAGE_TEXT_COMMAND_BUTTON)
                .setFiled(MultipleFields.SPAN_SIZE,1)
                .setFiled(MultipleFields.ID,5)
                .setFiled(MultipleFields.BUTTON_NAME,"用药记录")
                .setFiled(MultipleFields.BUTTON_IMAGE,R.mipmap.icon_medicine_take_plan)
                .build();
        ENTITIES.add(entity5);
    }
    private void convert(){
        ArrayList<String> medicinenames=new ArrayList<>();
        String atime=null;
        JSONObject jsonobject=JSON.parseObject(getJsonData());
        JSONObject jsonobject1=jsonobject.getJSONObject("detail");
        JSONArray jsonarray=jsonobject1.getJSONArray("histories");
        int size=jsonarray.size();
        for(int i=0;i<size;i++){
            JSONObject jsonobject3=jsonarray.getJSONObject(i);
            String tel=jsonobject3.getString("tel");
            atime=jsonobject3.getString("atime");
            JSONArray jsonarray6=jsonobject3.getJSONArray("medicine_names");
            int size5=jsonarray6.size();


            for(int j=0;j<size5;j++)
            {
                medicinenames.add(jsonarray6.getString(j));
            }
            final MultipleItemEntity entity=MultipleItemEntity.builder()
                    .setFiled(MultipleFields.ITEM_TYPE,ItemType.TEXT_TEXT)
                    .setFiled(MultipleFields.SPAN_SIZE,3)
                    .setFiled(MultipleFields.MEDICINE_NAME,medicinenames.get(0))
                    .setFiled(MultipleFields.ATIME,atime)
                    .build();
            ENTITIES.add(entity);
        }


    }
   //获取网络数据
    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        getBanner();
        getSeperator();
        getImage_TextButton();
        getSeperator();
        getMedicineHistoryMore();
        convert();
/*        final JSONArray dataArray= JSON.parseObject(getJsonData()).getJSONArray("data");
        final int size= dataArray.size();
        for (int i = 0; i < size; i++) {
            final JSONObject data=dataArray.getJSONObject(i);
            final String imageUrl=data.getString("imageUrl");
            final String text=data.getString("text");
            final int spanSize=data.getInteger("spanSize");
            final int id=data.getInteger("goodsId");
            final JSONArray banners=data.getJSONArray("banners");
            final ArrayList<String> bannerImages=new ArrayList<>();
            int type=0;
            if(imageUrl==null&&text!=null){
                type= ItemType.TEXT;
            }else if(imageUrl!=null&&text==null){
                type=ItemType.IMAGE;
            }else if(imageUrl!=null){
                type=ItemType.TEXT_IMAGE;
            }else if(banners!=null)
            {
                type=ItemType.BANNER;
                //banner的初始化
                final int bannerSize=banners.size();
                for (int j = 0; j < bannerSize; j++){
                    final String banner=banners.getString(j);
                    bannerImages.add(banner);
                }
            }
            final MultipleItemEntity entity=MultipleItemEntity.builder()
                    .setFiled(MultipleFields.ITEM_TYPE,type)
                    .setFiled(MultipleFields.SPAN_SIZE,spanSize)
                    .setFiled(MultipleFields.ID,id)
                    .setFiled(MultipleFields.TEXT,text)
                    .setFiled(MultipleFields.IMAGE_URL,imageUrl)
                    .setFiled(MultipleFields.BANNERS,bannerImages)
                    .build();
            ENTITIES.add(entity);
        }*/
        return ENTITIES;
    }
/*  @Override

 public ArrayList<MultipleItemEntity> convert() {
     getBanner();
     getSeperator();
     getImage_TextButton();
     Log.d("here", ENTITIES.toString());
    final JSONObject DATA=JSON.parseObject(getJsonData()).getJSONObject("detail");

     final JSONArray dataArray= JSON.parseObject(getJsonData()).getJSONArray("histories");

     final int size= dataArray.size();

     for (int i = 0; i < size; i++) {
         final JSONObject data=dataArray.getJSONObject(i);
         final String tel=data.getString("tel");
         final String showtip=data.getString("showtip");
         //final int spanSize=data.getInteger("spanSize");

         int medicine_count=data.getInteger("medicine_count");
         String atime=data.getString("atime");
         String id=data.getString("id");
         final JSONArray medicine_names=data.getJSONArray("medicine_names");
         final ArrayList<String> medicinenames=new ArrayList<>();
         int type=ItemType.TEXT_TEXT;
             //banner的初始化
             final int bannerSize=medicine_names.size();
             for (int j = 0; j < bannerSize; j++){
                 final String name=medicine_names.getString(j);
                 medicinenames.add(name);
             }

         final MultipleItemEntity entity=MultipleItemEntity.builder()
                 .setFiled(MultipleFields.ITEM_TYPE,type)
                 .setFiled(MultipleFields.SPAN_SIZE,3)
                 .setFiled(MultipleFields.ID,id)
                 .setFiled(MultipleFields.MEDICINE_NAME,medicinenames.get(0))
                 .setFiled(MultipleFields.ATIME,atime)
                 .build();
         ENTITIES.add(entity);
     }
     return ENTITIES;
 }*/
}
