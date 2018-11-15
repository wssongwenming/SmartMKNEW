package com.dtmining.MedicalKit.myapplication.generators;


import com.dtmining.latte.annotations.EntryGenerator;
import com.dtmining.latte.wechat.templates.WXEntryTemplate;

@EntryGenerator(
        packageName = "com.dtmining.MedicalKit.myapplication",
        entryTemplete = WXEntryTemplate.class
)
public interface WeChatEntry {
}
