package com.dtmining.MedicalKit.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import com.dtmining.latte.alarmclock.Alarm;
import com.dtmining.latte.alarmclock.MyDBOpenHelper;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.alarm.AlarmOpreation;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/12/21
 * Description:
 */
public class BootInfoReceiver extends BroadcastReceiver {
    private MyDBOpenHelper myDBOpenHelper;

    @Override
    public void onReceive(Context ctx, Intent arg1) {
        Intent intent=new Intent(ctx,ActivityForReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        // TODO Auto-generated method stub
        //setAlarmAccordSqlite(ctx);
        /*final Context context = ctx;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("run1", "run1: ");
                setAlarmAccordSqlite(context);
            }
        }).start();*/
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
