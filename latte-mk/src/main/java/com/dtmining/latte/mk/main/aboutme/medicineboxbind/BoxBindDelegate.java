package com.dtmining.latte.mk.main.aboutme.medicineboxbind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListDataConverter;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.regex.RegexTool;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/4
 * Description:
 */
public class BoxBindDelegate extends LatteDelegate {
    @BindView(R2.id.spinner_medicine_box_bind_boxid)
    AppCompatSpinner mBoxidSpinner=null;
    @OnClick(R2.id.btn_medicine_box_bind_submit)
    void onSubmit(){
        checkForm();
    }
    private BoxListDataConverter converter=null;
    private BoxListAdapter mAdapter=null;
    private String tel=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_box_bind;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
    }
    private boolean checkForm(){
        Toast.makeText(getContext(),((AppCompatTextView)mBoxidSpinner.getChildAt(0)).getText(),Toast.LENGTH_LONG).show();
        boolean isPass=true;
        if(((AppCompatTextView)mBoxidSpinner.getChildAt(0)).getText().toString().equalsIgnoreCase("请选择药箱Id")){
            ((TextView) mBoxidSpinner.getChildAt(0)).setError("请选择药箱Id");
            isPass=false;
        }else {
            ((TextView) mBoxidSpinner.getChildAt(0)).setError(null);;
        }
        return isPass;
    }
    private void getBoxId(){
        RestClient.builder()
                .url("http://10.0.2.2:8081/Web01_exec/get_box")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        converter=new BoxListDataConverter();
                        mAdapter= BoxListAdapter.create(converter.setJsonData(response),R.layout.simple_single_item_list);
                        mBoxidSpinner.setAdapter(mAdapter);
                    }
                })
                .build()
                .get();

    }
}
