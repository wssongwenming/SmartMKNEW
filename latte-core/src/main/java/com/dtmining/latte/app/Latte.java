package com.dtmining.latte.app;

import android.content.Context;
import android.support.v7.widget.ContentFrameLayout;

import java.util.HashMap;

/**
 * author:songwenming
 * Date:2018/9/20 0020
 * Description:
 * 对外的工具类，大多是一些静态方法
 */
public final class Latte {
    //初始化配置入口函数
    public static Configurator init(Context context){
        getConfigurations().put(ConfigKeys.APPLICATION_CONTEXT,context.getApplicationContext());
        return Configurator.getInstance();
    }

    private static HashMap<Object,Object> getConfigurations(){
        return Configurator.getInstance().getLatteConfigs();
    }
    public static Context getApplication(){
        return (Context) getConfigurations().get(ConfigKeys.APPLICATION_CONTEXT);
    }
}
