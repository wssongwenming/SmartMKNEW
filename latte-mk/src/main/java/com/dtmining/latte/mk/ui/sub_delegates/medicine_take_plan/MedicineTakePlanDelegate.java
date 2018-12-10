package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.alarmclock.Alarm;
import com.dtmining.latte.alarmclock.MyDBOpenHelper;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.index.IndexDelegate;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineTakePlanDelegate extends LatteDelegate{
    MedicinePlanExpandableListViewAdapter medicinePlanExpandableListViewAdapter;
    private Map<String, List<MedicinePlan>> dataset = new HashMap<>();
    private ArrayList<String> parentList=new ArrayList<>();
    String boxId=null;
    String tel=null;
    private MyDBOpenHelper myDBOpenHelper;
    public static final String ALARM_ALERT_ACTION = "com.kidcare.alarm_alert";
    @BindView(R2.id.btn_medicine_take_plan_add)
    AppCompatButton mBtnAdd=null;
    @BindView(R2.id.elv_medicine_take_plan_expandableListView)
    ExpandableListView mExpandableListView=null;
    @OnClick(R2.id.btn_medicine_take_plan_add)
    void onClickAddPlan(){
        start(new MedicinePlanAddChoiceDelegate());
    }
    @OnClick(R2.id.btn_medicine_take_plan_set_alarm)
    void onClickSetAlarm(){
        setalarm();
    }
    private Set<SwipeListLayout> sets = new HashSet();
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId= LattePreference.getBoxId();
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
            if(boxId==null){
                Toast.makeText(getContext(),"App未绑定当前药箱",Toast.LENGTH_LONG).show();
            }
        }
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_GET_MEDICINE_PLAN, new IGlobalCallback() {
                    @Override
                    public void executeCallback(@Nullable Object args) {
                        RestClient.builder()
                                .url(UploadConfig.API_HOST+"/api/get_plan")
                                //.url("medicine_plan")
                                .params("tel",tel)
                                .params("boxId",boxId)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        com.alibaba.fastjson.JSONObject object= JSON.parseObject(response);
                                        int code=object.getIntValue("code");
                                        if(code==1) {
                                            medicinePlanExpandableListViewAdapter.notifyDataSetChanged();
                                            convert_response_to_plan(response);
                                            medicinePlanExpandableListViewAdapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .build()
                                .get();
                    }
                });

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //myDBOpenHelper.close();// 释放数据库资源
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDBOpenHelper=MyDBOpenHelper.getInstance((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY));
    }

/*    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_plan")
                //.url("medicine_plan")
                .params("tel",tel)
                .params("boxId",9999)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        com.alibaba.fastjson.JSONObject object= JSON.parseObject(response);
                        int code=object.getIntValue("code");
                        if(code==1) {
                            mExpandableListView.setAdapter((ExpandableListAdapter) null);
                            //MedicinePlanExpandableListViewAdapter medicinePlanExpandableListViewAdapter = new MedicinePlanExpandableListViewAdapter(response, sets, MedicineTakePlanDelegate.this);
                            //medicinePlanExpandableListViewAdapter.convert(response);
                            //mExpandableListView.setAdapter(medicinePlanExpandableListViewAdapter);

                        }
                    }
                })
                .build()
                .get();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        //mRefreshHandler.firstPage_medicine_history(UploadConfig.API_HOST+"/api/get_history",tel,1,5);
        //mRefreshHandler.get_medicine_plan(UploadConfig.API_HOST+"/api/get_plan",tel,boxId);
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_plan")
                //.url("medicine_plan")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        com.alibaba.fastjson.JSONObject object= JSON.parseObject(response);
                        int code=object.getIntValue("code");
                        if(code==1) {
                            convert(response);
                            medicinePlanExpandableListViewAdapter.notifyDataSetChanged();
                            //MedicinePlanExpandableListViewAdapter medicinePlanExpandableListViewAdapter = new MedicinePlanExpandableListViewAdapter(response, sets, IndexDelegate.this);
                            //medicinePlanExpandableListViewAdapter.convert(response);
                            //mExpandableListView.setAdapter(medicinePlanExpandableListViewAdapter);


                        }
                    }
                })
                .build()
                .get();
    }

    private void setalarm(){
        List<Alarm> alarms = myDBOpenHelper.query();

        int[] alarmIds=new int[alarms.size()];
        int size=alarms.size();
        for (int i = 0; i <size ; i++) {
            alarmIds[i]=alarms.get(i).getId();
            Log.d("ids"+i,alarmIds[i]+"");
        }
        Log.d("ids", alarmIds+"");
        int length=alarmIds.length;
        for (int i = 0; i <length ; i++) {
            int alarmid=alarmIds[i];
            AlarmOpreation.enableAlert(getContext(),alarmid,alarmIds);
        }

       }




    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_plan")
                //.url("medicine_plan")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        com.alibaba.fastjson.JSONObject object= JSON.parseObject(response);
                        int code=object.getIntValue("code");
                        if(code==1) {
                            convert(response);
                            medicinePlanExpandableListViewAdapter.notifyDataSetChanged();
                            //MedicinePlanExpandableListViewAdapter medicinePlanExpandableListViewAdapter = new MedicinePlanExpandableListViewAdapter(response, sets, MedicineTakePlanDelegate.this);
                            //medicinePlanExpandableListViewAdapter.convert(response);

                            //mExpandableListView.setAdapter(medicinePlanExpandableListViewAdapter);
                        }
                    }
                })
                .build()
                .get();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        medicinePlanExpandableListViewAdapter = new MedicinePlanExpandableListViewAdapter( dataset,parentList,sets, MedicineTakePlanDelegate.this);
        mExpandableListView.setAdapter(medicinePlanExpandableListViewAdapter);
    }
    private void convert(String jsonString){

        final JSONObject jsonObject= JSON.parseObject(jsonString);
        //String tel=jsonObject.getString("tel");
        final com.alibaba.fastjson.JSONObject data= jsonObject.getJSONObject("detail");
        final JSONArray dataArray=data.getJSONArray("planlist");
        int size=dataArray.size();
        Log.d("size", size+"");
        for (int i = 0; i <size ; i++) {

            JSONObject jsondata= (JSONObject) dataArray.get(i);
            parentList.add(jsondata.getString("time"));
            JSONArray jsonArray=jsondata.getJSONArray("plans");
            int lenght=jsonArray.size();
            List<MedicinePlan> childrenList = new ArrayList<>();
            for (int j = 0; j <lenght ; j++) {
                JSONObject jsonObject1= (JSONObject) jsonArray.get(j);
                MedicinePlan medicinePlanModel=new MedicinePlan();
                medicinePlanModel.setAtime(jsonObject1.getString("atime"));
                medicinePlanModel.setEndRemind(jsonObject1.getString("endRemind"));
                medicinePlanModel.setId(jsonObject1.getString("id"));
                medicinePlanModel.setMedicineUseCount(jsonObject1.getInteger("medicineUseCount"));
                medicinePlanModel.setDayInterval(jsonObject1.getInteger("dayInterval"));
                medicinePlanModel.setStartRemind(jsonObject1.getString("startRemind"));
                medicinePlanModel.setMedicineName(jsonObject1.getString("medicineName"));
                medicinePlanModel.setBoxId(jsonObject1.getString("boxId"));

                childrenList.add(medicinePlanModel);
            }
            dataset.put(parentList.get(i),childrenList);
        }
        Log.d("datset", dataset.toString());
    }
    private void convert_response_to_plan(String jsonString){
        if(jsonString!=null) {
            dataset.clear();
            parentList.clear();
            final JSONObject jsonObject = JSON.parseObject(jsonString);
            //String tel=jsonObject.getString("tel");
            final com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("detail");
            final JSONArray dataArray = data.getJSONArray("planlist");
            int size = dataArray.size();
            for (int i = 0; i < size; i++) {

                JSONObject jsondata = (JSONObject) dataArray.get(i);
                parentList.add(jsondata.getString("time"));
                JSONArray jsonArray = jsondata.getJSONArray("plans");
                int lenght = jsonArray.size();
                List<MedicinePlan> childrenList = new ArrayList<>();
                for (int j = 0; j < lenght; j++) {
                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(j);
                    MedicinePlan medicinePlanModel = new MedicinePlan();
                    medicinePlanModel.setAtime(jsonObject1.getString("atime"));
                    medicinePlanModel.setEndRemind(jsonObject1.getString("endRemind"));
                    medicinePlanModel.setId(jsonObject1.getString("id"));
                    medicinePlanModel.setMedicineUseCount(jsonObject1.getInteger("medicineUseCount"));
                    //medicinePlanModel.setDayInterval(jsonObject1.getInteger("dayInterval"));
                    medicinePlanModel.setStartRemind(jsonObject1.getString("startRemind"));
                    medicinePlanModel.setMedicineName(jsonObject1.getString("medicineName"));
                    medicinePlanModel.setBoxId(jsonObject1.getString("boxId"));
                    childrenList.add(medicinePlanModel);
                }
                dataset.put(parentList.get(i), childrenList);
            }
        }
    }
}
