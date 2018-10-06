package com.dtmining.smartmk.example.generators;


import com.dtmining.latte.annotations.AppRegisterGenerator;
import com.dtmining.latte.wechat.templates.AppRegisterTemplate;

@AppRegisterGenerator(
        packageName = "com.dtmining.smartmk.example",
        registerTemplete = AppRegisterTemplate.class
)
public interface AppRegister {
}
