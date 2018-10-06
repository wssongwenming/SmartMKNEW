package com.dtmining.smartmk.example;

import android.app.Application;

import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.database.DatabaseManager;
import com.dtmining.latte.mk.icon.FontMkModule;
import com.dtmining.latte.net.interceptors.DebugInterceptor;
import com.facebook.stetho.Stetho;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class ExampleApp  extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Latte.init(this)
                .withIcon(new FontAwesomeModule())
                .withInterceptor(new DebugInterceptor("index",R.raw.sort_list))
                .withWeChatAppId("")//微信登陆初始化AppId
                .withWeChatAppSecret("")//微信登陆人传入Secret
                .withIcon(new FontMkModule())
                .withApiHost("http://127.5.5.1")
                .configure();
        //利用GreenDAO初始化数据库
        DatabaseManager.getInstance().init(this);
        //facebook的一个工具，可以把原生的数据映射到web上展现出来，
        initStetho();
    }
    private void initStetho(){
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
}
