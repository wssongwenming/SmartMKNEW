package com.dtmining.latte.mk.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.log.LatteLogger;
import com.dtmining.latte.wechat.LatteWeChat;
import com.dtmining.latte.wechat.callbacks.IWeChatSignInCallback;

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

    private ISignListener mISignListener=null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISignListener)
        {
            mISignListener=(ISignListener) activity;
        }
    }
    //登陆
    @OnClick(R2.id.btn_sign_in)
    void onClickSignIn(){
        if(checkForm()){
            RestClient.builder()
                    .url("http://39.105.97.128:8000/api/UserLogin")
                    .params("email","")
                    .params("password",mPassword.getText().toString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            LatteLogger.json("USER_PROFILE",response);
                            SignHandler.onSignIn(response,mISignListener);
                        }
                    })
                    .build()
                    .post();
            // Toast.makeText(this.getContext(),"验证通过",Toast.LENGTH_SHORT).show();
        }
    }
    //微信登陆
    @OnClick(R2.id.btn_wechat_sign_in)
    void onClickWeChat(){
        LatteWeChat.getInstancee().onSignSuccess(new IWeChatSignInCallback() {
            @Override
            public void onSignInSuccess(String userInfo) {
                Toast.makeText(getContext(),userInfo,Toast.LENGTH_LONG).show();
            }
        }).signIn();

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
        if(phone.isEmpty()||!Patterns.PHONE.matcher(phone).matches()){
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

    }
}
