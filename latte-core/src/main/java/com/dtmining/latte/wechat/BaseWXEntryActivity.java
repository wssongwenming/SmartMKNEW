package com.dtmining.latte.wechat;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.IError;
import com.dtmining.latte.net.callback.IFailure;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.log.LatteLogger;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;


public abstract class BaseWXEntryActivity extends BaseWXActivity {
    //用户登录成功后的回调
    protected abstract void onSignInSuccess(String userInfo);

    protected abstract void onGetOpenIdSuccess(String openId);

    // 微信发送请求到第三方应用后的回调
    @Override
    public void onReq(BaseReq baseReq) {

    }
    // 第三方应用发送请求到微信后的回调
    @Override
    public void onResp(BaseResp baseResp) {
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                final String code = ((SendAuth.Resp) baseResp).code;
                // Log.d("base", ((SendAuth.Resp)baseResp).code);
                //拿到ｃｏｄｅ后就可以进行第一次请求
                final StringBuilder authUrl = new StringBuilder();
                authUrl
                        .append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
                        .append(LatteWeChat.APP_ID)
                        .append("&secret=")
                        .append(LatteWeChat.APP_SECRET)
                        .append("&code=")
                        .append(code)
                        .append("&grant_type=authorization_code");
                LatteLogger.d("authUrl", authUrl.toString());
                getAuth(authUrl.toString());
            break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Log.d("message","发送取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Log.d("message", "发送被拒绝");
                finish();
                break;
            case BaseResp.ErrCode.ERR_BAN:
                result = "签名错误";
                Log.d("message", "签名错误");

                break;
            default:
                result = "发送返回";
//                showMsg(0,result);
                Log.d("message", "发送返回");
                finish();
                break;
        }
    }
    private void getAuth(String authUrl){
        RestClient
        .builder()
        .url(authUrl)
        .success(new ISuccess() {
            @Override
            public void onSuccess(String response) {
                final JSONObject authObj= JSON.parseObject(response);
                final String accessToken=authObj.getString("access_token");
                final String openId=authObj.getString("openid");
                final StringBuilder userInfoUrl=new StringBuilder();
                userInfoUrl
                        .append("https://api.weixin.qq.com/sns/userinfo?access_token")
                        .append(accessToken)
                        .append("&openid")
                        .append(openId)
                        .append("&lang=")
                        .append("zh_CN");
                onGetOpenIdSuccess(openId);
                LatteLogger.d("userInfoUrl",userInfoUrl.toString());
                getUserInfo(userInfoUrl.toString());

            }
        })
        .build()
        .get();
    }
    private void getUserInfo(String userInfoUrl){
        RestClient
                .builder()
                .url(userInfoUrl)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        onSignInSuccess(response);
                    //这里表示已经微信登陆成功
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {

                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {

                    }
                })
                .build()
                .get();
    }
}
