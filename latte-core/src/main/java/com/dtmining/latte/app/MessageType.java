package com.dtmining.latte.app;

/**
 * author:songwenming
 * Date:2018/10/14
 * Description:
 */
public enum MessageType {
    VERIFY_CODE(1);

    private int messageType;
    private MessageType(int msgType){
        this.messageType=msgType;
    }
    public int getMessageType(){
        return this.messageType;
    }
}
