package com.dtmining.latte.mk.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxByScanDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.regex.RegexTool;
import com.dtmining.latte.util.timer.CountTimer;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * author:songwenming
 * Date:2018/12/5
 * Description:
 */
public class BindWeixinForRegistedUserDelegate extends LatteDelegate {
    private static final String OPEN_ID = "OPEN_ID";
    private String openId ="";
    @BindView(R2.id.edit_bind_weixin_phone)
    AppCompatEditText mPhone=null;
    @BindView(R2.id.edit_bind_weixin_identifying_code)
    AppCompatEditText mIdentifyingCode=null;
    @BindView(R2.id.btn_bind_weixin_send_identifying_code)
    Button mBtnSendIdentifyingCode;
    private ISignListener mISignListener=null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISignListener)
        {
            mISignListener=(ISignListener) activity;
        }
    }
    @OnClick(R2.id.btn_bind_weixin_confirm)
    void onBindWeixin(){
        if(checkForm()){
            String tel=mPhone.getText().toString();
            String identifyingCode=mIdentifyingCode.getText().toString();
            JsonObject detail=new JsonObject();
            detail.addProperty("tel",tel);
            detail.addProperty("identify_code",identifyingCode);
            detail.addProperty("weixinOpenid",openId);
            JsonObject submitJson=new JsonObject();
            submitJson.add("detail",detail);
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/bindOpenid")
                    .clearParams()
                    .raw(submitJson.toString())
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
                                    start(new AddWeixinForUnregistedUserDelegate());
                                    break;
                              }
                        }
                    })
                    .build()
                    .post();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            openId = args.getString(OPEN_ID);
        }
    }
    public static BindWeixinForRegistedUserDelegate newInstance(String openId){
        final Bundle args = new Bundle();
        args.putString(OPEN_ID,openId);
        final BindWeixinForRegistedUserDelegate delegate = new BindWeixinForRegistedUserDelegate();
        delegate.setArguments(args);
        return delegate;
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_bind_weixin_for_registed_user;
    }
    //发送验证码
    @OnClick(R2.id.btn_bind_weixin_send_identifying_code)
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
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/SMSCode")
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
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

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
        if(identifyCode.isEmpty()||identifyCode.length()==0){
            mIdentifyingCode.setError("请输入验证码");
            isPass=false;
        }else{
            mIdentifyingCode.setError(null);
        }
        return isPass;
    }
}
