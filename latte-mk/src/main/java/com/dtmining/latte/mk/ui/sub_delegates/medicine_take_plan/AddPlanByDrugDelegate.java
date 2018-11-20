package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.delegates.bottom.BottomTabBean;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.adapter.SimpleHorizontalAdapter;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListDataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.views.HorizontalListview;
import com.dtmining.latte.mk.ui.sub_delegates.views.SetTimesDialog;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.ArrayList;
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

    String tel=null;
    String boxId=null;
    String interval=null;
    boolean alarmSet=false;
    MedicineModel medicineModel=null;
    SetTimesDialog setTimesDialog=null;
    private MedicineListDataConverter converter=null;
    private MedicineListAdapter mAdapter=null;
    private ArrayList<String> timeSet=new ArrayList<String>();
    private ArrayList<MedicinePlan> plans =new ArrayList<>();
    @BindView(R2.id.take_plan_add_by_drug_horizontallistview)
    HorizontalListview mHorizontalListView=null;
    //药名列表
    @BindView(R2.id.sp_delegate_medicine_take_plan_add_by_drug_medicine_name)
    Spinner mMedicineListSpinner=null;
    //时间间隔
    @BindView(R2.id.sp_delegate_medicine_take_plan_add_by_drug_time_span)
    Spinner mTimeSpanSpinner=null;

    @OnItemSelected(R2.id.sp_delegate_medicine_take_plan_add_by_drug_time_span)
    void getInterVal(AdapterView<?> parent, View view, int position, long id){
        interval=position+"";
    }
    //用药量
    @BindView(R2.id.et_delegate_medicine_take_plan_add_by_drug_medicine_count)
    EditText mMedicineUseCount=null;
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
        setTimesDialog = new SetTimesDialog(getContext() , "确定","取消", this);
        setTimesDialog.show();
    }
    //确定提交整个表单
    @OnClick(R2.id.btn_delegate_medicine_take_plan_add_by_drug_confirm)
    void confirmPlan(){
        if (checkForm()) {
            String medicineUseCount = mMedicineUseCount.getText().toString();
            int size=timeSet.size();
            MedicinePlans medicinePlans=new MedicinePlans();
            for (int i = 0; i <size ; i++) {
                MedicinePlan medicinePlan=new MedicinePlan();
                medicinePlan.setAtime(timeSet.get(i).toString());
                medicinePlan.setMedicineUseCount(medicineUseCount);
                medicinePlan.setMedicineName(medicineModel.getMedicineName());
                medicinePlan.setMedicineId(medicineModel.getMedicineId());
                medicinePlan.setInterval(interval);
                medicinePlans.addMedicinePlan(medicinePlan);
            }
            medicineModel.setMedicinePlans(medicinePlans);
            String planJson = JSON.toJSON( medicineModel).toString();
            Log.d("json", planJson);
            RestClient.builder()
                    .url("http://10.0.2.2:8081/Web01_exec/UserLogin")//提交计划
                    .raw(planJson)
                    .clearParams()
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            RestClient.builder()
                                    .url("")//获取所有现有计划，成功后取得时间信息，设置闹钟
                                    .params("tel",tel)
                                    .params("boxId",boxId)
                                    .success(new ISuccess() {
                                        @Override
                                        public void onSuccess(String response) {
                                            if(response!=null) {
                                                JSONObject jsonData = JSON.parseObject(response);
                                                JSONObject detail = jsonData.getJSONObject("detail");
                                                JSONArray planLsit = detail.getJSONArray("planlist");
                                                HashMap<String, HashMap<String, HashMap<String,ArrayList<String>>>> map_time = new HashMap<>();
                                                int size = planLsit.size();
                                                for (int i = 0; i < size; i++) {
                                                    JSONObject planData = (JSONObject) planLsit.get(i);
                                                    String time = planData.getString("time");
                                                    JSONArray planPairArray = planData.getJSONArray("plan");
                                                    HashMap<String, HashMap<String,ArrayList<String>>> map_interval = new HashMap<>();
                                                    map_interval.put("0", null);
                                                    map_interval.put("1", null);
                                                    map_interval.put("2", null);
                                                    map_interval.put("3", null);
                                                    map_interval.put("4", null);
                                                    map_interval.put("5", null);
                                                    map_interval.put("6", null);
                                                    map_interval.put("7", null);
                                                    int size2 = planPairArray.size();
                                                    for (int j = 0; j < size2; j++) {
                                                        JSONObject planPair = (JSONObject) planPairArray.get(j);
                                                        String interval = planPair.getString("interval");
                                                        String starttime = planPair.getString("startTime");
                                                        String medicineUseCount = planPair.getString("medicine_usecount");
                                                        String medicineName = planPair.getString("medicine_name");
                                                        //先以interval判断，如果
                                                        if (map_interval.get(interval) == null) {
                                                            HashMap<String, ArrayList<String>> map_start_time = new HashMap<>();
                                                            ArrayList<String> infoArray = new ArrayList<>();
                                                            infoArray.add(medicineName + " " + medicineUseCount);
                                                            map_start_time.put(starttime, infoArray);
                                                            map_interval.put(interval, map_start_time);
                                                        } else {
                                                            boolean has = false;
                                                            for (Map.Entry<String, HashMap<String,ArrayList<String>>> item_interval : map_interval.entrySet()) {
                                                                HashMap<String, ArrayList<String>> hashMap = item_interval.getValue();
                                                                for (Map.Entry<String, ArrayList<String>> item_start_time : hashMap.entrySet()) {
                                                                    String key_startTime = item_start_time.getKey();
                                                                    if (key_startTime.equalsIgnoreCase(starttime)) {
                                                                        has = true;
                                                                    }
                                                                }
                                                            }
                                                            if (has) {
                                                                ((ArrayList<String>) map_interval.get(interval).get(starttime)).add("," + medicineName + " " + medicineUseCount);
                                                            } else {
                                                                ArrayList<String> infoArray = new ArrayList<>();
                                                                infoArray.add(medicineName + " " + medicineUseCount);
                                                                map_interval.get(interval).put(starttime, infoArray);
                                                            }
                                                        }
                                                    }
                                                    map_time.put(time, map_interval);
                                                }
                                                if (map_time != null) {
                                                    for (Map.Entry<String, HashMap<String,HashMap<String,ArrayList<String>>>> item_time : map_time.entrySet()){
                                                        String time=null;
                                                        if(item_time!=null) {
                                                            time = item_time.getKey();
                                                            HashMap<String, HashMap<String,ArrayList<String>>> map_interval = item_time.getValue();
                                                            for (Map.Entry<String, HashMap<String,ArrayList<String>>> item_interval : map_interval.entrySet()){
                                                                String interval=null;
                                                                if(item_interval!=null)
                                                                {
                                                                    interval=item_interval.getKey();
                                                                    HashMap<String,ArrayList<String>> map_starttime = item_interval.getValue();
                                                                    for (Map.Entry<String,ArrayList<String>> item_starttime : map_starttime.entrySet()){
                                                                        String starttime=null;
                                                                        String message=null;
                                                                        if(item_starttime!=null)
                                                                        {
                                                                            starttime=item_starttime.getKey();
                                                                            message= item_starttime.getValue().toString();

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
            set_time_tag(timeSet);

        }else
        {
            set_time_tag(new ArrayList<String>());
        }
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_plan_add_by_drug;
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
        ArrayAdapter adap = new ArrayAdapter<String>(getContext(), R.layout.single_item_tv, new String[]{"每天", "间隔1天","间隔2天","间隔3天","间隔4天","间隔5天","间隔6天","间隔7天"});
        mTimeSpanSpinner.setAdapter(adap);
        getMedicineList();
    }
    private void getMedicineList(){
        RestClient.builder()
                .url("http://10.0.2.2:8081/Web01_exec/get_medicine_of_box")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        converter=new MedicineListDataConverter();
                        mAdapter= MedicineListAdapter.create(converter.setJsonData(response),R.layout.simple_single_item_list);
                        mMedicineListSpinner.setAdapter(mAdapter);
                    }
                })
                .build()
                .get();

    }
    // 设置时间提示标签　tips
    private void set_time_tag(ArrayList<String> the_time_tags){
        SimpleHorizontalAdapter  horizontalAdapter = new SimpleHorizontalAdapter(the_time_tags, getContext());
        mHorizontalListView.setAdapter(horizontalAdapter);
        //horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public void doConfirm(ArrayList<String> times) {
        timeSet=times;
        set_time_tag(timeSet);
        setTimesDialog.dismiss();
    }

    @Override
    public void doCancel() {
        setTimesDialog.dismiss();
    }
    public boolean checkForm(){
        String medicineUseCount=mMedicineUseCount.getText().toString();
        boolean isPass=true;
        if(mMedicineListSpinner.getChildAt(0)!=null) {
            if (((TextView) mMedicineListSpinner.getChildAt(0).findViewById(R.id.single_item_tv)).getText().toString().equalsIgnoreCase("请选择药品")) {
                ((TextView) mMedicineListSpinner.getChildAt(0).findViewById(R.id.single_item_tv)).setError("请选择药箱Id");
                isPass = false;
            } else {
                ((TextView) mMedicineListSpinner.getChildAt(0).findViewById(R.id.single_item_tv)).setError(null);
            }
        }
        if(timeSet.size()==0)
        {
            setTimeButton.setError("时间设置");

        }else {
            setTimeButton.setError(null);
        }
        if(medicineUseCount.isEmpty())
        {
            mMedicineUseCount.setError("请填写剂量");
        }else{
            mMedicineUseCount.setError(null);
        }
/*      if(!alarmSet)
        {
            setAlarmButton.setError("设置提醒音乐");
        }
        else {
            setTimeButton.setError(null);
        }*/
        return isPass;
    }
}
