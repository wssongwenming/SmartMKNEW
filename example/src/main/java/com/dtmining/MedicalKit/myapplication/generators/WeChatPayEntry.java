package com.dtmining.MedicalKit.myapplication.generators;


import com.dtmining.latte.annotations.PayEntryGenerator;
import com.dtmining.latte.wechat.templates.WXPayEntryTemplate;

@PayEntryGenerator(
        packageName = "com.dtmining.MedicalKit.myapplication",
        payentryTemplete = WXPayEntryTemplate.class
)
public interface WeChatPayEntry {
}
