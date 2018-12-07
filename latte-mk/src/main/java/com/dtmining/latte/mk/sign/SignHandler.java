package com.dtmining.latte.mk.sign;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.AccountManager;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.DatabaseManager;
import com.dtmining.latte.database.UserProfile;

/**
 * author:songwenming
 * Date:2018/10/6
 * Description:
 */
public class SignHandler {
    public static void onSignIn(String response,ISignListener signListener){
        DatabaseManager.getInstance().getDao().deleteAll();
        final JSONObject profileJson= JSON.parseObject(response).getJSONObject("detail");
        final long tel=Long.parseLong(profileJson.getString("tel"));
        final String role=profileJson.getString("role");
        final UserProfile localUser=new UserProfile( tel, null, null, role,null);
        //在内存中保留登陆数据
        Latte.getConfigurations().remove(ConfigKeys.LOCAL_USER);
        Latte.getConfigurations().put(ConfigKeys.LOCAL_USER,localUser);
        //数据库在APP开始时已经初始化，这时可以调用
        DatabaseManager.getInstance().getDao().insert(localUser);
        //已经注册并登陆成功了
        AccountManager.setSignState(true);
        signListener.onSignInSuccess();
    }
    public static void onSignUp(String response,ISignListener signListener,String tel,String role){
        DatabaseManager.getInstance().getDao().deleteAll();
         final long TEL= Long.parseLong(tel);
        final String ROLE=role;
         final UserProfile localUser=new UserProfile( TEL, null, null, ROLE,null);
        Log.d("signup", "onSignUp: ");
        //在内存中保留登陆数据
        Latte.getConfigurations().remove(ConfigKeys.LOCAL_USER);
        Latte.getConfigurations().put(ConfigKeys.LOCAL_USER,localUser);
        //数据库在APP开始时已经初始化，这时可以调用
        DatabaseManager.getInstance().getDao().insert(localUser);
        //已经注册并登陆成功了
        AccountManager.setSignState(true);
        signListener.onSignUpSuccess();
    }
}
