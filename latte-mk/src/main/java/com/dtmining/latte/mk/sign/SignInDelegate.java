package com.dtmining.latte.mk.sign;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.IError;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.regex.RegexTool;
import com.dtmining.latte.wechat.LatteWeChat;
import com.dtmining.latte.wechat.callbacks.IWeChatGetOpenIdCallback;
import com.dtmining.latte.wechat.callbacks.IWeChatSignInCallback;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * author:songwenming
 * Date:2018/9/28
 * Description:
 */
public class SignInDelegate extends LatteDelegate {

    @BindView(R2.id.edit_sign_in_phone)
    EditText mPhone=null;
    @BindView(R2.id.edit_sign_in_password)
    EditText mPassword=null;
    private Tencent mTencent;
    private String QQ_uid;//qq_openid
    private UserInfo userInfo;
    private ISignListener mISignListener=null;
    private BaseUiListener listener = new BaseUiListener();
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISignListener)
        {
            mISignListener=(ISignListener) activity;
        }
    }
    //忘记密码
    @OnClick(R2.id.tv_link_forget_password)
    void onClickForgetPWD(){
        start(new ForgetPassword());
    }
    //登陆
    @OnClick(R2.id.btn_sign_in)
    void onClickSignIn(){
        if(checkForm()){
            User user=new User();
            SignModel signModel =new SignModel();
            user.setTel(mPhone.getText().toString());
            user.setPwd(mPassword.getText().toString());
            user.setEntry_way(EntryType.NORMAL.getEntryType());
            signModel.setDetail(user);
            String singInJson = JSON.toJSON(signModel).toString();
            System.out.print(singInJson);
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/UserLogin")
                    .clearParams()
                    //.url("http://10.0.2.2:8081/Web01_exec/UserLogin")
                    .raw(singInJson)

                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            com.alibaba.fastjson.JSONObject object=JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            switch (code){
                                case 1:
                                    SignHandler.onSignIn(response,mISignListener);
                                    break;
                                case 3:
                                    Toast.makeText(getContext(),"用户不存在",Toast.LENGTH_LONG).show();
                                    startWithPop(new SignUpDelegate());
                                    break;
                                case 4:
                                    Toast.makeText(getContext(),"密码错误",Toast.LENGTH_LONG).show();
                                    break;
                            }

                        }
                    })
                    .error(new IError() {
                        @Override
                        public void onError(int code, String msg) {
                            Log.d("code", "code: "+code);
                            mISignListener.onSignInError(msg);
                        }
                    })
                    .build()
                    .post();
            // Toast.makeText(this.getContext(),"验证通过",Toast.LENGTH_SHORT).show();
        }
    }
    //微信登陆
    @OnClick(R2.id.icon_sign_in_wechat)
    void onClickWeChat(){

        LatteWeChat.getInstancee()
                .onSignSuccess(new IWeChatSignInCallback() {
                    @Override
                    public void onSignInSuccess(String userInfo) {
                       // Toast.makeText(getContext(),userInfo,Toast.LENGTH_LONG).show();
                        }
                        })
                .onGetOpenIdSuccess(new IWeChatGetOpenIdCallback() {
                    @Override
                    public void onGetOpenIdSuccess(String openId) {
                        Toast.makeText(getContext(),openId,Toast.LENGTH_LONG).show();
                    }
                })
                .signIn();

    }
    //QQ登陆
    @OnClick(R2.id.icon_sign_in_qq)
    void onClickQQ(){
        if (!mTencent.isSessionValid())
        {
            //注销登录 mTencent.logout(this);
            mTencent.login((Activity) Latte.getConfiguration(ConfigKeys.ACTIVITY), "all", listener);
        }
    }
    //尚未注册
    @OnClick(R2.id.tv_link_sign_up)
    void onClickLink(){
        start(new SignUpDelegate());
    }
    //表单验证
    private boolean checkForm(){
        final String phone=mPhone.getText().toString();
        final String password=mPassword.getText().toString();
        boolean isPass=true;
        if(phone.isEmpty()||!RegexTool.isMobileNO(phone)){
            mPhone.setError("请填写正确的电话号码！");
            isPass=false;
        }else{
            mPhone.setError(null);
        }
        if(password.isEmpty()||password.length()<6){
            mPassword.setError("请填写至少6位数字密码");
            isPass=false;
        }else{
            mPassword.setError(null);
        }
        return isPass;
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_in;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        String qq_app_id=Latte.getConfiguration(ConfigKeys.QQ_APP_ID).toString();
        Context applicationContext=(Context)Latte.getConfigurations().get(ConfigKeys.APPLICATION_CONTEXT);
        mTencent = Tencent.createInstance(qq_app_id,applicationContext);
     }
    class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Log.d("授权:",o.toString());
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject(o.toString());
                initOpenidAndToken(jsonObject);
                updateUserInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            Log.d("error","onError:code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel() {
            Log.d("onCancel","cancel");
        }
    }
    /**
     * 获取登录QQ腾讯平台的权限信息(用于访问QQ用户信息)
     * @param jsonObject
     */
    public void initOpenidAndToken(org.json.JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                QQ_uid = openId;
            }
        } catch(Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                }
                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    Log.d("response","................"+response.toString());
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
                @Override
                public void onCancel() {
                    Log.d("cancel","登录取消..");
                }
            };
            userInfo = new UserInfo((Activity) Latte.getConfiguration(ConfigKeys.ACTIVITY), mTencent.getQQToken());
            userInfo.getUserInfo(listener);
        }
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {}
    };
}
