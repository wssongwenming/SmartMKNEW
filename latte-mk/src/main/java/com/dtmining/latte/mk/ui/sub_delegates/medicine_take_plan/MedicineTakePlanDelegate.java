package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
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
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model.MedicinePlanInfo;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineTakePlanDelegate extends LatteDelegate{

    private List<MedicinePlanInfo> list =new ArrayList<>();
    private Context context;
    private MyElvAdapter myAdapter;
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
                                          initData(response);
                                          myAdapter.notifyDataSetChanged();
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

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_plan")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        com.alibaba.fastjson.JSONObject object= JSON.parseObject(response);
                        int code=object.getIntValue("code");
                        if(code==1) {
                            initData(response);
                            myAdapter.notifyDataSetChanged();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }
    public void initView() {
        myAdapter = new MyElvAdapter(context, mExpandableListView,list);
        mExpandableListView.setAdapter(myAdapter);
        mExpandableListView.setGroupIndicator(null);
        myAdapter.setOnClickDeleteListener(new OnClickDeleteListener() {
            @Override
            public void onItemClick(View view, final int groupPosition, final int childPosition) {
                String planId=(list.get(groupPosition).getDatas()).get(childPosition).getId();
                JsonObject detail=new JsonObject();
                detail.addProperty("planId",planId);
                JsonObject jsonForDelete=new JsonObject();
                jsonForDelete.add("detail",detail);

                RestClient.builder()
                        .url(UploadConfig.API_HOST+"/api/delete_plan")
                        .clearParams()
                        .raw(jsonForDelete.toString())//应该传参数medicineId，这里由于medicineId为空,所以暂用medicinename代替
                        .success(new ISuccess() {
                            @Override
                            public void onSuccess(String response) {

                                list.get(groupPosition).getDatas().remove(childPosition);
                                if(list.get(groupPosition).getDatas().size()==0){
                                    list.remove(groupPosition);
                                }
                                myAdapter.notifyDataSetChanged();
                                final IGlobalCallback<String> callback_medicine_plan_for_index = CallbackManager
                                        .getInstance()
                                        .getCallback(CallbackType. ON_GET_MEDICINE_PLAN_INDEX);
                                if (callback_medicine_plan_for_index != null) {
                                    callback_medicine_plan_for_index.executeCallback("");
                                }
                            }
                        })
                        .build()
                        .post();


            }
        });

        myAdapter.setOnClickChangeListener(new OnClickChangeListener() {
            @Override
            public void onItemClick(View view, final int groupPosition, final int childPosition) {
                String planId=(list.get(groupPosition).getDatas()).get(childPosition).getId();
                int medicneUsecount=(list.get(groupPosition).getDatas()).get(childPosition).getMedicineUseCount();
                String atime=(list.get(groupPosition).getDatas()).get(childPosition).getAtime();
                String medicinename=(list.get(groupPosition).getDatas()).get(childPosition).getMedicineName();
                 JsonObject detail=new JsonObject();
                detail.addProperty("planId",planId);
                detail.addProperty("medicineUseCount",medicneUsecount);
                detail.addProperty("atime",atime);
                detail.addProperty("medicineName",medicinename);
                detail.addProperty("boxId",boxId);
                JsonObject jsonForChange=new JsonObject();
                jsonForChange.add("detail",detail);
                UpdatePlanDelegate delegate=UpdatePlanDelegate.newInstance(jsonForChange.toString());
                start(delegate);


            }
        });
    }
    private void initData(String responseJsonString) {
        list.clear();
        final JSONObject jsonObject = JSON.parseObject(responseJsonString);
        final JSONObject data = jsonObject.getJSONObject("detail");
                final JSONArray dataArray = data.getJSONArray("planlist");
                int size = dataArray.size();
                for (int i = 0; i < size; i++) {
                    MedicinePlanInfo medicinePlanInfo=new MedicinePlanInfo();
                    JSONObject jsondata = (JSONObject) dataArray.get(i);
                    String medicineUseTime=jsondata.getString("time");
                    medicinePlanInfo.setTimeString(medicineUseTime);
                    JSONArray jsonArray = jsondata.getJSONArray("plans");
                    int lenght = jsonArray.size();
                    List<MedicinePlan> childrenList = new ArrayList<>();
                    for (int j = 0; j < lenght; j++) {
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(j);
                        MedicinePlan medicinePlan = new MedicinePlan();
                        medicinePlan.setAtime(jsonObject1.getString("atime"));
                        medicinePlan.setEndRemind(jsonObject1.getString("endRemind"));
                        medicinePlan.setId(jsonObject1.getString("id"));
                        medicinePlan.setMedicineUseCount(jsonObject1.getInteger("medicineUseCount"));
                        //medicinePlanModel.setDayInterval(jsonObject1.getInteger("dayInterval"));
                        medicinePlan.setStartRemind(jsonObject1.getString("startRemind"));
                        medicinePlan.setMedicineName(jsonObject1.getString("medicineName"));
                        medicinePlan.setBoxId(jsonObject1.getString("boxId"));
                        childrenList.add(medicinePlan);
                    }
                    medicinePlanInfo.setDatas(childrenList);
                    list.add(medicinePlanInfo);

                }

    }


}




