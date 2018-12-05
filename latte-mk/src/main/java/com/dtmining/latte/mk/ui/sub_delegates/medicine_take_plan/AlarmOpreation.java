/*
 *  autor：OrandNot
 *  email：orandnot@qq.com
 *  time: 2016 - 1 - 14
 *
 */

package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.dtmining.latte.alarmclock.Alarm;
import com.dtmining.latte.alarmclock.DBManager;
import com.dtmining.latte.alarmclock.MyDBOpenHelper;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AlarmOpreation {

    public static void cancelAlert(Context context, int type) {
        AlarmManager mAlarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmsSetting.ALARM_ALERT_ACTION);
        intent.putExtra("type", type);
        intent.setClass(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, type, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(pi);

    }

    public static void enableAlert(final Context context, final int Id, final int[]ids){
        new Thread(new Runnable() {
	            @Override
                public void run() {
                    MyDBOpenHelper dbOpenHelper=MyDBOpenHelper.getInstance((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY));
                    Alarm alarm= dbOpenHelper.queryById(Id);
                    AlarmManager mAlarmManager = (AlarmManager)
                            context.getSystemService(Context.ALARM_SERVICE);
                    int type=Id;
                    Date date=alarm.getStarttime();
                    int hour=alarm.getHour();
                    int minute=alarm.getMinute();
                    int interval= alarm.getInterval();
                    String messsage=alarm.getMessage();
                    Log.d("date", "id:"+Id+"starttime:"+date+"hour:"+hour+"minute:"+minute+"diff:"+interval);
                    Calendar mCalendar = cacluteNextAlarm(date,hour, minute, interval);//选择了一周中的哪几天，比如周一、周三、周四,可以用daydiffer
                    /*if (mCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                        Toast.makeText((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY),"setAlarm FAIL:设置时间小于当前系统时间",Toast.LENGTH_LONG).show();
                        //    return;
                    }*/
                    Intent intent = new Intent(AlarmsSetting.ALARM_ALERT_ACTION);
                    intent.putExtra("nextalarm",mCalendar.getTimeInMillis());
                    intent.putExtra("type", Id);
                    intent.putExtra("id",Id);
                    intent.putExtra("message", messsage);
                    intent.putExtra("ids",ids);
                    intent.setClass(context, AlarmReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(context, type, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pi);


	            }}).start();

        }

    public static   Calendar cacluteNextAlarm(Date startTime,int hour,int minute,int differDays){//可以用differDay计算下一次铃声

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
        Log.e("cal1", " mCalendar" + mCalendar);
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
    }

    //获取下次显示是星期几
    private static int getNextDayOfWeek(int data, int cWeek,long timeInMillis) {
        int tempBack = data >> cWeek - 1;
        int tempFront = data ;

        if(tempBack%2==1){
            if(System.currentTimeMillis()<timeInMillis)  return cWeek;
        }
        tempBack = tempBack>>1;
        int m=1,n=0;
        while (tempBack != 0) {
            if (tempBack % 2 == 1 ) return cWeek + m;
            tempBack = tempBack / 2;
            m++;
        }
        while(n<cWeek){
            if (tempFront % 2 == 1)  return n+1;
            tempFront =tempFront/2;
            n++;
        }
        return 0;
    }
}
