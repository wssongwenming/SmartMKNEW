package com.dtmining.latte.alarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * author:songwenming
 * Date:2018/11/25
 * Description:
 */
public class DBManager {
    private DatabaseHelper1 helper;
    private SQLiteDatabase db;
    public DBManager(Context context)
    {

        helper = new DatabaseHelper1(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void add(List<Alarm> alarms)
    {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (Alarm alarm : alarms)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper1.TABLE_NAME
                        + " VALUES(null, null,?, ?, ?, ?, ?, ?, ?)", new Object[] { alarm.starttime,
                        alarm.music, alarm.interval,alarm.state,alarm.message,alarm.hour,alarm.minute });
                db.execSQL("UPDATE "+ DatabaseHelper1.TABLE_NAME+" set id=_id");
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
                Log.d("add", "add: ");
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }

    public void add(Alarm alarm)
    {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            db.execSQL("INSERT INTO " + DatabaseHelper1.TABLE_NAME
                    + " VALUES(null,null, ?, ?, ?, ?, ?, ?, ?)", new Object[] { alarm.starttime,
                    alarm.music, alarm.interval,alarm.state,alarm.message,alarm.hour,alarm.minute });
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            db.execSQL("UPDATE "+ DatabaseHelper1.TABLE_NAME+" set id=_id");
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    public void updateStartTime(Alarm alarm)
    {
        ContentValues cv = new ContentValues();
        cv.put("starttime", String.valueOf(alarm.getStarttime()));
        db.update(DatabaseHelper1.TABLE_NAME, cv, "_id = ?",
                new String[] {String.valueOf(alarm.getId())});
    }

    public void updateStartTime(String starttime,String id)
    {
        ContentValues cv = new ContentValues();
        cv.put("starttime", String.valueOf(starttime));
        db.update(DatabaseHelper1.TABLE_NAME, cv, "_id = ?",
                new String[] {id});
    }
    public void deleteAlarm(){
        db.delete(DatabaseHelper1.TABLE_NAME,null,null);
    }
    public Alarm queryById(int ID){
        Cursor c = queryTheCursor();
        Alarm alarm=new Alarm();
        while (c.moveToNext())
        {
            int id= c.getInt(c.getColumnIndex("_id"));
            if(id==ID){
                alarm._id = c.getInt(c.getColumnIndex("_id"));
                alarm.starttime= Date.valueOf(c.getString(c.getColumnIndex("starttime")));
                alarm.music=c.getString(c.getColumnIndex("music"));
                alarm.interval=c.getInt(c.getColumnIndex("interval"));
                alarm.state=c.getInt(c.getColumnIndex("state"));
                alarm.message=c.getString(c.getColumnIndex("message"));
                alarm.hour=c.getInt(c.getColumnIndex("hour"));
                alarm.minute=c.getInt(c.getColumnIndex("minute"));
            }
            break;
        }
        c.close();
        return alarm;
    }
    public List<Alarm> query()
    {
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();
        Cursor c = queryTheCursor();
        while (c.moveToNext())
        {
            Alarm alarm = new Alarm();
            alarm._id = c.getInt(c.getColumnIndex("_id"));
            alarm.starttime= Date.valueOf(c.getString(c.getColumnIndex("starttime")));
            alarm.music=c.getString(c.getColumnIndex("music"));
            alarm.interval=c.getInt(c.getColumnIndex("interval"));
            alarm.state=c.getInt(c.getColumnIndex("state"));
            alarm.message=c.getString(c.getColumnIndex("message"));
            alarm.hour=c.getInt(c.getColumnIndex("hour"));
            alarm.minute=c.getInt(c.getColumnIndex("minute"));
            alarms.add(alarm);
        }
        c.close();
        return alarms;
    }
    public List<Alarm> queryByGroup()
    {
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();
        Cursor c = queryTheCursorByGroup();
        while (c.moveToNext())
        {
            Alarm alarm = new Alarm();
            //alarm._id = c.getInt(c.getColumnIndex("_id"));
            alarm.starttime= Date.valueOf(c.getString(c.getColumnIndex("starttime")));
            //alarm.music=c.getString(c.getColumnIndex("music"));
            //alarm.interval=c.getLong(c.getColumnIndex("interval"));
            //alarm.state=c.getInt(c.getColumnIndex("state"));

            alarm.message=c.getString(c.getColumnIndex("group_concat(message)"));
            alarm.hour=c.getInt(c.getColumnIndex("hour"));
            alarm.minute=c.getInt(c.getColumnIndex("minute"));
            alarms.add(alarm);
        }
        c.close();
        return alarms;
    }

    public Cursor queryTheCursor()
    {
        String sql="SELECT * FROM " + DatabaseHelper1.TABLE_NAME;
        Cursor c = db.rawQuery(sql,null);
        Log.d("sql1", sql);
        return c;
    }
    public Cursor queryTheCursorByGroup()
    {
        String sql="SELECT starttime,hour,minute,group_concat(message) from  " + DatabaseHelper1.TABLE_NAME +" group by starttime,hour,minute";
        Cursor c = db.rawQuery("SELECT starttime,hour,minute,group_concat(message) from  " + DatabaseHelper1.TABLE_NAME +" group by starttime,hour,minute",
                null);
        Log.d("select", sql);
        return c;
    }

    public void closeDB()
    {
        // 释放数据库资源
        db.close();
    }
}