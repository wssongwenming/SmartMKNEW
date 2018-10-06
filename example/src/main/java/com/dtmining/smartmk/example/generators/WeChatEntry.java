package com.dtmining.smartmk.example.generators;


import com.dtmining.latte.annotations.EntryGenerator;
import com.dtmining.latte.wechat.templates.WXEntryTemplate;

@EntryGenerator(
        packageName = "com.dtmining.smartmk.example",
        entryTemplete = WXEntryTemplate.class
)
public interface WeChatEntry {
}
