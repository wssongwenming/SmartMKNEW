package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.CustomDatePicker;
import com.dtmining.latte.util.regex.RegexTool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * author:songwenming
 * Date:2018/11/22
 * Description:
 */
public class UpdatePlanDelegate extends LatteDelegate {
    private  String medicineName=null;
    private String planId =null;
    private String medicineUsecount=null;
    private String atime=null;
    private CustomDatePicker customDatePicker;
    private static final String PLAN_DATA = "PLAN_DATA";
    private String planString=null;
    @BindView(R2.id.edit_plan_update_medicine_name)
    AppCompatEditText mMedicineName=null;
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

        }
    }
    private boolean checkForm(){
        final String medicineName=mMedicineName.getText().toString();
        final String medicineUseCount=mMedicineUseCount.getText().toString();
        final String atime=mMedicineUseTime.getText().toString();
        boolean isPass=true;
        if(medicineName.isEmpty()){
            mMedicineName.setError("请填写正确的电话号码！");
            isPass=false;
        }else{
            mMedicineName.setError(null);
        }
        if(medicineUseCount.isEmpty()){
            mMedicineUseCount.setError("请填写服药剂量");
            isPass=false;
        }else{
            mMedicineUseCount.setError(null);
        }
        if(atime.isEmpty()){
            mMedicineUseTime.setError("请填写服药剂量");
            isPass=false;
        }else{
            mMedicineUseTime.setError(null);
        }
        return isPass;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        initData();
        mMedicineName.setFocusable(false);
    }
    private void initData() {
        if(planString!=null) {
            JSONObject jsonObject= JSON.parseObject(planString);
            JSONObject jsonObject1=jsonObject.getJSONObject("detail");
             medicineName=jsonObject1.getString("medicineName");
             planId =jsonObject1.getString("planId");
             medicineUsecount=jsonObject1.getString("medicineUseCount");
             atime=jsonObject1.getString("atime");
            mMedicineName.setText(medicineName);
            mMedicineUseCount.setText(medicineUsecount);
            mMedicineUseTime.setText(atime);
        }
    }
}
