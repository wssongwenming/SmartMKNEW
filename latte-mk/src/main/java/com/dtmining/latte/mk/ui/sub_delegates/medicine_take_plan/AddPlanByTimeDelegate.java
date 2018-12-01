package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dtmining.latte.alarmclock.DBManager;
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
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/2
 * Description:
 */
public class AddPlanByTimeDelegate extends LatteDelegate implements View.OnClickListener, CheckMedicinesDialog.ClickListenerInterface{
    private DBManager dbManager;
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
            detail.add("medicine_plan",detail);
            medicinePlanSubModelbyTime.add("detail",detail);
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/Medicine_set_time")
                    .clearParams()
                    .raw(medicinePlanSubModelbyTime.toString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            pop();
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
        ArrayList<String>medicineNames_useCount=new ArrayList<>();
        int size=medicineStateList.size();
        for (int i = 0; i <size ; i++) {
            medicineNames_useCount.add(medicineSet.get(i).getMedicineName()+":"+useCountSet.get(i));
            //
        }
        set_medicine_tag(medicineNames_useCount);
        checkMedicinesDialog.dismiss();
    }
    // 设置时间提示标签　tips
    private void set_medicine_tag(ArrayList<String> the_medicine_tags){
        SimpleHorizontalAdapter horizontalAdapter = new SimpleHorizontalAdapter(the_medicine_tags, getContext());
        mHorizontalListView.setAdapter(horizontalAdapter);
        horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
