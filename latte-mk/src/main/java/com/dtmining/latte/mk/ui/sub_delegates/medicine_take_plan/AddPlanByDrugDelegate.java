package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
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
    private HandlerThread handlerThread=new HandlerThread("");
    private Handler myHandler=null;
    private String msgid=null;
    private String doseUnit="";
    private int medicineType=-1;
    private int medicineUsecount=0;
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
        //Toast.makeText(getContext(),doseUnit,Toast.LENGTH_LONG).show();
        setTimesDialog = new SetTimesDialog(getContext(),timeSet,original_time_set,useCountSet, "确定","取消", this,medicineUsecount,doseUnit);
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
                                msgid=object.getString("msgid");
                                Log.d("msgid", "msgid="+msgid);
                                if(msgid!=null) {
                                    myHandler.postDelayed(updateThread, 1000);
                                }
                               /* final IGlobalCallback<String> callback_medicine_plan = CallbackManager
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
                                pop();*/
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
                doseUnit = "包";
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
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划已添加等待向硬件端同步", Toast.LENGTH_SHORT).show();
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
                                pop();

                            }
                            if(code==3||code==4){
                                myHandler.removeCallbacks(updateThread);
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划添加失败，请重新添加", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .build()
                    .get();


        }
    };
}
