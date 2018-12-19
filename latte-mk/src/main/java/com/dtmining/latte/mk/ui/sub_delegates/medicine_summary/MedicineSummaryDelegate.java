package com.dtmining.latte.mk.ui.sub_delegates.medicine_summary;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.layoutmanager.MyLayoutManager;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue.MedicineOverdueDataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue.MedicineOverdueRefreshHandler;
import com.dtmining.latte.ui.date.DateDialogUtil;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dtmining.latte.util.tool.translate;

/**
 * author:songwenming
 * Date:2018/11/25
 * Description:
 */
public class MedicineSummaryDelegate extends LatteDelegate{
    private MedicineSummaryRefreshHandler mRefreshHandler=null;
    private String begin_time="2016-12-12";
    private String end_time="2100-12-12";
    private int _year;
    private int _month;
    private int _day;
    private Date date = null;
    private String tel=null;
    @BindView(R2.id.btn_medicine_summary_begintime)
    AppCompatButton mButtonBegintime=null;
    @BindView(R2.id.btn_medicine_summary_endtime)
    AppCompatButton mButtonEndtime=null;
    @BindView(R2.id.rv_medicine_summary)
    RecyclerView mRecyclerView=null;
    @OnClick(R2.id.btn_medicine_summary_begintime)
    void getBeginTime(){
        final DateDialogUtil dateDialogUtil = new DateDialogUtil();
        dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
            @Override
            public void onDateChange(String date) {
                mButtonBegintime.setText(date.replace("年","-").replace("月","-").replace("日",""));
                begin_time=mButtonBegintime.getText().toString();
            }
        });
        dateDialogUtil.showDialog(getContext());
    }
    @OnClick(R2.id.btn_medicine_summary_endtime)
    void getEndTime(){
        final DateDialogUtil dateDialogUtil = new DateDialogUtil();
        dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
            @Override
            public void onDateChange(String date) {
                mButtonEndtime.setText(date.replace("年","-").replace("月","-").replace("日",""));
                end_time=mButtonEndtime.getText().toString();
            }
        });
        dateDialogUtil.showDialog(getContext());
      }
    @OnClick(R2.id.btn_summary_confirm)
    void summaryByTimeSpan(){
        mRefreshHandler.getMedicineSummary(UploadConfig.API_HOST+"/api/medicine_collection",tel,begin_time,end_time);
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_summary;
    }
    private void initRecyclerView() {
        final LinearLayoutManager linearLayoutManager_history=new MyLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
        mRefreshHandler= MedicineSummaryRefreshHandler.create(null,mRecyclerView,new MedicineSummaryConverter());
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        mRefreshHandler.getMedicineSummary(UploadConfig.API_HOST+"/api/medicine_collection",tel,begin_time,end_time);
    }
}
