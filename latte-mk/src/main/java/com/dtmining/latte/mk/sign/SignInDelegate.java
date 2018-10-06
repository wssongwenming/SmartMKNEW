package com.dtmining.latte.mk.sign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;

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
    //登陆
    @OnClick(R2.id.btn_sign_in)
    void onClickSignIn(){
        if(checkForm()){

        }
    }
    //微信登陆
    @OnClick(R2.id.btn_wechat_sign_in)
    void onClickWeChat(){

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
