package com.dtmining.latte.mk.ui.sub_delegates.hand_add;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxByScanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineAddModel;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineModel;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.ui.date.DateDialogUtil;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.log.LatteLogger;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class HandAddDelegate extends LatteDelegate {
    private static final String MEDICINE_CODE = "MEDICINE_CODE";
    //药品名称
    @BindView(R2.id.edit_medicine_hand_add_medicine_name)
    AppCompatEditText mMedicinName=null;
    //药品条形码
    @BindView(R2.id.edit_medicine_hand_add_medicine_code)
    AppCompatEditText mMedicineCode=null;
    // 药品有效期
    @BindView(R2.id.btn_medicine_hand_add_please_select_medicine_validity_time)
    AppCompatButton mBtnValidityTimeSelection=null;
    //药品添加数量
    @BindView(R2.id.edit_medicine_hand_add_medicine_count)
    AppCompatEditText mMedicineCount=null;
    //开始提醒时间
    @BindView(R2.id.btn_medicine_hand_add_please_select_start_remind_time)
    AppCompatButton mBtnStartRemindTimeSelection=null;
    //结束提醒时间
    @BindView(R2.id.btn_medicine_hand_add_please_select_end_remind_time)
    AppCompatButton mBtnEndRemindTimeSelection=null;
    //服药间隔
    @BindView(R2.id.edit_medicine_hand_add_day_interval)
    AppCompatEditText mInterval=null;
    //每天服药次数
    @BindView(R2.id.edit_medicine_hand_add_times_onday)
    AppCompatEditText mTimesOnDay=null;
    //单次服药量
    @BindView(R2.id.edit_medicine_hand_add_medicine_usecount)
    AppCompatEditText mMedicineUseCount=null;
    //外包装
    @BindView(R2.id.img_medicine_hand_add_appearance)
    AppCompatImageView mMedicineImage=null;
    //外包装图片回传路径
    String medicineImage="";
    //药箱ID
    @BindView(R2.id.spinner_medicine_hand_add_boxid)
    AppCompatSpinner mBoxidSpinner=null;
    @BindView(R2.id.btn_medicine_hand_add_submit)
    AppCompatButton mSubmit=null;
    String boxId=null;
    @OnItemSelected(R2.id.spinner_medicine_hand_add_boxid)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //Toast.makeText(this.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
        boxId=parent.getItemAtPosition(position).toString();
    }
    private String medicineCode=null;
    private BoxListDataConverter converter=null;
    private BoxListAdapter mAdapter=null;
    private String tel=null;
    @OnClick(R2.id.btn_medicine_hand_add_submit)
    void onClickSubmit(){
        if(checkForm()){
            MedicineModel medicineModel=new MedicineModel();
            MedicineAddModel medicineAddModel=new MedicineAddModel();
            medicineModel.setBoxId(boxId);
            medicineModel.setDayInterval(mInterval.getText().toString());
            medicineModel.setEndRemind(mBtnEndRemindTimeSelection.getText().toString());
            medicineModel.setMedicineCode(mMedicineCode.getText().toString());
            medicineModel.setMedicineCount(mMedicineCount.getText().toString());
            medicineModel.setMedicineImage(medicineImage);
            medicineModel.setMedicineName(mMedicinName.getText().toString());
            medicineModel.setMedicineUseCount(mMedicineUseCount.getText().toString());
            medicineModel.setMedicineValidity(mBtnValidityTimeSelection.getText().toString());
            medicineModel.setStartRemind(mBtnStartRemindTimeSelection.getText().toString());
            medicineModel.setTel(tel);
            medicineModel.setTimesOnDay(mTimesOnDay.getText().toString());
            medicineAddModel.setDetail(medicineModel);
            String medicineAddJson = JSON.toJSON(medicineAddModel).toString();
            RestClient.builder()
                    .url("http://39.105.97.128:8080/medicinebox/api/Medicine_add")
                    .raw(medicineAddJson)
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
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            medicineCode = args.getString(MEDICINE_CODE);
        }
    }
    public static HandAddDelegate newInstance(String medicineCode){
        final Bundle args = new Bundle();
        args.putString(MEDICINE_CODE,medicineCode);
        final HandAddDelegate delegate = new HandAddDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    private void initData() {
        if(medicineCode!=null) {
            mMedicineCode.setText(medicineCode);
        }
    }
    //表单验证
    private boolean checkForm(){
        final String medicineName= mMedicinName.getText().toString();
        final String medicineCode=mMedicineCode.getText().toString();
        final String validityTime=mBtnValidityTimeSelection.getText().toString();
        final String medicineCount=mMedicineCount.getText().toString();
        final String medicineStartTime=mBtnStartRemindTimeSelection.getText().toString();
        final String medicineEndTime=mBtnEndRemindTimeSelection.getText().toString();
        final String interval=mInterval.getText().toString();
        final String timesOneDay=mTimesOnDay.getText().toString();
        final String medicineUseCount=mMedicineUseCount.getText().toString();
        boolean isPass=true;
        if(medicineName.isEmpty()||medicineName==null){
            mMedicinName.setError("请填写药品名！");
            isPass=false;
        }else{
         mMedicinName.setError(null);
        }
        if(medicineCode.isEmpty()||medicineCode==null){
            mMedicineCode.setError("请填写药品条形码！");
            isPass=false;
        }else{
            mMedicineCode.setError(null);
        }
        if(validityTime.isEmpty()||validityTime.equalsIgnoreCase("请选择有效期")){
            mBtnValidityTimeSelection.setError("请选择有效期");
            isPass=false;
        }else{
            mBtnValidityTimeSelection.setError(null);
        }
        if(medicineCount.isEmpty()||medicineCount==null){
            mMedicineCount.setError("请填写药品添加数量！");
            isPass=false;
        }else{
            mMedicineCount.setError(null);
        }

        if(medicineStartTime.isEmpty()||medicineStartTime.equalsIgnoreCase("请选择开始提醒时间")){
            mBtnStartRemindTimeSelection.setError("请选择开始提醒时间！");
            isPass=false;
        }else{
            mBtnStartRemindTimeSelection.setError(null);
        }
        if(medicineEndTime.isEmpty()||medicineEndTime.equalsIgnoreCase("请选择结束提醒时间")){
            mBtnEndRemindTimeSelection.setError("请选择结束提醒时间！");
            isPass=false;
        }else{
            mBtnEndRemindTimeSelection.setError(null);
        }
        if(interval.isEmpty()||interval==null){
            mInterval.setError("请填写服药间隔！");
            isPass=false;
        }else{
            mInterval.setError(null);
        }
        if(timesOneDay.isEmpty()||timesOneDay==null){
            mTimesOnDay.setError("请填写服药次数/每天！");
            isPass=false;
        }else{
            mTimesOnDay.setError(null);
        }
        if(medicineUseCount.isEmpty()||medicineUseCount==null){
            mMedicineUseCount.setError("请填写单次服药量！");
            isPass=false;
        }else{
            mMedicineUseCount.setError(null);
        }
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

    @OnClick(R2.id.btn_medicine_hand_add_please_select_medicine_validity_time)
    void onSelectValidityTimeClick(){
        final DateDialogUtil dateDialogUtil = new DateDialogUtil();
        dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
            @Override
            public void onDateChange(String date) {
                mBtnValidityTimeSelection.setText(date.replace("年","-").replace("月","-").replace("日",""));
            }
        });
        dateDialogUtil.showDialog(getContext());
    }

    @OnClick(R2.id.btn_medicine_hand_add_please_select_start_remind_time)
    void onSelectStartRemindTimeClick(){
        final DateDialogUtil dateDialogUtil = new DateDialogUtil();
        dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
            @Override
            public void onDateChange(String date) {
                mBtnStartRemindTimeSelection.setText(date.replace("年","-").replace("月","-").replace("日",""));
            }
        });
        dateDialogUtil.showDialog(getContext());
    }

    @OnClick(R2.id.btn_medicine_hand_add_please_select_end_remind_time)
    void onSelectEndRemindTimeClick(){
        final DateDialogUtil dateDialogUtil = new DateDialogUtil();
        dateDialogUtil.setDateListener(new DateDialogUtil.IDateListener() {
            @Override
            public void onDateChange(String date) {
                mBtnEndRemindTimeSelection.setText(date.replace("年","-").replace("月","-").replace("日",""));
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
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {

                                        medicineImage = JSON.parseObject(response).getJSONObject("detail")
                                                .getString("path");

/*                                        //通知服务器更新信息
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
                                                .post();*/
                                    }
                                })
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
            getBoxIdList();
        }
        initData();
    }

    private void getBoxIdList(){
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
