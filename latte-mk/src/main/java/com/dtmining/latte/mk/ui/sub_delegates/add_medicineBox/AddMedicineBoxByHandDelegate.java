package com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.EntryType;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.sign.model.SignModel;
import com.dtmining.latte.mk.sign.model.User;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.model.MedicineBox;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.model.MedicineBoxModel;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.regex.RegexTool;
import com.dtmining.latte.util.storage.LattePreference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/3
 * Description:
 */
public class AddMedicineBoxByHandDelegate extends LatteDelegate {
    private String boxId=null;
    @BindView(R2.id.et_box_add_by_hand_boxid)
    EditText mBoxId=null;
    @OnClick(R2.id.box_add_by_hand_confirm)
    void onClick() {
        if (checkForm()) {
            boxId=mBoxId.getText().toString();
            MedicineBox medicineBox=new MedicineBox();
            MedicineBoxModel medicineBoxModel =new MedicineBoxModel();
            medicineBox.setTel(tel);
            medicineBox.setBoxId(mBoxId.getText().toString());
            medicineBoxModel.setDetail(medicineBox);
            String medicineBoxJson = JSON.toJSON(medicineBoxModel).toString();
            RestClient.builder()
                    .clearParams()
                    .url(UploadConfig.API_HOST+"/api/Box_bind")
                    .raw(medicineBoxJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            LattePreference.setBoxID(boxId);
                            Toast.makeText(getContext(),"药箱绑定成功",Toast.LENGTH_LONG).show();
                            final IGlobalCallback<String> change_boxId_for_history = CallbackManager
                                    .getInstance()
                                    .getCallback(CallbackType. ON_CHANGE_BOXID_FOR_HISTORY);
                            if (change_boxId_for_history != null) {
                                change_boxId_for_history.executeCallback("");
                            }
                            final IGlobalCallback<String> change_boxId_for_plan = CallbackManager
                                    .getInstance()
                                    .getCallback(CallbackType. ON_GET_MEDICINE_PLAN_INDEX);
                            if (change_boxId_for_plan != null) {
                                change_boxId_for_plan.executeCallback("");
                            }
                            final IGlobalCallback<String> UpdateRecentUseCallback = CallbackManager
                                    .getInstance()
                                    .getCallback(CallbackType.ON_CHANGE_BOXID_FOR_RECENT_USE);
                            if (UpdateRecentUseCallback != null) {
                                UpdateRecentUseCallback.executeCallback("");
                            }
                            pop();
                        }
                    })
                    .build()
                    .post();
        }
    }
    String tel=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_box_add_by_hand;

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
    boolean checkForm(){
        final String boxId=mBoxId.getText().toString();
        boolean isPass=true;
        if(boxId.isEmpty()){
            mBoxId.setError("请填写正确的药箱编码！");
            isPass=false;
        }else{
            mBoxId.setError(null);
        }
        return isPass;
    }
}
