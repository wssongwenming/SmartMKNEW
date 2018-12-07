package com.dtmining.latte.mk.main.aboutme.medicineboxbind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.dtmining.latte.util.regex.RegexTool;
import com.dtmining.latte.util.storage.LattePreference;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/11/4
 * Description:
 */
public class BoxBindDelegate extends LatteDelegate {
    @BindView(R2.id.spinner_medicine_box_bind_boxid)
    AppCompatSpinner mBoxidSpinner=null;
    @OnItemSelected(R2.id.spinner_medicine_box_bind_boxid)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //Toast.makeText(this.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
        boxId=parent.getItemAtPosition(position).toString();

    }
    @OnClick(R2.id.btn_medicine_box_bind_submit)
    void onSubmit(){
        if(checkForm()){
            LattePreference.setBoxID(boxId);
            Toast.makeText(getContext(),"成功绑定当前药箱ID:"+LattePreference.getBoxId(),Toast.LENGTH_LONG).show();
            final IGlobalCallback<String> callback = CallbackManager
                    .getInstance()
                    .getCallback(CallbackType.ON_BIND_BOXID);
            if (callback != null) {
                callback.executeCallback(boxId);
            }
            pop();
        }
    }
    private String boxId;
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
            getBoxId();
        }
    }
    private boolean checkForm(){

        boolean isPass=true;
        TextView textView= (TextView) mBoxidSpinner.getChildAt(0);
        if(textView!=null){
            if(textView.getText().toString().equalsIgnoreCase("请选择药箱Id"))
            {
                textView.setError("请选择药箱Id");
                isPass=false;
            }else {
                textView.setError(null);

            }
        }
        return isPass;
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
}
