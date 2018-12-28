package com.dtmining.latte.alarmclock;

import java.sql.Date;

/**
 * author:songwenming
 * Date:2018/11/25
 * Description:
 */

public class Alarm {
    public int id;
    public String ids;
    public Date starttime;
    public int hour;
    public int minute;
    public int interval;
    public String message;
    public String music;
    public int state;

    public Alarm() {

    }


    public Alarm(Date starttime, int hour, int minute, int interval, String message, String music, int state) {
        this.starttime = starttime;
        this.hour = hour;
        this.minute = minute;
        this.interval = interval;
        this.message = message;
        this.music = music;
        this.state = state;

    }

    public int getId() {
        return id;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getInterval() {
        return interval;
    }

    public String getMessage() {
        return message;
    }

    public String getMusic() {
        return music;
    }

    public int getState() {
        return state;
    }


    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getIds() {
        return ids;

    }

    public void addMessage(String message){
        this.message+=message;
    }
    public void setIds(String ids) {
        this.ids = ids;
    }
}
