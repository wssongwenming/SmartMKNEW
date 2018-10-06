package com.dtmining.smartmk.example.generators;


import com.dtmining.latte.annotations.PayEntryGenerator;
import com.dtmining.latte.wechat.templates.WXPayEntryTemplate;

@PayEntryGenerator(
        packageName = "com.dtmining.smartmk.example",
        payentryTemplete = WXPayEntryTemplate.class
)
public interface WeChatPayEntry {
}
