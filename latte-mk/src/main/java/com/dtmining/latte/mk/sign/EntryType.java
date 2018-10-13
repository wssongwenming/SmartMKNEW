package com.dtmining.latte.mk.sign;

/**
 * author:songwenming
 * Date:2018/9/28
 * Description:
 */
public enum EntryType {
    NORMAL("1"),
    WECHAT("2"),
    QQ("3"),
    BLOG("4");
    private String entryType;
    private EntryType(String entryType){
        this.entryType=entryType;
    }
    public String getEntryType(){
        return this.entryType;
    }
}
