package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

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
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

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


    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        myDBOpenHelper.close();// 释放数据库资源
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("ok", "onResume: ");
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
                //.url(UploadConfig.API_HOST+"/api/get_plan")
                .url("medicine_plan")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        MedicinePlanExpandableListViewAdapter medicinePlanExpandableListViewAdapter=new MedicinePlanExpandableListViewAdapter(response,sets,MedicineTakePlanDelegate.this);
                        MedicinePlanExpandableListViewAdapter.convert(response);
                        mExpandableListView.setAdapter(medicinePlanExpandableListViewAdapter);
                    }
                })
                .build()
                .post();
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
        //AlarmOpreation.enableAlert(getContext(),3,new int[1]);
       }

/*    public void enableAlert(Context context,int Id){
        Alarm alarm= dbManager.queryById(Id);
        AlarmManager mAlarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        Alarm alarm1=dbManager.queryById(Id);
        Context CONTEXT=context;
        int type=Id;
        Date date=alarm.getStarttime();
        int hour=alarm.getHour();
        int minute=alarm.getMinute();
        int interval= alarm.getInterval();
        String messsage=alarm.getMessage();
        Calendar mCalendar = cacluteNextAlarm(date,hour, minute, interval);//选择了一周中的哪几天，比如周一、周三、周四,可以用daydiffer
        if (mCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(getContext(),"setAlarm FAIL:设置时间不能小于当前系统时间，本次"+mCalendar.getTimeInMillis()+"闹钟无效",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(AlarmsSetting.ALARM_ALERT_ACTION);
        intent.putExtra("nextalarm",mCalendar.getTimeInMillis());
        intent.putExtra("type", Id);
        intent.putExtra("message", messsage);
        intent.setClass(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, type, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pi);

    }

    public static Calendar cacluteNextAlarm(Date startTime,int hour,int minute,int differDays){//可以用differDay计算下一次铃声

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(startTime);
        mCalendar.set(Calendar.HOUR_OF_DAY,hour);
        mCalendar.set(Calendar.MINUTE, minute);
        Log.d("diff", "diffday="+differDays+"");
        int nextYear = getNextAlarmYear(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.DAY_OF_YEAR), mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR), differDays);
        int nextMonth = getNextAlarmMonth(mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.getActualMaximum(Calendar.DATE), differDays);
        int nextDay = getNextAlarmDay(mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.getActualMaximum(Calendar.DATE), differDays);
        Log.d("nextday", nextDay+"");
        mCalendar.set(Calendar.YEAR,nextYear);
        mCalendar.set(Calendar.MONTH, nextMonth % 12);//月份从0开始
        mCalendar.set(Calendar.DAY_OF_MONTH, nextDay);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        Log.e("cal1", " mCalendar" + mCalendar.get(Calendar.DAY_OF_WEEK));
        return mCalendar;
    }
    //考虑年进位的情况
    private static int getNextAlarmYear(int year,int dayOfYears, int actualMaximum, int differDays) {
        int temp = actualMaximum-dayOfYears-differDays;
        return temp >= 0?year:year+1;
    }

    //考虑月进位的情况
    private static int getNextAlarmMonth(int month,int dayOfMonth,int actualMaximum, int differDays) {
        int temp = actualMaximum-dayOfMonth-differDays;
        return temp >= 0?month:month+1;
    }

    //获取下次闹钟的day
    private static int getNextAlarmDay(int thisDayOfMonth, int actualMaximum, int differDays) {
        int temp = actualMaximum - thisDayOfMonth-differDays;
        if (temp<0){
            return thisDayOfMonth + differDays - actualMaximum;
        }
        return thisDayOfMonth + differDays;
    }*/
}
