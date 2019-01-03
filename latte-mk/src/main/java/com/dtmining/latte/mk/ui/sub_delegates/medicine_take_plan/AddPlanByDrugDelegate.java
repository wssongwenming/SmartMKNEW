package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dtmining.latte.alarmclock.Alarm;
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
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model.Detail;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model.MedicinePlan;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model.MedicinePlanNetModel;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.Model.TimeCountPair;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.alarm.AlarmOpreation;
import com.dtmining.latte.mk.ui.sub_delegates.views.HorizontalListview;
import com.dtmining.latte.mk.ui.sub_delegates.views.SetTimesDialog;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.ToastUtil;
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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/11/2
 * Description:
 */
public class AddPlanByDrugDelegate extends LatteDelegate implements SetTimesDialog.ClickListenerInterface {

    private String musicUriStr="";
    private HandlerThread handlerThread=new HandlerThread("");
    private Handler myHandler=null;
    private String msgid=null;
    private String doseUnit="";
    private int medicineType=-1;
    private int medicineUsecount=0;
    private MyDBOpenHelper myDBOpenHelper;
    private ArrayList<String>total_original_time_set=new ArrayList<>();//所有药品的已有plan的time集合(由于计划总数不能超过8，所以totaltotal_original_time_set的大小加上新加入的题meset不能大于8)
    private ArrayList<String>original_time_set=new ArrayList<>();//某一药品的已有plan的time集合
    String tel=null;
    int origin_time_count_size=0;//已经添加过的的用药计划：服药时间：用量,再添加时，这部分不添加
    String boxId=null;
    String interval=null;
    String medicineId=null;
    boolean alarmSet=false;
    MedicineModel medicineModel=null;
    SetTimesDialog setTimesDialog=null;
    private MedicineListDataConverter converter=null;
    private MedicineListAdapter mAdapter=null;
    private ArrayList<String> timeSet=new ArrayList<String>();
    private ArrayList<String> useCountSet=new ArrayList<String>();
    //private ArrayList<MedicinePlan> plans =new ArrayList<>();
    @BindView(R2.id.take_plan_add_by_drug_horizontallistview)
    HorizontalListview mHorizontalListView=null;
    //药名列表
    @BindView(R2.id.sp_delegate_medicine_take_plan_add_by_drug_medicine_name)
    Spinner mMedicineListSpinner=null;
    //时间设置
    @BindView(R2.id.btn_delegate_medicine_take_plan_add_by_drug_time_set)
    Button setTimeButton=null;
    @BindView(R2.id.btn_delegate_medicine_take_plan_add_by_drug_set_music)
    Button setAlarmButton=null;
    //设置铃声
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_set_music)
    void setAlarm(){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置通知铃声");
        if (musicUriStr != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(musicUriStr));
        }
        startActivityForResult(intent, 0);
    }
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_time_set)
    void setPlanTime(){

        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_plan")
                .params("tel",tel)
                .params("boxId",LattePreference.getBoxId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject object= JSON.parseObject(response);
                        int code=object.getIntValue("code");
                        if(code==1) {
                            total_original_time_set.clear();
                            final JSONObject data = object.getJSONObject("detail");
                            final JSONArray dataArray = data.getJSONArray("planlist");
                            int size = dataArray.size();
                            for (int i = 0; i <size ; i++) {
                                JSONObject object1= (JSONObject) dataArray.get(i);
                                total_original_time_set.add(object1.getString("time"));
                            }
                            setTimesDialog = new SetTimesDialog(getContext(),timeSet,original_time_set,total_original_time_set,useCountSet, "确定","取消", AddPlanByDrugDelegate.this,medicineUsecount,doseUnit);
                            setTimesDialog.show();
                        }
                    }
                })
                .build()
                .get();




    }
    //确定提交整个表单
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_confirm)
    void confirmPlan(){
        if (checkForm()) {
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/get_plan")
                    .params("tel", tel)
                    .params("boxId", LattePreference.getBoxId())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            JSONObject object= JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            if(code==1) {
                                final JSONObject data = object.getJSONObject("detail");
                                final JSONArray dataArray = data.getJSONArray("planlist");

                                int size1 = dataArray.size();
                                if(size1<8){
                                    int size=timeSet.size();
                                    if(size>origin_time_count_size) {//表示有新的计划加入避免了plans为空的情况
                                        Log.d("sizeofplans", size+":"+origin_time_count_size);
                                        MedicinePlan medicinePlan = new MedicinePlan();
                                        Detail detail = new Detail();
                                        MedicinePlanNetModel medicinePlanNetModel = new MedicinePlanNetModel();
                                        for (int i = origin_time_count_size; i < size; i++) {//添加的时候从新加入的开始
                                            TimeCountPair pair = new TimeCountPair();
                                            pair.setMedicine_time(timeSet.get(i));
                                            pair.setMedicine_useCount(useCountSet.get(i));
                                            medicinePlan.addPair(pair);
                                        }
                                        detail.setMedicine_plan(medicinePlan);
                                        detail.setMedicineId(medicineModel.getMedicineId());
                                        medicinePlanNetModel.setDetail(detail);
                                        String planJson = JSON.toJSON(medicinePlanNetModel).toString();
                                        Log.d("addbydrug", planJson);
                                        RestClient.builder()
                                                .url(UploadConfig.API_HOST + "/api/Medicine_set_time")//提交计划
                                                .clearParams()
                                                .raw(planJson)
                                                .success(new ISuccess() {
                                                    @Override
                                                    public void onSuccess(String response) {
                                                        JSONObject object = JSON.parseObject(response);
                                                        int code = object.getIntValue("code");
                                                        if (code == 1) {
                                                            msgid = object.getString("msgid");
                                                            Log.d("msgid", "msgid=" + msgid);
                                                            if (msgid != null) {
                                                                myHandler.postDelayed(updateThread, 1000);
                                                            }

                                                        }
                                                    }
                                                })
                                                .build()
                                                .post();
                                    }

                                }else{
                                    Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY),"用药计划数已超8个，无法设置新的用药计划",Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    })
                    .build()
                    .get();

        }
    }
    @OnItemSelected(R2.id.sp_delegate_medicine_take_plan_add_by_drug_medicine_name)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        timeSet.clear();
        original_time_set.clear();
        medicineModel=mAdapter.getItem(position);
        MedicinePlans medicinePlans=medicineModel.getMedicinePlans();
        medicineType=medicineModel.getMedicintType();
        medicineUsecount=medicineModel.getMedicineUsecount();
        switch (medicineType) {
            case 0:
                doseUnit = "片";
                break;
            case 1:
                doseUnit = "粒/颗";
                break;
            case 2:
                doseUnit = "瓶/支";
                break;
            case 3:
                doseUnit = "包/袋";
                break;
            case 4:
                doseUnit = "克";
                break;
            case 5:
                doseUnit = "毫升";
                break;
            case 6:
                doseUnit = "其他";
                break;
        }
        if(medicinePlans!=null)
        {
            timeSet=medicineModel.getMedicinePlans().getTime();//药品包含的计划的时间串，
            useCountSet= medicineModel.getMedicinePlans().getUseCount();//药品包含的计划的用linag
            int size=timeSet.size();
            origin_time_count_size=size;//取得原始的数据量
            ArrayList<String> time_count=new ArrayList<>();
            for (int i = 0; i <size ; i++) {
                original_time_set.add(timeSet.get(i));
                time_count.add(timeSet.get(i)+"剂量:"+useCountSet.get(i)+doseUnit);
            }
            set_time_count_tag(time_count);

        }else
        {
            set_time_count_tag(new ArrayList<String>());
        }
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan_add_by_drug;
    }
    private Date getDate(String time,int interval) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
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
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId=LattePreference.getBoxId();
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
            if(boxId==null){
                Toast.makeText(getContext(),"App未绑定当前药箱",Toast.LENGTH_LONG).show();
            }
        }
        //ArrayAdapter adap = new ArrayAdapter<String>(getContext(), R.layout.single_item_tv, new String[]{"每天", "间隔1天","间隔2天","间隔3天","间隔4天","间隔5天","间隔6天","间隔7天"});
        //mTimeSpanSpinner.setAdapter(adap);
        getMedicineList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerThread.start();
        myHandler=new Handler(handlerThread.getLooper());
        myDBOpenHelper = MyDBOpenHelper.getInstance((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY));
        getMedicineList();
        getTotalPlans();
        //myDBOpenHelper.getWritableDatabase();
    }

    private void getMedicineList(){
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_medicine_of_box")
                .params("tel",tel)
                .params("boxId",LattePreference.getBoxId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                            converter = new MedicineListDataConverter();
                            mAdapter = MedicineListAdapter.create(converter.setJsonData(response), R.layout.simple_single_item_list);
                            mMedicineListSpinner.setAdapter(mAdapter);
                    }
                })
                .build()
                .get();

    }
    // 设置时间提示标签　tips
    private void set_time_count_tag(ArrayList<String> the_time_tags){
        SimpleHorizontalAdapter  horizontalAdapter = new SimpleHorizontalAdapter(the_time_tags, getContext());
        mHorizontalListView.setAdapter(horizontalAdapter);
        //horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public void doConfirm(ArrayList<String> times,ArrayList<String>counts,String doseUnit) {
        timeSet=times;
        useCountSet=counts;
        ArrayList<String>time_useCount=new ArrayList<>();
        int size=times.size();
        for (int i = 0; i <size ; i++) {
            time_useCount.add(timeSet.get(i)+"剂量:"+useCountSet.get(i)+doseUnit);
        }
        set_time_count_tag(time_useCount);
        setTimesDialog.dismiss();
    }

    @Override
    public void doCancel() {
        setTimesDialog.dismiss();
    }
    public boolean checkForm(){

        boolean isPass=true;

        TextView textView= (TextView) mMedicineListSpinner.getChildAt(0);
        if(textView!=null){
            if(textView.getText().toString().equalsIgnoreCase("请选择药品"))
            {
                textView.setError("请选择药品");
                isPass=false;
            }else {
                textView.setError(null);

            }
        }

        if(timeSet.size()==0)
        {
            setTimeButton.setError("请设置服药时间和剂量");

        }else {
            setTimeButton.setError(null);
        }
        return isPass;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // myDBOpenHelper.close();// 释放数据库资源
    }
    Runnable updateThread=new Runnable() {
        @Override
        public void run() {
            RestClient.builder()
                    .clearParams()
                    .params("uuid",msgid)
                    //.url("http://192.168.1.3:8081/Web01_exec/getStatus")
                    .url(UploadConfig.API_HOST+"/api/getStatus")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            JSONObject object=JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            Log.d("statuscode", code+"");
                            if(code==1){
                                ToastUtil.showToast((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划已添加等待向硬件端同步");
                                myHandler.postDelayed(updateThread,1000);
                            }
                            if(code==2) {
                                myHandler.removeCallbacks(updateThread);
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划已成功添加", Toast.LENGTH_LONG).show();
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
                                synPlanWithSqlite();
                                pop();

                            }
                            if(code==3||code==4){
                                myHandler.removeCallbacks(updateThread);
                                ToastUtil.showToast((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划添加失败，请重新添加");
                            }
                        }
                    })
                    .build()
                    .get();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            musicUriStr = pickedUri.toString();
        } catch (Exception e) {
            e.printStackTrace();
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
                                Log.d("map_time123", map_time.toString());
                                if (map_time != null) {
                                    myDBOpenHelper.deleteAlarm();
                                    //清空计划表
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
                                                                int interval = Integer.parseInt(Interval)+1;//由于每天服用表示间隔0，但实际这在设置闹钟时还是相差1天，
                                                                Alarm alarm = new Alarm(getDate(starttime, interval), hour, minute, interval, message, musicUriStr, 1);
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
                                        setAlarmAccordSqlite();
                                    }
                                }
                            }
                        }

                    }
                })
                .build()
                .get();
    }
    private void setAlarmAccordSqlite(){
        List<Alarm> alarms = myDBOpenHelper.query();
        int[] alarmIds   =new int[alarms.size()];
        int size=alarms.size();
        for (int i = 0; i <size ; i++) {
            alarmIds[i]=alarms.get(i).getId();
        }
        int length=alarmIds.length;
        for (int i = 0; i <length ; i++) {
            int alarmid=alarmIds[i];
            AlarmOpreation.enableAlert((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY),alarmid,alarmIds);
        }
    }
    private void getTotalPlans(){
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_plan")
                .params("tel",tel)
                .params("boxId",LattePreference.getBoxId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject object= JSON.parseObject(response);
                        int code=object.getIntValue("code");
                        if(code==1) {
                            final JSONObject data = object.getJSONObject("detail");
                            final JSONArray dataArray = data.getJSONArray("planlist");
                            int size = dataArray.size();
                            for (int i = 0; i <size ; i++) {
                                JSONObject object1= (JSONObject) dataArray.get(i);
                                total_original_time_set.add(object1.getString("time"));
                            }
                        }
                    }
                })
                .build()
                .get();

    }
}
