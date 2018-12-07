package com.dtmining.latte.mk.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.regex.RegexTool;
import com.dtmining.latte.util.timer.CountTimer;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * author:songwenming
 * Date:2018/12/5
 * Description:
 */
public class AddWeixinForUnregistedUserDelegate extends LatteDelegate{
   private static final String OPEN_ID = "OPEN_ID";
       private String openId ="";
    private String role=null;
     @BindView(R2.id.edit_add_weixin_phone)
     AppCompatEditText mPhone=null;
     @BindView(R2.id.edit_add_weixin_identifying_code)
     AppCompatEditText mIdentifyingCode=null;
     @BindView(R2.id.sp_add_weixin_user_role)
     Spinner mUserRole=null;
     @BindView(R2.id.btn_add_weixin_send_identifying_code)
     Button mBtnSendIdentifyingCode;
     @OnClick(R2.id.btn_add_weixin_confirm)
     void onAddWeixin(){
         if(checkForm()){
             String tel=mPhone.getText().toString();
             String identifyingCode=mIdentifyingCode.getText().toString();
             JsonObject detail=new JsonObject();
             detail.addProperty("tel",tel);
             detail.addProperty("identify_code",identifyingCode);
             detail.addProperty("role",role);
             detail.addProperty("weixinOpenid",openId);
             JsonObject submitJson=new JsonObject();
             submitJson.add("detail",detail);
             RestClient.builder()
                     .clearParams()
                     .raw(submitJson.toString())
                     .success(new ISuccess() {
                         @Override
                         public void onSuccess(String response) {
                             com.alibaba.fastjson.JSONObject object=JSON.parseObject(response);
                             int code=object.getIntValue("code");
                             switch (code)
                             {
                                 case 1:
                                     SignHandler.onSignIn(response,mISignListener);
                                     break;
                                 default:
                             }

                         }
                     })
                     .url(UploadConfig.API_HOST+"/api/addOpenid")
                     .build()
                     .post();
         }
     }
     private ISignListener mISignListener=null;
    //选择用户类型
    @OnItemSelected(R2.id.sp_add_weixin_user_role)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        role=parent.getItemAtPosition(position).toString();
    }
   @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISignListener){
            mISignListener=(ISignListener)activity;
        }
    }
    //发送验证码
    @OnClick(R2.id.btn_add_weixin_send_identifying_code)
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("create", "onCreate: ");
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            openId = args.getString(OPEN_ID);
        }
    }
    public static AddWeixinForUnregistedUserDelegate newInstance(String openId){
        Log.d("new", openId);
        final Bundle args = new Bundle();
        args.putString(OPEN_ID,openId);
        final AddWeixinForUnregistedUserDelegate delegate = new AddWeixinForUnregistedUserDelegate();
        delegate.setArguments(args);
        return delegate;
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_add_weixin_for_unregisted_user;
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
    }
    private boolean checkForm(){
        final String phone=mPhone.getText().toString();
        final String identifyCode=mIdentifyingCode.getText().toString();
        boolean isPass=true;
        if(phone.isEmpty()|| !RegexTool.isMobileNO(phone)){
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
        if(role.isEmpty()||role.equalsIgnoreCase("请选择用户类型"))
        {
            ((TextView) mUserRole.getChildAt(0)).setError("请选择用户类型");
            isPass=false;
        }else{
            ((TextView) mUserRole.getChildAt(0)).setError(null);
        }

        return isPass;
    }
}
