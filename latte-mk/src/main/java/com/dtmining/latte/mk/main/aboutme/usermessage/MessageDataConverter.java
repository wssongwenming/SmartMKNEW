package com.dtmining.latte.mk.main.aboutme.usermessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/12/7
 * Description:
 */
public class MessageDataConverter extends DataConverter {

    String tel=null;
    @Override
    public ArrayList<MultipleItemEntity> convert() {
        return null;
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
    @Override
    public ArrayList<MultipleItemEntity> getEntities() {
        get_medicine_use_message(tel);
        get_medicine_supply_message(tel);
        get_medicine_overdue_message(tel);
        return ENTITIES;
    }
    private void get_medicine_use_message(String tel) {
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine_use_message")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if(response!=null){
                            JSONObject object=JSON.parseObject(response);
                            int code= object.getIntValue("code");
                            if(code==1){
                                JSONObject detail= object.getJSONObject("detail");
                                JSONArray  planlist=detail.getJSONArray("planlist");
                                StringBuilder plansbuilder=new StringBuilder();
                                plansbuilder.append("请于");
                                int size=planlist.size();
                                for (int i = 0; i <size ; i++) {
                                    JSONObject planlist_i= (JSONObject) planlist.get(i);
                                    String useTime=planlist_i.getString("time");
                                    plansbuilder.append(useTime+"服用");
                                    JSONArray plans=planlist_i.getJSONArray("plans");
                                    int size1=plans.size();
                                    for (int j = 0; j <size1 ; j++) {
                                        JSONObject plan= (JSONObject) plans.get(j);
                                        String medicineName=plan.getString("medicineName");
                                    }
                                }
                            }
                        }

                    }
                })
                .build()
                .get();
    }

    private void get_medicine_supply_message(String tel){
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine_supply_message")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                    }
                })
                .build()
                .get();

    }
    private void get_medicine_overdue_message(String tel){
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine_overdue")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                    }
                })
                .build()
                .get();

    }
}
