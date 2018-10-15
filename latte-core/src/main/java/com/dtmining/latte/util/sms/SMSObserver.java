package com.dtmining.latte.util.sms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;


import com.dtmining.latte.app.MessageType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author:songwenming
 * Date:2018/10/14
 * Description:
 */
public class SMSObserver extends ContentObserver {

    private Context mContext;
    private Handler mHandler;

    public SMSObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        String code = "";
        // 第一遍 先执行content://sms/raw 第二遍则 uri.toString :content://sms/inbox
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }

        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor c = mContext.getContentResolver().query(inboxUri, null, null,
                null, "date desc"); //按日期【排序  最后条记录应该在最上面  desc 从大到小    asc小到大
        if (c != null) {
            if (c.moveToFirst()) {
                String address = c.getString(c.getColumnIndex("address"));
                String body = c.getString(c.getColumnIndex("body"));
                 if (!address.equals("15555215554")) {
                 return; //判断是否是 来自 再提取验证码
                 }
                Pattern pattern = Pattern.compile("(\\d{6})");
                Matcher matcher = pattern.matcher(body);

                if (matcher.find()) {
                    code = matcher.group();
                    mHandler.obtainMessage(MessageType.VERIFY_CODE.getMessageType(), code)
                            .sendToTarget();
                }

            }
            c.close();
        }

    }
}
