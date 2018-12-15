package com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListDataConverter;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/12/13
 * Description:
 */
public class DeleteBoxByHandDelegate extends LatteDelegate{
    String tel=null;
    private BoxListDataConverter converter=null;
    private BoxListAdapter mAdapter=null;
    private String boxId;
    @BindView(R2.id.spinner_medicine_box_delete_boxid)
    AppCompatSpinner mBoxidSpinner=null;
    @OnItemSelected(R2.id.spinner_medicine_box_delete_boxid)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //Toast.makeText(this.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
        boxId=parent.getItemAtPosition(position).toString();

    }
    @OnClick(R2.id.btn_medicine_box_delete_submit)
    void onSubmit(){
        if(checkForm()){
            JsonObject detail=new JsonObject();
            detail.addProperty("boxId",boxId);
            detail.addProperty("tel",tel);
            final JsonObject object=new JsonObject();
            object.add("detail",detail);
            RestClient.builder()
                    .clearParams()
                    .raw(object.toString())
                    .url(UploadConfig.API_HOST+"/api/Box_delete")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            com.alibaba.fastjson.JSONObject object1= JSON.parseObject(response);
                            int code=object1.getIntValue("code");
                            if(code==1){
                                getBoxId();
                                if(boxId==LattePreference.getBoxId())
                                {
                                    LattePreference.setBoxID(null);
                                    Toast.makeText(getContext(),"您删除了默认药箱",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    })
                    .build()
                    .post();
        }

    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_box_delete;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
            getBoxId();
        }
    }
    private void getBoxId(){
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_boxes")
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
    private boolean checkForm(){
        boolean isPass=true;

        TextView textView= (TextView) mBoxidSpinner.getChildAt(0);
        if(textView!=null){
            if(textView.getText().toString().equalsIgnoreCase("请选择药箱Id"))
            {
                textView.setError("请选择药箱Id");
                isPass = false;

            }else {
                textView.setError(null);
            }
        }

        return isPass;
    }
}
