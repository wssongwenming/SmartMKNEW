package com.dtmining.latte.mk.ui.sub_delegates.hand_add;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import com.bumptech.glide.Glide;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.ui.date.DateDialogUtil;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.log.LatteLogger;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class HandAddDelegate extends LatteDelegate {

    @BindView(R2.id.edit_medicine_hand_add_medicine_name)
    AppCompatEditText mMedicinName=null;
    @BindView(R2.id.btn_medicine_hand_add_please_select_production_time)
    AppCompatButton mBtnProductionTimeSelection=null;
    @BindView(R2.id.btn_medicine_hand_add_please_select_overdue_time)
    AppCompatButton mBtnOverdueTimeSelect=null;
    @BindView(R2.id.edit_medicine_hand_add_medicine_count)
    AppCompatEditText mMedicineCount=null;
    @BindView(R2.id.img_medicine_hand_add_appearance)
    AppCompatImageView mMedicineImage=null;
    @BindView(R2.id.spinner_medicine_hand_add_boxid)
    AppCompatSpinner mBoxidSpinner=null;
    @BindView(R2.id.btn_medicine_hand_add_submit)
    AppCompatButton mSubmit=null;

    private BoxListDataConverter converter=null;
    private BoxListAdapter mAdapter=null;
    private String tel=null;
    @OnClick()
    void onClickSubmit(){

    }
    @OnClick(R2.id.btn_medicine_hand_add_please_select_overdue_time)
    void onSelectOverDueTimeClick(){
        final DateDialogUtil dateDialogUtil = new DateDialogUtil();
        dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
            @Override
            public void onDateChange(String date) {
                mBtnOverdueTimeSelect.setText(date.replace("年","-").replace("月","-").replace("日",""));
            }
        });
        dateDialogUtil.showDialog(getContext());
    }

    @OnClick(R2.id.btn_medicine_hand_add_please_select_production_time)
    void onSelectProductionTimeClick(){
        final DateDialogUtil dateDialogUtil = new DateDialogUtil();
        dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
            @Override
            public void onDateChange(String date) {
                mBtnProductionTimeSelection.setText(date.replace("年","-").replace("月","-").replace("日",""));
            }
        });
        dateDialogUtil.showDialog(getContext());
    }

    @OnClick(R2.id.img_medicine_hand_add_appearance)
    void onClickImg(){
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                    @Override
                    public void executeCallback(Uri args) {
                        LatteLogger.d("ON_CROP", args.getPath());
                        Glide.with(getContext())
                                .load(args)
                                .into(mMedicineImage);
                        RestClient.builder()
                                .url(UploadConfig.UPLOAD_IMG)
                                .loader(getContext())
                                .file(args.getPath())
/*                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        LatteLogger.d("ON_CROP_UPLOAD", response);
                                        final String path = JSON.parseObject(response).getJSONObject("result")
                                                .getString("path");

                                        //通知服务器更新信息
                                        RestClient.builder()
                                                .url("user_profile.php")
                                                .params("avatar", path)
                                                .loader(getContext())
                                                .success(new ISuccess() {
                                                    @Override
                                                    public void onSuccess(String response) {
                                                        //获取更新后的用户信息，然后更新本地数据库
                                                        //没有本地数据的APP，每次打开APP都请求API，获取信息
                                                    }
                                                })
                                                .build()
                                                .post();
                                    }
                                })*/
                                .build()
                                .upload();
                    }
                });
        this.startCameraWithCheck();
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_hand_add;
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
