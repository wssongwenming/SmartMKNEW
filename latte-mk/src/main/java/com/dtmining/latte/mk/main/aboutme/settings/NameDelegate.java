package com.dtmining.latte.mk.main.aboutme.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.aboutme.profile.UserProfileClickListener;
import com.dtmining.latte.mk.main.aboutme.profile.UserProfileDelegate;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.regex.RegexTool;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.OnClick;


/**
 */

public class NameDelegate extends LatteDelegate {
    private String tel=null;
    @BindView(R2.id.et_userprofile_name)
    AppCompatEditText mUserName=null;
    @BindView(R2.id.btn_name_submit)
    AppCompatButton subMit=null;
    @OnClick(R2.id.btn_name_submit)
    void  onSubmit(){
        if(checkForm()){
            String userName=mUserName.getText().toString();
            JsonObject detail=new JsonObject();
            detail.addProperty("tel",tel);
            detail.addProperty("username",userName);
            JsonObject jsonObject=new JsonObject();
            jsonObject.add("detail",detail);
            Log.d("ok", jsonObject.toString());
            RestClient.builder()
                    .clearParams()
                    .raw(jsonObject.toString())
                    .url(UploadConfig.API_HOST+"/api/UserUpdate")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            Log.d("ok", response);
                            ((UserProfileDelegate)Latte.getConfiguration(ConfigKeys.ABOUNTMEDELEGATE)).onRefresh();
                            pop();
                        }
                    })
                    .build()
                    .post();
        }
    }
    @OnClick(R2.id.btn_name_cancel)
    void cancel(){
        pop();
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_name;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
    }
    private boolean checkForm(){
        final String name=mUserName.getText().toString();
        boolean isPass=true;

        if(name.isEmpty()){
            mUserName.setError("姓名不能为空");
            isPass=false;
        }else{
            mUserName.setError(null);
        }
        return isPass;
    }
    //返回键监听实现
    public interface RefreshListener {
        void onRefresh();
    }
    private UserProfileClickListener.RefreshListener backListener;
}
