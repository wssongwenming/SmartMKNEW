package com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
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
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.model.MedicineBox;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.model.MedicineBoxModel;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/11/3
 * Description:
 */
public class AddMedicineBoxByScanDelegate extends LatteDelegate {
    String tel=null;
    @BindView(R2.id.box_add_by_san_boxid)
    AppCompatEditText mBoxId=null;
    @OnClick(R2.id.box_add_by_scan_confirm)
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
                            pop();
                        }
                    })
                    .build()
                    .post();
        }
    }
    private static final String BOX_ID = "BOX_ID";
    private String boxId ="";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            boxId = args.getString(BOX_ID);
        }
    }
    public static AddMedicineBoxByScanDelegate newInstance(String boxId){
        final Bundle args = new Bundle();
        args.putString(BOX_ID,boxId);
        final AddMedicineBoxByScanDelegate delegate = new AddMedicineBoxByScanDelegate();
        delegate.setArguments(args);
        return delegate;
    }
    private void initData() {
        mBoxId.setText(boxId);
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_box_add_by_scan;
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        //Toast.makeText(getContext(),boxId,Toast.LENGTH_LONG).show();
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
        initData();
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
