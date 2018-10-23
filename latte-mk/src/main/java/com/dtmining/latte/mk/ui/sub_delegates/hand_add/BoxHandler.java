package com.dtmining.latte.mk.ui.sub_delegates.hand_add;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.AccountManager;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.DatabaseManager;
import com.dtmining.latte.database.UserProfile;


/**
 * author:songwenming
 * Date:2018/10/23
 * Description:
 */
public class BoxHandler {

    IBoxChangeListener boxChangeListener=null;
    public static void getBoxes(String response,IBoxChangeListener boxChangeListener) {
        DatabaseManager.getInstance().getDao().deleteAll();
        final JSONObject profileJson = JSON.parseObject(response).getJSONObject("detail");
        final long tel = Long.parseLong(profileJson.getString("tel"));
        final String username = profileJson.getString("username");
        final String role = profileJson.getString("role");
        final String pwd = profileJson.getString("pwd");
        final String entry_way = profileJson.getString("entry_way");
        final UserProfile localUser = new UserProfile(tel, username, pwd, role, entry_way);

        //在内存中保留登陆数据
        Latte.getConfigurations().remove(ConfigKeys.LOCAL_USER);
        Latte.getConfigurations().put(ConfigKeys.LOCAL_USER, localUser);
        //数据库在APP开始时已经初始化，这时可以调用
        DatabaseManager.getInstance().getDao().insert(localUser);
        //已经注册并登陆成功了
        AccountManager.setSignState(true);
       //boxChangeListener.onGetBoxes("");
    }
}
