package com.dtmining.latte.mk.tools;

/**
 * Created by shikun on 18-5-15.
 */
public class Icon {
    public String url;
    public String name;
    private int iId;
    private String iName;


    public Icon() {
    }

    public Icon(int iId, String iName) {
        this.iId = iId;
        this.iName = iName;
    }

    public int getiId() {
        return iId;
    }

    public String getiName() {
        return iName;
    }

    public void setiId(int iId) {
        this.iId = iId;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }
}