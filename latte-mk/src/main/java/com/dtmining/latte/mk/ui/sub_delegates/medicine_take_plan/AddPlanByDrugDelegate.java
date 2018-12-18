package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
import android.os.Bundle;
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
import com.dtmining.latte.mk.ui.sub_delegates.views.HorizontalListview;
import com.dtmining.latte.mk.ui.sub_delegates.views.SetTimesDialog;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
    private MyDBOpenHelper myDBOpenHelper;
    private ArrayList<String>original_time_set=new ArrayList<>();
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
/*    //时间间隔
    @BindView(R2.id.sp_delegate_medicine_take_plan_add_by_drug_time_span)
    Spinner mTimeSpanSpinner=null;

    @OnItemSelected(R2.id.sp_delegate_medicine_take_plan_add_by_drug_time_span)
    void getInterVal(AdapterView<?> parent, View view, int position, long id){
        interval=position+"";
    }
    //用药量
    @BindView(R2.id.et_delegate_medicine_take_plan_add_by_drug_medicine_count)
    EditText mMedicineUseCount=null;*/
    //时间设置
    @BindView(R2.id.btn_delegate_medicine_take_plan_add_by_drug_time_set)
    Button setTimeButton=null;
    @BindView(R2.id.btn_delegate_medicine_take_plan_add_by_drug_set_music)
    Button setAlarmButton=null;
    //设置铃声
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_set_music)
    void setAlarm(){

    }
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_time_set)
    void setPlanTime(){
        setTimesDialog = new SetTimesDialog(getContext(),timeSet,original_time_set,useCountSet, "确定","取消", this);
        setTimesDialog.show();
    }
    //确定提交整个表单
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_confirm)
    void confirmPlan(){
        if (checkForm()) {
            //String medicineUseCount = mMedicineUseCount.getText().toString();
            int size=timeSet.size();
            //MedicinePlans medicinePlans=new MedicinePlans();
            MedicinePlan medicinePlan=new MedicinePlan();
            Detail detail=new Detail();
            MedicinePlanNetModel medicinePlanNetModel=new MedicinePlanNetModel();
            for (int i = origin_time_count_size; i <size ; i++) {//添加的时候从新加入的开始
                TimeCountPair pair=new TimeCountPair();
                pair.setMedicine_time(timeSet.get(i));
                pair.setMedicine_useCount(useCountSet.get(i));
                medicinePlan.addPair(pair);
            }
            detail.setMedicine_plan(medicinePlan);
            detail.setMedicineId(medicineModel.getMedicineId());
            medicinePlanNetModel.setDetail(detail);
            String planJson = JSON.toJSON( medicinePlanNetModel).toString();
            Log.d("addbydrug", planJson);
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/Medicine_set_time")//提交计划
                    .clearParams()
                    .raw(planJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            JSONObject object = JSON.parseObject(response);
                            int code = object.getIntValue("code");
                            if (code == 1)
                            {
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
                                RestClient.builder()
                                        .url(UploadConfig.API_HOST + "/api/get_plan")//获取所有现有计划，成功后取得时间信息，设置闹钟
                                        .params("tel", tel)
                                        .params("boxId", boxId)
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
                                        .get();
                        }
                    }
                    })
                    .build()
                    .post();
        }
    }
    @OnItemSelected(R2.id.sp_delegate_medicine_take_plan_add_by_drug_medicine_name)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        medicineModel=mAdapter.getItem(position);
        MedicinePlans medicinePlans=medicineModel.getMedicinePlans();
        if(medicinePlans!=null)
        {
            timeSet=medicineModel.getMedicinePlans().getTime();
            useCountSet= medicineModel.getMedicinePlans().getUseCount();
            int size=timeSet.size();
            origin_time_count_size=size;//取得原始的数据量
            ArrayList<String> time_count=new ArrayList<>();
            for (int i = 0; i <size ; i++) {
                original_time_set.add(timeSet.get(i));
                time_count.add(timeSet.get(i)+"剂量:"+useCountSet.get(i));
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
        myDBOpenHelper = MyDBOpenHelper.getInstance((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY));
        getMedicineList();
        //myDBOpenHelper.getWritableDatabase();
    }

    private void getMedicineList(){
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_medicine")
                .clearParams()
                .params("tel",tel)
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
    public void doConfirm(ArrayList<String> times,ArrayList<String>counts) {
        timeSet=times;
        useCountSet=counts;
        ArrayList<String>time_useCount=new ArrayList<>();
        int size=times.size();
        for (int i = 0; i <size ; i++) {
            time_useCount.add(timeSet.get(i)+"剂量:"+useCountSet.get(i));
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

}
