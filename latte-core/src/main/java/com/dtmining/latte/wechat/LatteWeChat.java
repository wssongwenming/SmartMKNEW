package com.dtmining.latte.wechat;

import android.app.Activity;


import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.wechat.callbacks.IWeChatGetOpenIdCallback;
import com.dtmining.latte.wechat.callbacks.IWeChatSignInCallback;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class LatteWeChat {
    //要接入微信，首先要在威信平台注册获得APP_ID和APP_SECRET
    static final String APP_ID= Latte.getConfiguration(ConfigKeys.WE_CHAT_APP_ID);
    static final String APP_SECRET=Latte.getConfiguration(ConfigKeys.WE_CHAT_APP_SECRET);
    //登陆和支付所需
    private final IWXAPI WXAPI;
    private IWeChatSignInCallback mSignInCallback=null;
    private IWeChatGetOpenIdCallback mOpenIdCallback=null;
    //LatteWeChat懒汉单例模式
    private static final class Holder{
        private static final LatteWeChat INSTANCE=new LatteWeChat();
    }

    public static LatteWeChat getInstancee(){
        return Holder.INSTANCE;
    }

    private LatteWeChat(){
        final Activity activity=Latte.getConfiguration(ConfigKeys.ACTIVITY);
        WXAPI= WXAPIFactory.createWXAPI(activity,APP_ID,false);
        WXAPI.registerApp(APP_ID);
    }

    //
    public final IWXAPI getWXAPI(){
        return WXAPI;
    }

    public LatteWeChat onSignSuccess(IWeChatSignInCallback callback){
        this.mSignInCallback=callback;
        return this;
    }

    public IWeChatSignInCallback getSignInCallback() {
        return mSignInCallback;
    }

    public IWeChatGetOpenIdCallback getmOpenIdCallback() {
        return mOpenIdCallback;
    }

    public LatteWeChat onGetOpenIdSuccess(IWeChatGetOpenIdCallback mOpenIdCallback) {
        this.mOpenIdCallback = mOpenIdCallback;
        return this;
    }

    //点击“微信登陆”按钮调用
    public final void signIn(){
        final SendAuth.Req req=new SendAuth.Req();
        //威信规定的字符串开发
        req.scope="snsapi_userinfo";
        req.state="random_state";
        //向微信共用开发平台发起请求，一旦有结果返回就会回调WXAPI目录下以WXEntryTemplate为模板生成的类
        WXAPI.sendReq(req);
    }
}
