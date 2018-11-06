package com.dtmining.latte.mk.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.IError;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.regex.RegexTool;
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
            RestClient.builder()
                    .url("http://10.0.2.2:8081/Web01_exec/UserLogin")
                    //.url("http://192.168.1.3:8081/Web01_exec/UserLogin")
                    .raw(singInJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                           SignHandler.onSignIn(response,mISignListener);
                        }
                    })
                    .error(new IError() {
                        @Override
                        public void onError(int code, String msg) {
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

    }
}
