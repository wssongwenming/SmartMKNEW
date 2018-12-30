package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.CustomDatePicker;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.regex.RegexTool;
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/11/22
 * Description:
 */
public class UpdatePlanDelegate extends LatteDelegate {
    private HandlerThread handlerThread=new HandlerThread("");
    private Handler myHandler=null;
    private String msgid=null;
    private String tel;
    private String medicineId=null;
    private  String medicineName=null;
    private String planId =null;
    private String medicineUsecount;
    private String atime=null;
    private String boxId=null;
    private MedicineListDataConverter converter=null;
    private MedicineListAdapter mAdapter=null;
    private CustomDatePicker customDatePicker;
    private static final String PLAN_DATA = "PLAN_DATA";
    private String planString=null;
    @BindView(R2.id.sp_delegate_medicine_take_plan_edit_medicine_name)
    Spinner mMedicineListSpinner=null;
    @OnItemSelected(R2.id.sp_delegate_medicine_take_plan_edit_medicine_name)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //Toast.makeText(this.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
        medicineId=((MedicineModel)parent.getItemAtPosition(position)).getMedicineId();
        Toast.makeText(this.getContext(),medicineId,Toast.LENGTH_SHORT).show();
    }
    @BindView(R2.id.edit_plan_update_medicine_use_count)
    AppCompatEditText mMedicineUseCount=null;
    @BindView(R2.id.edit_plan_update_medicine_use_time)
    AppCompatEditText mMedicineUseTime=null;

    @OnClick(R2.id.edit_plan_update_medicine_use_time)
    void setTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker.show(now);
    }
    @OnClick(R2.id.btn_plan_update_submit)
    void submit(){
        if(checkForm()){
            //String medicineName=mMedicineName.getText().toString();
            String planid =planId;
            int medicineUsecount=Integer.parseInt(mMedicineUseCount.getText().toString());
            String atime=mMedicineUseTime.getText().toString();
            String boxid=boxId;

            JsonObject detail=new JsonObject();
            JsonObject updateJson=new JsonObject();
            detail.addProperty("atime",atime);
            detail.addProperty("id",planid);
            detail.addProperty("medicineUseCount",medicineUsecount);
            detail.addProperty("boxId",boxid);
            detail.addProperty("medicineId",medicineId);
            updateJson.add("detail",detail);
            Log.d("updateplan", updateJson.toString());
            RestClient.builder()
                    .clearParams()
                    .raw(updateJson.toString())
                    .url(UploadConfig.API_HOST+"/api/update_plan")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            if(response!=null) {
                                JSONObject object=JSON.parseObject(response);
                                int code=object.getIntValue("code");
                                if(code==1)
                                {
                                    msgid=object.getString("msgid");
                                    Log.d("msgid", "msgid="+msgid);
                                    if(msgid!=null) {
                                        myHandler.postDelayed(updateThread, 1000);
                                    }
                                }
                            }

/*
                            final IGlobalCallback<String> UpdatePlanCallback_for_index = CallbackManager
                                    .getInstance()
                                    .getCallback(CallbackType.ON_GET_MEDICINE_PLAN_INDEX);
                            if (UpdatePlanCallback_for_index!= null) {
                                UpdatePlanCallback_for_index.executeCallback("");
                            }
                            final IGlobalCallback<String> UpdatePlanCallback = CallbackManager
                                    .getInstance()
                                    .getCallback(CallbackType.ON_GET_MEDICINE_PLAN);
                            if (UpdatePlanCallback != null) {
                                UpdatePlanCallback.executeCallback("");
                            }
                            pop();*/
                        }
                    })
                    .build()
                    .post();

            //Toast.makeText(getContext(),updateJson.toString(),Toast.LENGTH_LONG).show();

        }
    }
    private boolean checkForm(){
        final String medicineUseCount=mMedicineUseCount.getText().toString();
        final String atime=mMedicineUseTime.getText().toString();
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
        if(medicineUseCount.isEmpty()){
            mMedicineUseCount.setError("请填写服药剂量");
            isPass=false;
        }else{
            mMedicineUseCount.setError(null);
        }
        if(atime.isEmpty()){
            mMedicineUseTime.setError("请填写服药时间");
            isPass=false;
        }else{
            mMedicineUseTime.setError(null);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerThread.start();
        myHandler=new Handler(handlerThread.getLooper());
        initDatePicker();
        final Bundle args = getArguments();
        if (args != null) {
            planString = args.getString(PLAN_DATA);
        }
    }
    public static UpdatePlanDelegate newInstance(String planString){
        final Bundle args = new Bundle();
        args.putString(PLAN_DATA,planString);
        final UpdatePlanDelegate delegate = new UpdatePlanDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_plan_update;
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
        getMedicineList();

    }
    private void initData() {
        if(planString!=null) {
            JSONObject jsonObject= JSON.parseObject(planString);
            JSONObject jsonObject1=jsonObject.getJSONObject("detail");
            medicineName=jsonObject1.getString("medicineName");
            planId =jsonObject1.getString("planId");
            boxId=jsonObject1.getString("boxId");
            medicineUsecount=jsonObject1.getString("medicineUseCount");
            atime=jsonObject1.getString("atime");
            setSpinnerItemSelectedByValue(mMedicineListSpinner,medicineName);
            mMedicineUseCount.setText(medicineUsecount);
            mMedicineUseTime.setText(atime);
        }
    }
    private void getMedicineList(){
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        converter = new MedicineListDataConverter();
                        mAdapter = MedicineListAdapter.create(converter.setJsonData(response), R.layout.simple_single_item_list);
                        mMedicineListSpinner.setAdapter(mAdapter);
                        initData();
                    }
                })
                .build()
                .get();
    }
    public  void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(((MedicineModel)apsAdapter.getItem(i)).getMedicineName())){
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
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
                            Log.d("statuscode", msgid+"");
                            if(code==1){
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划已修改等待向硬件端同步", Toast.LENGTH_SHORT).show();
                                myHandler.postDelayed(updateThread,1000);
                            }
                            if(code==2){
                                myHandler.removeCallbacks(updateThread);
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划成功修改", Toast.LENGTH_LONG).show();

                                final IGlobalCallback<String> UpdatePlanCallback_for_index = CallbackManager
                                        .getInstance()
                                        .getCallback(CallbackType.ON_GET_MEDICINE_PLAN_INDEX);
                                if (UpdatePlanCallback_for_index!= null) {
                                    UpdatePlanCallback_for_index.executeCallback("");
                                }
                                final IGlobalCallback<String> UpdatePlanCallback = CallbackManager
                                        .getInstance()
                                        .getCallback(CallbackType.ON_GET_MEDICINE_PLAN);
                                if (UpdatePlanCallback != null) {
                                    UpdatePlanCallback.executeCallback("");
                                }
                                pop();

                            }
                            if(code==3||code==4){
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "用药计划修改失败，请重新操作", Toast.LENGTH_LONG).show();
                                myHandler.removeCallbacks(updateThread);

                            }
                        }
                    })
                    .build()
                    .get();


        }
    };
}
