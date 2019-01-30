package com.dtmining.MedicalKit.myapplication;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.dtmining.latte.app.AccountManager;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.DatabaseManager;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.mk.icon.FontMkModule;
import com.dtmining.latte.net.interceptors.DebugInterceptor;

import com.facebook.stetho.Stetho;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.List;

public class ExampleApp  extends Application{

    private  UserProfile mLocalUser=null;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("appthread", Thread.currentThread().getId()+"");
        Latte.init(this)
                .withIcon(new FontAwesomeModule())
              ///.withInterceptor(new DebugInterceptor("index", R.raw.history))
                //.withInterceptor(new DebugInterceptor("medicine_mine",R.raw.medicine_mine))
                .withInterceptor(new DebugInterceptor("medicine_plan",R.raw.medicine_plan))
              //  .withInterceptor(new DebugInterceptor("medicine_boxes",R.raw.medicine_boxes))
                //.withInterceptor(new DebugInterceptor("recentmedicine",R.raw.recentmedicine))
                //.withInterceptor(new DebugInterceptor("medicine_overdue",R.raw.medicineoverdue))
                //.withInterceptor(new DebugInterceptor("body_situation",R.raw.body_situation))
               // .withInterceptor(new DebugInterceptor("medicine_summary",R.raw.summary))
                //.withInterceptor(new DebugInterceptor("usemessage",R.raw.usemessage))
                //.withInterceptor(new DebugInterceptor("overdue",R.raw.overdue))
               // .withInterceptor(new DebugInterceptor("supply",R.raw.supply))
                .withWeChatAppId("wxe42b49bb3dff09ab")//微信登陆初始化AppId
                .withWeChatAppSecret("da557a780af2ef52411d1205519cd3ce")//微信登陆人传入Secret
                .withQQAppID("1108067090")
                .withScanFor("medicine")
                .withIcon(new FontMkModule())
                .withApiHost("http://127.5.5.1")
                .configure();
        //利用GreenDAO初始化数据库
        DatabaseManager.getInstance().init(this);
        //facebook的一个工具，可以把原生的数据映射到web上展现出来，
        initStetho();
        initLoginState();
    }
    private void initStetho(){
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
    private void initLoginState(){
         this.mLocalUser = getModel();
         //Toast.makeText(getApplicationContext(),"qidong="+mLocalUser,Toast.LENGTH_LONG).show();
         if(mLocalUser!=null) {
             Latte.getConfigurations().put(ConfigKeys.LOCAL_USER, mLocalUser);
             //Toast.makeText(getApplicationContext(),"qidong111111111111",Toast.LENGTH_LONG).show();
             AccountManager.setSignState(true);
         }else{
             AccountManager.setSignState(false);
         }
        //暂时不做refreshLocalUser();

    }
    public UserProfile getModel()
    {
        List<UserProfile> listModel = DatabaseManager.getInstance().getDao().loadAll();;
        if (listModel != null && listModel.size() == 1)
        {
            UserProfile model = listModel.get(0);
            return model;
        } else
        {
            return null;
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

    }
}
