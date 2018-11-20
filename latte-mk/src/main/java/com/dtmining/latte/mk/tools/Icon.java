package com.dtmining.latte.mk.tools;

/**
 * Created by shikun on 18-5-15.
 */
public class Icon {
    public String url;
    public String name;
    public String boxId;
    private int iId;
    private String iName;

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

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