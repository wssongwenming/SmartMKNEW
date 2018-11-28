/*
 *  autor：OrandNot
 *  email：orandnot@qq.com
 *  time: 2016 - 1 - 14
 *
 */

package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private AlarmsSetting alarmsSetting;
    @Override
    public void onReceive(Context context, Intent intent) {
        alarmsSetting = new AlarmsSetting(context);
        int type = intent.getIntExtra("type",0);
        int id=intent.getIntExtra("id",0);
        long nextalarm=intent.getLongExtra("nextalarm",0);
        String message=intent.getStringExtra("message");
        int[] ids=intent.getIntArrayExtra("ids");
        for (int i = 0; i <ids.length ; i++) {
            System.out.print("ids="+ids[i]+"");
        }
        Log.d("ids", ids+"");
        if(intent.getAction().equals(AlarmsSetting.ALARM_ALERT_ACTION)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日——HH时mm分ss秒SSS毫秒");
            Log.e("###########此次闹钟#######", "alarmsSetting.getNextAlarm()" + formatter.format(new Date(alarmsSetting.getNextAlarm())));
            Log.e("###########当前系统时间###", "System.currentTimeMillis()" + formatter.format(new Date(System.currentTimeMillis())));
            if (nextalarm + 1000 * 30 < System.currentTimeMillis()){//解决闹钟广播比设置时间闹钟快的问题
                Log.e("###########无效闹钟#######", "不执行");
                return;
            }
            Log.e("###########准备弹出提示框###", " ");
            intent.setClass(context, AlarmAlertActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            AlarmOpreation.cancelAlert(context,id);
            AlarmOpreation.enableAlert(context,id, ids);
        }else{
            for (int i = 0; i <ids.length ; i++) {
                int ID=ids[i];
                AlarmOpreation.cancelAlert(context, id);
                AlarmOpreation.enableAlert(context, id, ids);
            }

        }

    }
}
