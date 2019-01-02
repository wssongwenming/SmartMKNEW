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
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.alarm.AlarmOpreation;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class MedicineTakePlanDelegate1 extends LatteDelegate{

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
                                .params("boxId",LattePreference.getBoxId())
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        JSONObject object= JSON.parseObject(response);
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
                        JSONObject object= JSON.parseObject(response);
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
                        medicinePlan.setMedicineType(jsonObject1.getIntValue("medicineType"));
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
    public void synPlanWithSqlite(){
        RestClient.builder()
                .url(UploadConfig.API_HOST + "/api/get_plan")//获取所有现有计划，成功后取得时间信息，设置闹钟
                .params("tel", tel)
                .params("boxId", LattePreference.getBoxId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            JSONObject object = JSON.parseObject(response);
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
                                    //map_interval.put("0", null);
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
                                        int medicineType=plan.getIntValue("medicineType");
                                        String unitfordose=null;
                                        switch (medicineType) {
                                            case 0:
                                                unitfordose = "片";
                                                break;
                                            case 1:
                                                unitfordose = "粒/颗";
                                                break;
                                            case 2:
                                                unitfordose = "瓶/支";
                                                break;
                                            case 3:
                                                unitfordose = "包/袋";
                                                break;
                                            case 4:
                                                unitfordose = "克";
                                                break;
                                            case 5:
                                                unitfordose = "毫升";
                                                break;
                                            case 6:
                                                unitfordose = "其他";
                                                break;
                                        }
                                        String medicineUseCount = String.valueOf(plan.getInteger("medicineUseCount"));
                                        String medicineName = plan.getString("medicineName");
                                        //先以interval判断，如果
                                        if (map_interval.get(interval) == null) {
                                            HashMap<String, ArrayList<String>> map_start_time = new HashMap<>();
                                            ArrayList<String> infoArray = new ArrayList<>();
                                            infoArray.add(medicineName + ":服用" + medicineUseCount+unitfordose);
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
                                                ((ArrayList<String>) map_interval.get(interval).get(starttime)).add(medicineName + ":服用" + medicineUseCount+unitfordose);
                                            } else {
                                                ArrayList<String> infoArray = new ArrayList<>();
                                                infoArray.add(medicineName + ":服用" + medicineUseCount+unitfordose);
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

                    }
                })
                .build()
                .get();
    }
    public void add(Alarm alarm) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        d = format.parse("2012-12-12");
        Date date = new Date(d.getTime());
        myDBOpenHelper.add(alarm);
    }
    private Date getDate(String time, int interval) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        if (time != null && !time.equals("")) {
            java.util.Date date1 = null;
            try {
                date1 = sdf.parse(time);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date1);
                cal.add(Calendar.DATE, -interval);
                date2 =new Date ((sdf.parse(sdf.format(cal.getTime()))).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date2;
    }
}




