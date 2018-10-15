package com.dtmining.latte.mk.sign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.regex.RegexTool;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/10/15
 * Description:
 */
public class ResetPassword extends LatteDelegate {
    @BindView(R2.id.edit_reset_password_password)
    EditText mPassword=null;
    @BindView(R2.id.edit_reset_password_repassword)
    EditText mRepassword=null;
    @OnClick(R2.id.btn_reset_password_confirm)
    void onClickConfirm(){
        if(checkForm()){
            User user=new User();
            SignModel signModel =new SignModel();
            user.setPwd(mPassword.getText().toString());
            signModel.setDetail(user);
            String   resetPasswordJson= JSON.toJSON(signModel).toString();
            RestClient.builder()
                    .url("http://10.0.2.2:8081/Web01_exec/ResetPassword")
                    .raw(resetPasswordJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {

                        }
                    })
                    .build()
                    .post();

        }
    }

    //表单验证
    private boolean checkForm(){
        final String password=mPassword.getText().toString();
        final String rePassword=mRepassword.getText().toString();

        boolean isPass=true;

        if(password.isEmpty()||password.length()<6){
            mPassword.setError("请填写至少6位数密码");
            isPass=false;
        }else {
            mPassword.setError(null);
        }
        if(rePassword.isEmpty()||rePassword.length()<6||!(rePassword.equals(password)))
        {
            mRepassword.setError("密码验证错误");
            isPass=false;
        }else {
            mRepassword.setError(null);
        }
        return isPass;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_reset_password;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
