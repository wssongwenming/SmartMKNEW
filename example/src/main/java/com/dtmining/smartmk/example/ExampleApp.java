package com.dtmining.smartmk.example;

import android.app.Application;

import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.icon.FontMkModule;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class ExampleApp  extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Latte.init(this)
                .withIcon(new FontAwesomeModule())
                .withIcon(new FontMkModule())
                .withApiHost("http://127.0.0.1")
                .configure();
    }
}
