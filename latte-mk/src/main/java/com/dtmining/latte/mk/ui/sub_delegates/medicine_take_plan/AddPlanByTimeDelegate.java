package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.alarmclock.Alarm;
import com.dtmining.latte.alarmclock.DBManager;
import com.dtmining.latte.alarmclock.MyDBOpenHelper;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.adapter.SimpleHorizontalAdapter;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model.TimeCountPair;
import com.dtmining.latte.mk.ui.sub_delegates.views.CheckMedicinesDialog;
import com.dtmining.latte.mk.ui.sub_delegates.views.CustomDatePicker;
import com.dtmining.latte.mk.ui.sub_delegates.views.HorizontalListview;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/2
 * Description:
 */
public class AddPlanByTimeDelegate extends LatteDelegate implements View.OnClickListener, CheckMedicinesDialog.ClickListenerInterface{
    private MyDBOpenHelper myDBOpenHelper;
    private CheckMedicinesDialog checkMedicinesDialog;
    private String boxId=null;
    private String tel=null;
    private CustomDatePicker customDatePicker;
    private List<MedicineState> medicineSet=new ArrayList<>();
    private List<String> useCountSet=new ArrayList<String>();
    @BindView(R2.id.hl_plan_add_by_time_horizontallistview)
    HorizontalListview mHorizontalListView=null;
    @OnClick(R2.id.tv_plan_add_by_time_set_time)
    void setTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker.show(now);
    }
    @BindView(R2.id.tv_plan_add_by_time_set_time)
    TextView mMedicineUseTime=null;
    @BindView(R2.id.tv_plan_set_by_time_set_medicines)
    TextView mSetMedicine=null;
    @OnClick(R2.id.tv_plan_set_by_time_set_medicines)
    void setMedicine(){
        checkMedicinesDialog = new CheckMedicinesDialog(getContext(),AddPlanByTimeDelegate.this);
        checkMedicinesDialog.show();
    }
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_time_confirm)
    void confirmPlan(){
        if(checkForm()){
            int size=medicineSet.size();
            Log.d("size1", size+"");
            String medicineUseTime=mMedicineUseTime.getText().toString();
            JsonObject medicinePlanSubModelbyTime=new JsonObject();
            JsonObject detail=new JsonObject();
            JsonObject medicinePlan=new JsonObject();
            JsonArray pairs=new JsonArray();
            for (int i = 0; i <size ; i++) {
                JsonObject pair=new JsonObject();
                pair.addProperty("medicine_id",medicineSet.get(i).getMedicineId());
                pair.addProperty("medicine_usecount",useCountSet.get(i));
                pairs.add(pair);
            }

            medicinePlan.add("pairs",pairs);

            detail.addProperty("medicine_time",medicineUseTime);
            detail.addProperty("boxid",boxId);
            detail.add("medicine_plan",medicinePlan);

            medicinePlanSubModelbyTime.add("detail",detail);
            Log.d("addbytime", medicinePlanSubModelbyTime.toString());
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/Medicine_update_plan_by_time")
                    .clearParams()
                    .raw(medicinePlanSubModelbyTime.toString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            com.alibaba.fastjson.JSONObject object = JSON.parseObject(response);
                            int code = object.getIntValue("code");
                            if (code == 1)
                            {
                                //刷新回调
                                final IGlobalCallback<String> callback_medicine_plan = CallbackManager
                                        .getInstance()
                                        .getCallback(CallbackType.ON_GET_MEDICINE_PLAN);
                                if (callback_medicine_plan != null) {
                                    callback_medicine_plan.executeCallback("");
                                }

                                final IGlobalCallback<String> callback_medicine_plan_for_index = CallbackManager
                                        .getInstance()
                                        .getCallback(CallbackType. ON_GET_MEDICINE_PLAN_INDEX);
                                if (callback_medicine_plan_for_index != null) {
                                    callback_medicine_plan_for_index.executeCallback("");
                                }
/*                                RestClient.builder()
                                        .url(UploadConfig.API_HOST + "/api/get_plan")//获取所有现有计划，成功后取得时间信息，设置闹钟
                                        .params("tel", tel)
                                        .params("boxId", boxId)
                                        .success(new ISuccess() {
                                            @Override
                                            public void onSuccess(String response) {
                                                if (response != null) {
                                                    com.alibaba.fastjson.JSONObject object = JSON.parseObject(response);
                                                    int code = object.getIntValue("code");
                                                    if (code == 1) {
                                                        JSONObject jsonData = JSON.parseObject(response);
                                                        JSONObject detail = jsonData.getJSONObject("detail");
                                                        JSONArray planLsit = detail.getJSONArray("planlist");
                                                        HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> map_time = new HashMap<>();
                                                        int size = planLsit.size();
                                                        for (int i = 0; i < size; i++) {
                                                            JSONObject planData = (JSONObject) planLsit.get(i);
                                                            String time = planData.getString("time");
                                                            JSONArray planArray = planData.getJSONArray("plans");
                                                            HashMap<String, HashMap<String, ArrayList<String>>> map_interval = new HashMap<>();
                                                            map_interval.put("0", null);
                                                            map_interval.put("1", null);
                                                            map_interval.put("2", null);
                                                            map_interval.put("3", null);
                                                            map_interval.put("4", null);
                                                            map_interval.put("5", null);
                                                            map_interval.put("6", null);
                                                            map_interval.put("7", null);
                                                            int size2 = planArray.size();
                                                            for (int j = 0; j < size2; j++) {
                                                                JSONObject plan = (JSONObject) planArray.get(j);
                                                                String interval = plan.getString("dayInterval");
                                                                String starttime = plan.getString("startRemind");
                                                                String endtime = plan.getString("endRemind");
                                                                String medicineUseCount = String.valueOf(plan.getInteger("medicineUseCount"));
                                                                String medicineName = plan.getString("medicineName");
                                                                //先以interval判断，如果
                                                                if (map_interval.get(interval) == null) {
                                                                    HashMap<String, ArrayList<String>> map_start_time = new HashMap<>();
                                                                    ArrayList<String> infoArray = new ArrayList<>();
                                                                    infoArray.add(medicineName + ":服用" + medicineUseCount);
                                                                    map_start_time.put(starttime, infoArray);
                                                                    map_interval.put(interval, map_start_time);
                                                                } else {
                                                                    boolean has = false;
                                                                    for (Map.Entry<String, HashMap<String, ArrayList<String>>> item_interval : map_interval.entrySet()) {
                                                                        if (map_interval != null) {
                                                                            HashMap<String, ArrayList<String>> hashMap = item_interval.getValue();
                                                                            if (hashMap != null) {
                                                                                for (Map.Entry<String, ArrayList<String>> item_start_time : hashMap.entrySet()) {
                                                                                    String key_startTime = item_start_time.getKey();
                                                                                    if (key_startTime.equalsIgnoreCase(starttime)) {
                                                                                        has = true;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    if (has) {
                                                                        ((ArrayList<String>) map_interval.get(interval).get(starttime)).add(medicineName + ":服用" + medicineUseCount);
                                                                    } else {
                                                                        ArrayList<String> infoArray = new ArrayList<>();
                                                                        infoArray.add(medicineName + ":服用" + medicineUseCount);
                                                                        map_interval.get(interval).put(starttime, infoArray);
                                                                    }
                                                                }
                                                            }
                                                            map_time.put(time, map_interval);
                                                            Log.d("map_time", map_time.toString());
                                                        }
                                                        if (map_time != null) {
                                                            //清空计划表
                                                            myDBOpenHelper.deleteAlarm();
                                                            for (Map.Entry<String, HashMap<String, HashMap<String, ArrayList<String>>>> item_time : map_time.entrySet()) {
                                                                String time = null;
                                                                if (item_time != null) {
                                                                    time = item_time.getKey();
                                                                    HashMap<String, HashMap<String, ArrayList<String>>> map_interval = item_time.getValue();
                                                                    for (Map.Entry<String, HashMap<String, ArrayList<String>>> item_interval : map_interval.entrySet()) {
                                                                        String Interval = null;
                                                                        if (item_interval != null) {
                                                                            Interval = item_interval.getKey();
                                                                            HashMap<String, ArrayList<String>> map_starttime = item_interval.getValue();
                                                                            if (map_starttime != null) {
                                                                                for (Map.Entry<String, ArrayList<String>> item_starttime : map_starttime.entrySet()) {
                                                                                    String starttime = null;
                                                                                    String message = null;
                                                                                    if (item_starttime != null) {
                                                                                        starttime = item_starttime.getKey();
                                                                                        message = item_starttime.getValue().toString();
                                                                                        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                                                                                        int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.length()));
                                                                                        int interval = Integer.parseInt(Interval);
                                                                                        Alarm alarm = new Alarm(getDate(starttime, interval), hour, minute, interval, message, "aaa.mp3", 1);
                                                                                        try {
                                                                                            add(alarm);
                                                                                        } catch (ParseException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                pop();
                                            }
                                        })
                                        .build()
                                        .get();*/
                                pop();
                            }

                        }
                    })
                    .build()
                    .post();

        }
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan_add_by_time;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId= LattePreference.getBoxId();
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDatePicker();
        myDBOpenHelper = MyDBOpenHelper.getInstance((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY));

    }

    private boolean checkForm(){
        final String medicineUseTime=mMedicineUseTime.getText().toString();

        boolean isPass=true;
        if(medicineUseTime.isEmpty()){
            mMedicineUseTime.setError("请设置服药时间");
            isPass=false;
        }else{
            mMedicineUseTime.setError(null);
        }
        if(medicineSet.size()==0||useCountSet.size()==0)
        {
            mSetMedicine.setError("请设置服用药品及剂量");
            isPass=false;
        }
        else
        {
            mSetMedicine.setError(null);
        }
        return isPass;
    }
    private void initDatePicker(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker = new CustomDatePicker(this.getContext(), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                Log.d("time", time);
                mMedicineUseTime.setText(time);
                //time_list.add(time);
                //Toast.makeText(getContext(),time,Toast.LENGTH_LONG).show();

            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(true); // 显示时和分
        customDatePicker.setIsLoop(true); // 允许循环滚动
    }
    @Override
    public void onClick(View v) {
    }
    @Override
    public void doConfirm(List<MedicineState> medicineStateList,List<String>useCountList) {
        medicineSet=medicineStateList;
        useCountSet=useCountList;
        ArrayList<String>medicineNames_useCount_doseUnit=new ArrayList<>();
        int size=medicineStateList.size();
        for (int i = 0; i <size ; i++) {
            medicineNames_useCount_doseUnit.add(medicineSet.get(i).getMedicineName()+":"+useCountSet.get(i)+medicineSet.get(i).getDoseUnit());
            //
        }
        set_medicine_tag(medicineNames_useCount_doseUnit);
        checkMedicinesDialog.dismiss();
    }
    // 设置时间提示标签　tips
    private void set_medicine_tag(ArrayList<String> the_medicine_tags){
        SimpleHorizontalAdapter horizontalAdapter = new SimpleHorizontalAdapter(the_medicine_tags, getContext());
        mHorizontalListView.setAdapter(horizontalAdapter);
        horizontalAdapter.notifyDataSetChanged();
    }
    private java.sql.Date getDate(String time, int interval) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date date2 = null;
        if (time != null && !time.equals("")) {
            java.util.Date date1 = null;
            try {
                date1 = sdf.parse(time);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date1);
                cal.add(Calendar.DATE, -interval);
                date2 =new java.sql.Date ((sdf.parse(sdf.format(cal.getTime()))).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date2;
    }
    public void add(Alarm alarm) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        d = format.parse("2012-12-12");
        java.sql.Date date = new java.sql.Date(d.getTime());
        myDBOpenHelper.add(alarm);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
