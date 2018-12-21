package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.alarm;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.alarmclock.Alarm;
import com.dtmining.latte.alarmclock.MyDBOpenHelper;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:songwenming
 * Date:2018/12/21
 * Description:主要完成在服药计划变化时，比如删除服药计划，删除药品，添加计划等造成服药计划变化时
 * 将变化的服药计划信息加入到sqllite库
 */
public class   AlarmHelper {
    private String tel;
    private String boxId;
    private Context context;
    private  MyDBOpenHelper myDBOpenHelper = MyDBOpenHelper.getInstance((Context) Latte.getConfiguration(ConfigKeys.ACTIVITY));;
    public AlarmHelper(Context context,String tel, String boxId) {
        this.tel=tel;
        this.boxId=boxId;
        this.context=context;
    }

    public void synPlanWithSqlite(){
        RestClient.builder()
                .url(UploadConfig.API_HOST + "/api/get_plan")//获取所有现有计划，成功后取得时间信息，设置闹钟
                .params("tel", tel)
                .params("boxId", LattePreference.getBoxId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            JSONObject object = JSON.parseObject(response);
                            int code = object.getIntValue("code");
                            if (code == 1) {
                                JSONObject jsonData = JSON.parseObject(response);
                                JSONObject detail = jsonData.getJSONObject("detail");
                                JSONArray planLsit = detail.getJSONArray("planlist");
                                HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> map_time = new HashMap<>();
                                int size = planLsit.size();
                                for (int i = 0; i < size; i++) {
                                    JSONObject planData = (JSONObject) planLsit.get(i);
                                    String time = planData.getString("time");
                                    JSONArray planArray = planData.getJSONArray("plans");
                                    HashMap<String, HashMap<String, ArrayList<String>>> map_interval = new HashMap<>();
                                    //map_interval.put("0", null);
                                    map_interval.put("1", null);
                                    map_interval.put("2", null);
                                    map_interval.put("3", null);
                                    map_interval.put("4", null);
                                    map_interval.put("5", null);
                                    map_interval.put("6", null);
                                    map_interval.put("7", null);
                                    int size2 = planArray.size();
                                    for (int j = 0; j < size2; j++) {
                                        JSONObject plan = (JSONObject) planArray.get(j);
                                        String interval = plan.getString("dayInterval");
                                        String starttime = plan.getString("startRemind");
                                        String endtime = plan.getString("endRemind");
                                        int medicineType=plan.getIntValue("medicineType");
                                        String unitfordose=null;
                                        switch (medicineType) {
                                            case 0:
                                                unitfordose = "片";
                                                break;
                                            case 1:
                                                unitfordose = "粒/颗";
                                                break;
                                            case 2:
                                                unitfordose = "瓶/支";
                                                break;
                                            case 3:
                                                unitfordose = "包";
                                                break;
                                            case 4:
                                                unitfordose = "克";
                                                break;
                                            case 5:
                                                unitfordose = "毫升";
                                                break;
                                            case 6:
                                                unitfordose = "其他";
                                                break;
                                        }
                                        String medicineUseCount = String.valueOf(plan.getInteger("medicineUseCount"));
                                        String medicineName = plan.getString("medicineName");
                                        //先以interval判断，如果
                                        if (map_interval.get(interval) == null) {
                                            HashMap<String, ArrayList<String>> map_start_time = new HashMap<>();
                                            ArrayList<String> infoArray = new ArrayList<>();
                                            infoArray.add(medicineName + ":服用" + medicineUseCount+unitfordose);
                                            map_start_time.put(starttime, infoArray);
                                            map_interval.put(interval, map_start_time);
                                        } else {
                                            boolean has = false;
                                            for (Map.Entry<String, HashMap<String, ArrayList<String>>> item_interval : map_interval.entrySet()) {
                                                if (map_interval != null) {
                                                    HashMap<String, ArrayList<String>> hashMap = item_interval.getValue();
                                                    if (hashMap != null) {
                                                        for (Map.Entry<String, ArrayList<String>> item_start_time : hashMap.entrySet()) {
                                                            String key_startTime = item_start_time.getKey();
                                                            if (key_startTime.equalsIgnoreCase(starttime)) {
                                                                has = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (has) {
                                                ((ArrayList<String>) map_interval.get(interval).get(starttime)).add(medicineName + ":服用" + medicineUseCount+unitfordose);
                                            } else {
                                                ArrayList<String> infoArray = new ArrayList<>();
                                                infoArray.add(medicineName + ":服用" + medicineUseCount+unitfordose);
                                                map_interval.get(interval).put(starttime, infoArray);
                                            }
                                        }
                                    }
                                    map_time.put(time, map_interval);
                                    Log.d("map_time", map_time.toString());
                                }
                                if (map_time != null) {
                                    //清空计划表
                                    myDBOpenHelper.deleteAlarm();
                                    for (Map.Entry<String, HashMap<String, HashMap<String, ArrayList<String>>>> item_time : map_time.entrySet()) {
                                        String time = null;
                                        if (item_time != null) {
                                            time = item_time.getKey();
                                            HashMap<String, HashMap<String, ArrayList<String>>> map_interval = item_time.getValue();
                                            for (Map.Entry<String, HashMap<String, ArrayList<String>>> item_interval : map_interval.entrySet()) {
                                                String Interval = null;
                                                if (item_interval != null) {
                                                    Interval = item_interval.getKey();
                                                    HashMap<String, ArrayList<String>> map_starttime = item_interval.getValue();
                                                    if (map_starttime != null) {
                                                        for (Map.Entry<String, ArrayList<String>> item_starttime : map_starttime.entrySet()) {
                                                            String starttime = null;
                                                            String message = null;
                                                            if (item_starttime != null) {
                                                                starttime = item_starttime.getKey();
                                                                message = item_starttime.getValue().toString();
                                                                int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                                                                int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.length()));
                                                                int interval = Integer.parseInt(Interval);
                                                                Alarm alarm = new Alarm(getDate(starttime, interval), hour, minute, interval, message, "aaa.mp3", 1);
                                                                try {
                                                                    add(alarm);
                                                                } catch (ParseException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                })
                .build()
                .get();
    }

    private Date getDate(String time, int interval) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        if (time != null && !time.equals("")) {
            java.util.Date date1 = null;
            try {
                date1 = sdf.parse(time);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date1);
                cal.add(Calendar.DATE, -interval);
                date2 =new java.sql.Date ((sdf.parse(sdf.format(cal.getTime()))).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date2;
    }
    public void add(Alarm alarm) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        d = format.parse("2012-12-12");
        java.sql.Date date = new java.sql.Date(d.getTime());
        myDBOpenHelper.add(alarm);
    }
    private void setalarm(Context context){
        synPlanWithSqlite();
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
            AlarmOpreation.enableAlert(context,alarmid,alarmIds);
        }

    }
}
