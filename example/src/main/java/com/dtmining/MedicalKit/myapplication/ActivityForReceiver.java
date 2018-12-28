package com.dtmining.MedicalKit.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.dtmining.latte.activities.ProxyActivity;
import com.dtmining.latte.alarmclock.Alarm;
import com.dtmining.latte.alarmclock.MyDBOpenHelper;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.alarm.AlarmOpreation;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/12/27
 * Description:
 */
public class ActivityForReceiver extends Activity {
    private MyDBOpenHelper myDBOpenHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_for_receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAlarmAccordSqlite(this);
        moveTaskToBack(true);
    }

    private void setAlarmAccordSqlite(Context context){
        myDBOpenHelper= MyDBOpenHelper.getInstance(context);
        List<Alarm> alarms = myDBOpenHelper.query();
        int[] alarmIds   =new int[alarms.size()];
        int size=alarms.size();
        for (int i = 0; i <size ; i++) {
            alarmIds[i]=alarms.get(i).getId();
        }
        int length=alarmIds.length;
        for (int i = 0; i <length ; i++) {
            int alarmid=alarmIds[i];
            AlarmOpreation.enableAlert(context,alarmid,alarmIds);
        }
    }
}
