package com.dtmining.latte.mk.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dtmining.latte.alarmclock.Alarm;
import com.dtmining.latte.alarmclock.DBManager;
import com.dtmining.latte.alarmclock.DatabaseHelper;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/25
 * Description:
 */
public class TestDelegate extends LatteDelegate {
    private DBManager dbManager;

    @OnClick(R2.id.test_add)
    void addAlarm() {
        try {
            add();
        } catch (ParseException e) {
        e.printStackTrace();
    }
    }

    @OnClick(R2.id.test_update)
    void updateAlarm() {
        try {
            update();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @OnClick(R2.id.test_query)
    void queryAlarm() {
        query();
    }
    @OnClick(R2.id.test_delete)
    void deleteAlarm() {
        deleteOldAlarm();
    }

    @BindView(R2.id.listView)
    ListView listView=null;
    @Override
    public Object setLayout() {
        return R.layout.test_delegate;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        super.onCreate(savedInstanceState);
        // 初始化DBManager
        dbManager = new DBManager(this.getContext());
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        dbManager.closeDB();// 释放数据库资源
    }
    public void add() throws ParseException {
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        d = format.parse("2012-12-12");
        java.sql.Date date = new java.sql.Date(d.getTime());
        Alarm alarm = new Alarm(date, 12, 23,1231243,"吃药了","aaa.mp3",1);
        alarms.add(alarm);
        alarms.add(alarm);
        Log.d("before", "addbefore: ");
        dbManager.add(alarms);
    }
    public void update () throws ParseException
    {
        // 把Jane的年龄改为30（注意更改的是数据库中的值，要查询才能刷新ListView中显示的结果）

        ArrayList<Alarm> alarms = new ArrayList<Alarm>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        Alarm alarm = new Alarm();
        alarm.setId(1);
        d = format.parse("2012-12-11");
         alarm.setStarttime(new java.sql.Date(d.getTime()));
        dbManager.updateStartTime(alarm);
    }

    public void query()
    {
        List<Alarm> alarms = dbManager.query();
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Alarm alarm : alarms)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(alarm.getId()));
            map.put("starttime", alarm.starttime.toString());
            map.put("info", alarm.minute+"");
            list.add(map);
        }
        Log.d("query", list.toString());
    }
    public void deleteOldAlarm()
    {

        dbManager.deleteAlarm();
    }
}
