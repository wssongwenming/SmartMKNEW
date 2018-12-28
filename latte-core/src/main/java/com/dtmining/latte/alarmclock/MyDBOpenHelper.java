package com.dtmining.latte.alarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 */

public class MyDBOpenHelper extends SQLiteOpenHelper {
    // 数据表名，一个数据库中可以有多个表（虽然本例中只建立了一个表）
    public static final String TABLE_NAME = "AlarmTable";

    public final static String DB_NAME = "AlarmDB.db";
    public final static int VERSION = 2;
    private static MyDBOpenHelper instance = null;
    private SQLiteDatabase db;

    // 获取helper实例
    public static MyDBOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MyDBOpenHelper(context);
        }
        return instance;
    }

    //链接数据库
    private void openDatabase() {
        if (db == null) {
            db = getWritableDatabase();
        }
    }
    public MyDBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE " + TABLE_NAME + " (");
        sBuffer.append("_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("id INTEGER, ");
        sBuffer.append("starttime datetime NOT NULL,");
        sBuffer.append("music TEXT,");
        sBuffer.append("interval INTEGER NOT NULL,");
        sBuffer.append("state INTEGER,");
        sBuffer.append("message TEXT,");
        sBuffer.append("hour INTEGER NOT NULL,");
        sBuffer.append("minute INTEGER NOT NULL)"
        );
        Log.d("create", "onCreate1: ");
        // 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());
        Log.d("create", sBuffer.toString());
    }

    //软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade
        // onUpgrade方法的三个参数，一个 SQLiteDatabase对象，一个旧的版本号和一个新的版本号
        // 这样就可以把一个数据库从旧的模型转变到新的模型
        // 这个方法中主要完成更改数据库版本的操作
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        // 上述做法简单来说就是，通过检查常量值来决定如何，升级时删除旧表，然后调用onCreate来创建新表
        // 一般在实际项目中是不能这么做的，正确的做法是在更新数据表结构时，还要考虑用户存放于数据库中的数据不丢失
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 每次打开数据库之后首先被执行
    }
    public List<Alarm> query()
    {
        openDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();
        while (c.moveToNext()) {
            Alarm alarm = new Alarm();
            alarm.id = c.getInt(c.getColumnIndex("id"));
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


/*
    //删除用户数据
    public int deleteUser(String tel) {
        openDatabase();
        return db.delete("account", "tel=?", new String[] { tel });
    }

    public void deleteAllUser(){
        openDatabase();
        db.execSQL("delete from account");
    }
*/


    /*

     */
/** 查询所有数据 *//*

    public ArrayList<HashMap<String, Object>> getUserList() {
        openDatabase();
        Cursor cursor = db.query("account", null, null, null, null, null, null);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("tel", cursor.getInt(cursor.getColumnIndex("tel")));
            map.put("pwd", cursor.getString(cursor.getColumnIndex("pwd")));
            map.put("role", cursor.getString(cursor.getColumnIndex("role")));
            map.put("entry_way", cursor.getString(cursor.getColumnIndex("entry_way")));

            System.out.println(map);
            list.add(map);
        }
        return list;
    }
*/

    /*   */

    /**
     * 根据编码查询数据
     *//*
    public User1 getUser(String code) {
        openDatabase();
        Cursor cursor = db.query("account", null, "tel=?", new String[] { code }, null, null, null);
        if(cursor.getCount()==0){
            return null;
        }
        User1 user1;
        while (cursor.moveToNext()) {
            String tel=cursor.getString(cursor.getColumnIndex("tel"));
            String pwd=cursor.getString(cursor.getColumnIndex("pwd"));
            String role=cursor.getString(cursor.getColumnIndex("role"));
            String entry_way=cursor.getString(cursor.getColumnIndex("entry_way"));
            String third_id=cursor.getString(cursor.getColumnIndex("third_id"));
            user1 = new User1(tel,pwd,role,entry_way);
            return user1;
        }
        return null;
    }*/


/*    //动态修改数据库表
    public void change_db(){
        openDatabase();
        db.execSQL("drop table if exists account");
        db.execSQL("CREATE TABLE account(tel VARCHAR(20) PRIMARY KEY NOT NULL ,pwd VARCHAR(20) ,role VARCHAR(20),entry_way VARCHAR(10),third_id VARCHAR(50))");

    }*/
    public void add(Alarm alarm) {
        openDatabase();
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            db.execSQL("INSERT INTO " + TABLE_NAME
                    + " VALUES(null,null, ?, ?, ?, ?, ?, ?, ?)", new Object[]{alarm.starttime,
                    alarm.music, alarm.interval, alarm.state, alarm.message, alarm.hour, alarm.minute});
            // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
            // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
            // 使用占位符有效区分了这种情况
            db.execSQL("UPDATE " + TABLE_NAME + " set id=hour||minute||interval||strftime('%m',starttime)||strftime('%d',starttime)");
            //db.execSQL("UPDATE " + TABLE_NAME + " set id=_id");
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
        }
    }

    public void updateStartTime(Alarm alarm) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("starttime", String.valueOf(alarm.getStarttime()));
        db.update(TABLE_NAME, cv, "_id = ?",
                new String[]{String.valueOf(alarm.getId())});
    }

   public void updateStartTime(String starttime, int id) {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("starttime", String.valueOf(starttime));
        db.update(TABLE_NAME, cv, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public Alarm queryByMixedInfo(Date startTime,int interval,int hour,int minute){

        try{
            openDatabase();
            Cursor c = db.query(TABLE_NAME, null, "starttime=?,interval=?,hour=?,minute=?", new String[]{startTime.toString(),interval+"",hour+"",minute+""}, null, null, null);
            System.out.println("count   "+c.getCount());
            Alarm alarm = new Alarm();

            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex("_id"));

                alarm.id = c.getInt(c.getColumnIndex("id"));
                alarm.starttime = Date.valueOf(c.getString(c.getColumnIndex("starttime")));
                alarm.music = c.getString(c.getColumnIndex("music"));
                alarm.interval = c.getInt(c.getColumnIndex("interval"));
                alarm.state = c.getInt(c.getColumnIndex("state"));
                alarm.message = c.getString(c.getColumnIndex("message"));
                alarm.hour = c.getInt(c.getColumnIndex("hour"));
                alarm.minute = c.getInt(c.getColumnIndex("minute"));


            }
            c.close();
            return alarm;
        }
        catch (Exception e){
            return null;
        }

    }

    public Alarm queryById2(int ID) {
        try{
            openDatabase();
            Cursor c = db.query(TABLE_NAME, null, "id=?", new String[]{ID+""}, null, null, null);
            java.sql.Date STARTTIME=new java.sql.Date(System.currentTimeMillis());
            int HOUR=-1;
            int MINUTE=-1;
            boolean has=false;
            Alarm alarm = new Alarm();
            while (c.moveToNext()) {
                has=true;
                alarm.id = c.getInt(c.getColumnIndex("id"));
                alarm.starttime = Date.valueOf(c.getString(c.getColumnIndex("starttime")));
                STARTTIME= Date.valueOf(c.getString(c.getColumnIndex("starttime")));
                alarm.music = c.getString(c.getColumnIndex("music"));
                alarm.interval = c.getInt(c.getColumnIndex("interval"));
                alarm.state = c.getInt(c.getColumnIndex("state"));
                alarm.message = c.getString(c.getColumnIndex("message"));
                alarm.hour = c.getInt(c.getColumnIndex("hour"));
                HOUR=c.getInt(c.getColumnIndex("hour"));
                alarm.minute = c.getInt(c.getColumnIndex("minute"));
                MINUTE=c.getInt(c.getColumnIndex("minute"));
            }
            c.close();
 /*           if(has) {
                Cursor c1 = db.query(TABLE_NAME, null, "starttime=?,hour=?,minute=?", new String[]{STARTTIME.toString(), HOUR + "", MINUTE + ""}, null, null, null);
                while (c1.moveToNext()) {
                    alarm.addMessage(c.getString(c.getColumnIndex("message")));
                }
                c1.close();
            }*/

            return alarm;
        }
        catch (Exception e){
            return null;
        }
    }
    public Alarm queryById(int ID) {
        try{
            openDatabase();
            Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
            ArrayList<java.sql.Date> STARTTIMES=new ArrayList<>();
            ArrayList<Integer> INTERVALS=new ArrayList<>();
            ArrayList<Integer> HOURS=new ArrayList<>();
            ArrayList<Integer> MINUTES=new ArrayList<>();
            ArrayList<String>  MESSAGES=new ArrayList<>();
            java.sql.Date STARTTIME=new java.sql.Date(System.currentTimeMillis());
            int INTERVAL=-1;
            int HOUR=-1;
            int MINUTE=-1;
            Alarm alarm = new Alarm();
            while (c.moveToNext()) {
                int id=c.getInt(c.getColumnIndex("id"));
                java.sql.Date starttime=Date.valueOf(c.getString(c.getColumnIndex("starttime")));
                String music=c.getString(c.getColumnIndex("music"));
                int interval=c.getInt(c.getColumnIndex("interval"));
                int state=c.getInt(c.getColumnIndex("state"));
                String message=c.getString(c.getColumnIndex("message"));
                int hour=c.getInt(c.getColumnIndex("hour"));
                int minute= c.getInt(c.getColumnIndex("minute"));

                if(ID==id) {
                    alarm.id = id;
                    alarm.starttime = starttime;
                    alarm.music = music;
                    alarm.interval = interval;
                    alarm.state = state;
                    alarm.message = message;
                    alarm.hour =hour;
                    alarm.minute = minute;
                    INTERVAL=interval;
                    STARTTIME=starttime;
                    HOUR=hour;
                    MINUTE=minute;
                }else
                {
                    STARTTIMES.add(starttime);
                    HOURS.add(hour);
                    INTERVALS.add(interval);
                    MINUTES.add(minute);
                    MESSAGES.add(message);
                }
            }
            c.close();
            int size=STARTTIMES.size();
            Log.d("sizeof", size+"");
            for (int i = 0; i <size ; i++) {
                if(getRingTime(STARTTIMES.get(i),INTERVALS.get(i)).compareTo(getRingTime(STARTTIME,INTERVAL))==0){
                    if(HOURS.get(i)==HOUR){
                        if(MINUTES.get(i)==MINUTE)
                        {
                            alarm.addMessage(MESSAGES.get(i));

                            alarm.setMessage(alarm.getMessage().replace("][",","));
                            Log.d("mymessage", alarm.getMessage());
                            Log.d("mymessage1",MESSAGES.get(i));

                        }
                    }
                }
            }

            return alarm;
        }
        catch (Exception e){
            return null;
        }


    }




    public Cursor queryTheCursor() {
        openDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        Log.d("sql1", sql);
        return c;
    }

    /* //保存用户数据
     public long saveUser(User1 user) {
         openDatabase();
         ContentValues value = new ContentValues();
         value.put("tel", user.tel);
         value.put("role", user.role);
         value.put("pwd", user.pwd);
         value.put("entry_way", user.entry_way);
         return db.insert("account", null, value);
     }*/
    public Cursor queryTheCursorByGroup() {
        String sql = "SELECT starttime,hour,minute,group_concat(message) from  " + TABLE_NAME + " group by starttime,hour,minute";
        Cursor c = db.rawQuery("SELECT starttime,hour,minute,group_concat(message) from  " + TABLE_NAME + " group by starttime,hour,minute",
                null);
        Log.d("select", sql);
        return c;
    }

    public void deleteAlarm() {
        openDatabase();
        Log.d("delete", "delete......................");
        db.delete(TABLE_NAME, null, null);
        Log.d("delete", "deleteok......................");
    }
 /*   //保存用户数据
    public long saveUserByThirdId(User1 user) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("third_id", user.third_id);
        value.put("tel", user.tel);
        value.put("entry_way", user.entry_way);
        return db.insert("account", null, value);
    }*/


  /*  public User1 getOneUser() {

        try{
            openDatabase();
            Cursor cursor = db.query("account", null, null, null, null, null, null);
            System.out.println("count   "+cursor.getCount());

            User1 user1;
            while (cursor.moveToNext()) {
                String tel=cursor.getString(cursor.getColumnIndex("tel"));
                String pwd=cursor.getString(cursor.getColumnIndex("pwd"));
                String role=cursor.getString(cursor.getColumnIndex("role"));
                String third_id=cursor.getString(cursor.getColumnIndex("third_id"));
                System.out.println(tel+pwd+role);
                String entry_way=cursor.getString(cursor.getColumnIndex("entry_way"));
                user1 = new User1(tel,pwd,role,entry_way);
                user1.third_id=third_id;
                return user1;
            }
        }
       catch (Exception e){
           return null;
       }
       return null;


    }

    public String get_Tel(){
        openDatabase();
        Cursor cursor = db.query("account", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String tel=cursor.getString(cursor.getColumnIndex("tel"));
            return tel;
        }
        return null;
    }

    *//** 查询有多少条记录 *//*
    public int getLampCount() {
        openDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        return cursor.getCount();
    }*/

    protected Date getRingTime(Date holdDate,int interval) {
        Calendar calendar =new GregorianCalendar();
        calendar.setTime(holdDate);
        calendar.add(calendar.DATE, interval);
        // calendar的time转成java.util.Date格式日期
        java.util.Date utilDate = (java.util.Date)calendar.getTime();
        return new Date(utilDate.getTime());
    }


}