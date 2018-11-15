package com.dtmining.MedicalKit.myapplication.generators;


import com.dtmining.latte.annotations.AppRegisterGenerator;
import com.dtmining.latte.wechat.templates.AppRegisterTemplate;

@AppRegisterGenerator(
        packageName = "com.dtmining.MedicalKit.myapplication",
        registerTemplete = AppRegisterTemplate.class
)
public interface AppRegister {
}
