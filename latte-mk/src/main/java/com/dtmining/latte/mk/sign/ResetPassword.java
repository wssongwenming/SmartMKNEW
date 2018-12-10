package com.dtmining.latte.mk.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
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
    private static final String TEL = "TEL";
    private String tel=null;
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
            user.setTel(tel);
            signModel.setDetail(user);
            String  resetPasswordJson= JSON.toJSON(signModel).toString();
            Log.d("res1", resetPasswordJson);
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/ResetPassword")
                    .raw(resetPasswordJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d("res", response);
                            if(response!=null){

                                Log.d("res", response);
                                JSONObject object=JSON.parseObject(response);
                                int code=object.getIntValue("code");
                                if(code==1){
                                    SignHandler.onSignIn(response,mISignListener);
                                }
                            }

                        }
                    })
                    .build()
                    .post();

        }
    }
    private ISignListener mISignListener=null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISignListener)
        {
            mISignListener=(ISignListener) activity;
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            tel = args.getString(TEL);
        }
    }
    public static ResetPassword newInstance(String medicineCode){
        final Bundle args = new Bundle();
        args.putString(TEL,medicineCode);
        final ResetPassword delegate = new ResetPassword();
        delegate.setArguments(args);
        return delegate;
    }
}
