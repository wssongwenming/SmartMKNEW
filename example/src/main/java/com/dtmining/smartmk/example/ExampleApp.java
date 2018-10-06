package com.dtmining.smartmk.example;

import android.app.Application;

import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.database.DatabaseManager;
import com.dtmining.latte.mk.icon.FontMkModule;
import com.dtmining.latte.net.interceptors.DebugInterceptor;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class ExampleApp  extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Latte.init(this)
                .withIcon(new FontAwesomeModule())
                .withInterceptor(new DebugInterceptor("index",R.raw.sort_list))
                .withIcon(new FontMkModule())
                .withApiHost("http://127.5.5.1")
                .configure();
        DatabaseManager.getInstance().init(this);
    }
}
