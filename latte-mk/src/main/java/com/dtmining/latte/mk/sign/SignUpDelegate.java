package com.dtmining.latte.mk.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.IError;
import com.dtmining.latte.net.callback.IFailure;
import com.dtmining.latte.net.callback.ISuccess;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/9/26
 * Description:
 */
public class SignUpDelegate extends LatteDelegate {
    @BindView(R2.id.edit_sign_up_phone)
    EditText mPhone=null;
    @BindView(R2.id.edit_sign_up_identifying_code)
    EditText mIdentifyingCode=null;
    @BindView(R2.id.edit_sign_up_password)
    EditText mPassword=null;
    @BindView(R2.id.edit_sign_up_repassword)
    EditText mRepassword=null;
    @BindView(R2.id.edit_sign_up_user_role)
    Spinner mUserRole=null;

    private ISignListener mISignListener=null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISignListener){
            mISignListener=(ISignListener)activity;
        }
    }

    private String role=null;



    //选择用户类型
    @OnItemSelected(R2.id.edit_sign_up_user_role)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //Toast.makeText(this.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
        role=parent.getItemAtPosition(position).toString();

    }
    //单击注册按钮
    @OnClick(R2.id.btn_sign_up)
    void onClickSignUp(){

        if(checkForm()){
            User user=new User();
            SignModel signModel =new SignModel();
            user.setTel(mPhone.getText().toString());
            user.setIdentify_code(mIdentifyingCode.getText().toString());
            user.setPwd(mPassword.getText().toString());
            user.setEntry_way(EntryType.NORMAL.getEntryType());
            user.setRole(role);
            signModel.setDetail(user);
            String singUpJson = JSON.toJSON(signModel).toString();
            Log.d("singup", singUpJson);
            Toast.makeText(this.getContext(),singUpJson,Toast.LENGTH_SHORT).show();
            RestClient.builder().url("http://10.0.2.2:8081/Web01_exec/UserRegister")
                    .raw(singUpJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d("response", response);
                            SignHandler.onSignUp(response,mISignListener);

                        }
                    })
                    .error(new IError() {
                        @Override
                        public void onError(int code, String msg) {
                            mISignListener.onSignUpError(msg);
                        }
                    })
                    .failure(new IFailure() {
                        @Override
                        public void onFailure() {
                        }
                    })
                    .build()
                    .post();
            Toast.makeText(getContext(),"验证通过",Toast.LENGTH_LONG).show();
        }
    }
    // 已经注册可以登陆
    @OnClick(R2.id.tv_link_sign_in)
    void onClickLink(){
        start(new SignInDelegate());
    }
    //表单验证
    private boolean checkForm(){
        final String phone=mPhone.getText().toString();
        final String identifyCode=mIdentifyingCode.getText().toString();
        final String password=mPassword.getText().toString();
        final String rePassword=mRepassword.getText().toString();

        boolean isPass=true;
        if(phone.isEmpty()|| !Patterns.PHONE.matcher(phone).matches()){
            mPhone.setError("请输入电话号码");
            isPass=false;
        }else {
            mPhone.setError(null);
        }
        if(identifyCode.isEmpty()){
            mIdentifyingCode.setError("请输入验证码");
            isPass=false;
        }else {
            mIdentifyingCode.setError(null);
        }
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
        if(role.isEmpty()||role.equalsIgnoreCase("请选择用户类型"))
        {

            ((TextView) mUserRole.getChildAt(0)).setError("请选择用户类型");
             isPass=false;
        }else{
            ((TextView) mUserRole.getChildAt(0)).setError(null);
        }

        return isPass;
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_up;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
