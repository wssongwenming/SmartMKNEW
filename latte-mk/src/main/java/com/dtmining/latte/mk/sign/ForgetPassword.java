package com.dtmining.latte.mk.sign;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.handler.MyHandler;
import com.dtmining.latte.util.regex.RegexTool;
import com.dtmining.latte.util.sms.SMSObserver;
import com.dtmining.latte.util.timer.CountTimer;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * author:songwenming
 * Date:2018/10/15
 * Description:
 */
public class ForgetPassword extends LatteDelegate{
    @BindView(R2.id.edit_forget_password_phone)
    EditText mPhone=null;
    @BindView(R2.id.edit_forget_password_identifying_code)
    EditText mIdentifyingCode=null;
    @BindView(R2.id.btn_forget_password_send_identifying_code)
    Button mBtnSendIdentifyingCode;


    //发送验证码
    @OnClick(R2.id.btn_forget_password_send_identifying_code)
    void onClickSendIdentifyingCode()
    {
        if(!RegexTool.isMobileNO(mPhone.getText().toString())){
            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("警告")
                    .setContentText("您输入的手机号码不正确")
                    .show();

        }else {
            CountTimer countTimer = new CountTimer(60000, 1000, mBtnSendIdentifyingCode);
            countTimer.start();
            User user=new User();
            SignModel signModel =new SignModel();
            user.setTel(mPhone.getText().toString());
            signModel.setDetail(user);
            //获取验证码
            String sMSJson = JSON.toJSON(signModel).toString();
            RestClient.builder().url("http://10.0.2.2:8081/Web01_exec/SMSCode")
                    .raw(sMSJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {

                        }
                    })
                    .build()
                    .post();
        }
    }
    @OnClick(R2.id.btn_forget_password_confirm)
    void onClickConfirm(){
        if(checkForm()){
            User user=new User();
            SignModel signModel =new SignModel();
            user.setTel(mPhone.getText().toString());
            user.setIdentify_code(mIdentifyingCode.getText().toString());
            signModel.setDetail(user);
            String forgetPwdJson = JSON.toJSON(signModel).toString();
            RestClient.builder()
                    .url("")
                    .raw(forgetPwdJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            //携带tel跳到ResetPassword

                        }
                    })
                    .build()
                    .post();
        }
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_forget_password;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        //接受短信验证码
        MyHandler myHandler= Latte.getMyHandler();
        myHandler.setOnHandlerListener(new MyHandler.HandlerListener() {
                                           @Override
                                           public void handleMessage(Message msg) {
                                               System.out.println("MainActivity_msg==== " + msg.what);
                                               mIdentifyingCode.setText("123456");
                                           }
                                       }
        );
        SMSObserver smsObserver = new SMSObserver(getContext(), myHandler.mHandler);
        Uri uri = Uri.parse("content://sms");
        //第二个参数： 是否监听对应服务所有URI监听  例如sms 所有uri
        getActivity().getContentResolver().registerContentObserver(uri, true, smsObserver);
    }
    //表单验证
    private boolean checkForm(){
        final String phone=mPhone.getText().toString();
        final String identifyCode=mIdentifyingCode.getText().toString();
        boolean isPass=true;
        if(phone.isEmpty()||!RegexTool.isMobileNO(phone)){
            mPhone.setError("请填写正确的电话号码！");
            isPass=false;
        }else{
            mPhone.setError(null);
        }
        if(identifyCode.isEmpty()){
            mIdentifyingCode.setError("请输入验证码");
            isPass=false;
        }else {
            mIdentifyingCode.setError(null);
        }
        return isPass;
    }
}
