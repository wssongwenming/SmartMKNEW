package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.mymedicineboxes.MedicineMineDelegateForBox;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.aboutme.profile.UserProfileDelegate;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxByScanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.BoxListDataConverter;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineAddModel;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineEditModel;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineModel;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineModelForEdit;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicineListDataConverter;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.ui.date.DateDialogUtil;
import com.dtmining.latte.util.ToastUtil;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/12/14
 * Description:
 */
public class MedicineMineEditDelegate extends LatteDelegate {
    private ArrayList<String> medicineNameList=new java.util.ArrayList<>();
    private HandlerThread handlerThread=new HandlerThread("");
    private Handler myHandler=null;
    private String msgid=null;

    private int medicineType;
    private String tel;
    private String medicineId;
    private BoxListDataConverter converter=null;
    private BoxListAdapter mAdapter=null;
    private int interval;
    private final static String MEDICINEMODEL="MEDICINEMODEL";
    private MedicineModel medicineModel;
    //药品名称
    @BindView(R2.id.edit_medicine_hand_edit_medicine_name)
    AppCompatEditText mMedicinName=null;
    //药品条形码
    @BindView(R2.id.edit_medicine_hand_edit_medicine_code)
    AppCompatEditText mMedicineCode=null;
    // 药品有效期
    @BindView(R2.id.btn_medicine_hand_edit_please_select_medicine_validity_time)
    AppCompatButton mBtnValidityTimeSelection=null;
    //剂量单位
    @BindView(R2.id.sp_medicine_hand_edit_dose_unit)
    Spinner mDoseUnitSpinner=null;
    @OnItemSelected(R2.id.sp_medicine_hand_edit_dose_unit)
    public void onDoseUnitSelected(AdapterView<?> parent, View view,int pos, long id)
    {
        medicineType=pos-1;
    }
    //药品添加数量
    @BindView(R2.id.edit_medicine_hand_edit_medicine_count)
    AppCompatEditText mMedicineCount=null;
    //开始提醒时间
    @BindView(R2.id.btn_medicine_hand_edit_please_select_start_remind_time)
    AppCompatButton mBtnStartRemindTimeSelection=null;
    //结束提醒时间
    @BindView(R2.id.btn_medicine_hand_edit_please_select_end_remind_time)
    AppCompatButton mBtnEndRemindTimeSelection=null;
    //服药间隔
    @BindView(R2.id.sp_medicine_hand_edit_day_interval)
    Spinner mTimeSpanSpinner=null;
    @OnItemSelected(R2.id.sp_medicine_hand_edit_day_interval)
    public void onIntervalSelected(AdapterView<?> parent, View view,int pos, long id)
    {
        interval=pos-1;
    }
   //每天服药次数
    @BindView(R2.id.edit_medicine_hand_edit_times_onday)
    AppCompatEditText mTimesOnDay=null;
    //单次服药量
    @BindView(R2.id.edit_medicine_hand_edit_medicine_usecount)
    AppCompatEditText mMedicineUseCount=null;
    //外包装
    @BindView(R2.id.img_medicine_hand_edit_appearance)
    AppCompatImageView mMedicineImage=null;
    //外包装图片回传路径
    String medicineImage=null;
    //药箱ID
    @BindView(R2.id.spinner_medicine_hand_edit_boxid)
    AppCompatSpinner mBoxidSpinner=null;
    @BindView(R2.id.btn_medicine_hand_edit_submit)
    AppCompatButton mSubmit=null;
    String boxId=null;
    @OnItemSelected(R2.id.spinner_medicine_hand_edit_boxid)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        boxId=parent.getItemAtPosition(position).toString();
        if(!boxId.equalsIgnoreCase("请选择药箱Id")){
            getMedicineList(boxId);
        }else {
            //Toast.makeText(this.getContext(),boxId,Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R2.id.btn_medicine_hand_edit_please_select_medicine_validity_time)
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

    @OnClick(R2.id.btn_medicine_hand_edit_please_select_start_remind_time)
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

    @OnClick(R2.id.btn_medicine_hand_edit_please_select_end_remind_time)
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

    @OnClick(R2.id.img_medicine_hand_edit_appearance)
    void onClickImg(){
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                    @Override
                    public void executeCallback(Uri args) {
                        //File  fileDir = Environment.getExternalStorageDirectory();
                        //BitmapFactory.Options opts = new BitmapFactory.Options();
                        //File imageFile = new File(args.getPath());
                        //Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), opts);
                        // File file=new File(fileDir,new Random()+"jpeg");
                        //compressImage2FileBySize(bitmap,file,19);
                        Glide.with(getContext())
                                .load(args)
                                .into(mMedicineImage);
                        RestClient.builder()
                                .url(UploadConfig.API_HOST+"/api/fileupload")
                                .loader(getContext())
                                .file(args.getPath())
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        if (response != null) {
                                            final JSONObject responseObject = JSON.parseObject(response);
                                            Log.d("imgurl", responseObject.toJSONString());
                                            int code = responseObject.getIntValue("code");
                                            if (code == 1) {
                                                //获得图片保存路径
                                                medicineImage=responseObject.getString("url");
                                                //Toast.makeText(getContext(),medicineImage,Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                })
                                .build()
                                .upload();
                    }
                });
        this.startCameraWithCheck();
    }
    @OnClick(R2.id.btn_medicine_hand_edit_submit)
    void editMedicine(){
        if(checkForm()){
            MedicineModelForEdit medicineModel=new MedicineModelForEdit();
            MedicineEditModel medicineEditModel=new MedicineEditModel();
            medicineModel.setBoxId(boxId);
            medicineModel.setDayInterval(interval);
            medicineModel.setEndRemind(mBtnEndRemindTimeSelection.getText().toString());
            medicineModel.setMedicineCode(mMedicineCode.getText().toString());
            medicineModel.setMedicineCount(mMedicineCount.getText().toString());
            medicineModel.setMedicineImage(medicineImage);
            medicineModel.setMedicineId(medicineId);
            medicineModel.setMedicineType(medicineType);
            medicineModel.setMedicineName(mMedicinName.getText().toString());
            medicineModel.setMedicineUseCount(Integer.parseInt(mMedicineUseCount.getText().toString()));
            medicineModel.setMedicineValidity(mBtnValidityTimeSelection.getText().toString());
            medicineModel.setStartRemind(mBtnStartRemindTimeSelection.getText().toString());
            medicineModel.setTel(tel);
            medicineModel.setTimesOnDay(Integer.parseInt(mTimesOnDay.getText().toString()));
            medicineEditModel.setDetail(medicineModel);
            final String medicineEditJson = JSON.toJSON(medicineEditModel).toString();
            Log.d("medicineEditJson",  medicineEditJson);
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/Medicine_update")
                    .clearParams()
                    .raw(medicineEditJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            JSONObject object=JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            if(code==1){
                                msgid=object.getString("msgid");
                                Log.d("msgid", "msgid="+msgid);
                                if(msgid!=null) {
                                    myHandler.postDelayed(updateThread, 1000);
                                }
                            }
                            /*LatteDelegate delegate= Latte.getConfiguration(ConfigKeys.MEDICINEMINEDELEGATE);
                            if(delegate instanceof MedicineMineDelegate)
                            {
                                ((MedicineMineDelegate)delegate).onRefresh();
                            }else if(delegate instanceof MedicineMineDelegateForBox)
                            {
                                ((MedicineMineDelegateForBox)delegate).onRefresh();
                            }
                            pop();*/
                        }
                    })
                    .build()
                    .post();
        }
    }
    private boolean checkForm(){
        final String medicineName= mMedicinName.getText().toString();
        final String medicineCode=mMedicineCode.getText().toString();
        final String validityTime=mBtnValidityTimeSelection.getText().toString();
        final String medicineCount=mMedicineCount.getText().toString();
        final String medicineStartTime=mBtnStartRemindTimeSelection.getText().toString();
        final String medicineEndTime=mBtnEndRemindTimeSelection.getText().toString();
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
        //图片校验
        if(medicineImage.isEmpty()||medicineImage==null){
            mMedicineImage.setImageResource(R.drawable.warn);
            isPass=false;
        }else{
            //mMedicineImage.setError(null);
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
                isPass = false;

            }else {
                textView.setError(null);
            }
        }
        TextView daysInterval= (TextView) mTimeSpanSpinner.getChildAt(0);
        if(daysInterval!=null){
            if(daysInterval.getText().toString().equalsIgnoreCase("请选择服药间隔"))
            {
                daysInterval.setError("请选择服药间隔");
                isPass = false;

            }else {
                daysInterval.setError(null);
            }
        }
        TextView doseUnit= (TextView) mDoseUnitSpinner.getChildAt(0);
        if(doseUnit!=null){
            if(doseUnit.getText().toString().equalsIgnoreCase("请选择剂量单位"))
            {
                doseUnit.setError("请选择剂量单位");
                isPass = false;

            }else {
                doseUnit.setError(null);
            }
        }

        return isPass;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_hand_edit;
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
        ArrayAdapter adap = new ArrayAdapter<String>(getContext(), R.layout.single_item_tv, new String[]{"请选择服药间隔","每天", "间隔1天","间隔2天","间隔3天","间隔4天","间隔5天","间隔6天","间隔7天"});
        mTimeSpanSpinner.setAdapter(adap);
        com.dtmining.latte.mk.adapter.SpinnerAdapter doseunitadap = new com.dtmining.latte.mk.adapter.SpinnerAdapter<String>(getContext(), R.layout.single_item_tv, Arrays.asList(new String[]{"请选择剂量单位","片", "粒/颗", "瓶/支", "包/袋", "克", "毫升", "其他"}));
        mDoseUnitSpinner.setAdapter(doseunitadap);
        initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerThread.start();
        myHandler=new Handler(handlerThread.getLooper());
        final Bundle args = getArguments();
        if (args != null) {
            medicineModel = (MedicineModel) args.getSerializable(MEDICINEMODEL);
        }

    }
    public static MedicineMineEditDelegate newInstance(MedicineModel medicineModel){
        final Bundle args = new Bundle();
        args.putSerializable(MEDICINEMODEL,medicineModel);
        final MedicineMineEditDelegate delegate = new MedicineMineEditDelegate();
        delegate.setArguments(args);
        return delegate;
    }
    private void initData(){
        Log.d("count",medicineModel.getMedicineCount()+"");
        mMedicinName.setText(medicineModel.getMedicineName());
        medicineId=medicineModel.getMedicineId();
        mMedicineCode.setText(medicineModel.getMedicineCode());
        mBtnValidityTimeSelection.setText(medicineModel.getMedicineValidity());
        mMedicineCount.setText(medicineModel.getMedicineCount()+"");

        mBtnStartRemindTimeSelection.setText(medicineModel.getStartRemind());
        mBtnEndRemindTimeSelection.setText(medicineModel.getEndRemind());
        mTimeSpanSpinner.setSelection(medicineModel.getDayInterval()+1);
        mTimesOnDay.setText(medicineModel.getTimesOnDay()+"");
        mMedicineUseCount.setText(medicineModel.getMedicineUseCount()+"");
        medicineImage=medicineModel.getMedicineImage();
        Glide.with(getContext())
                .load(UploadConfig.UPLOAD_IMG+medicineModel.getMedicineImage())
                .into(mMedicineImage);
        setSpinnerItemSelectedByValue(mBoxidSpinner,medicineModel.getBoxId());
        mDoseUnitSpinner.setSelection(medicineModel.getMedicineType()+1);
    }
    public  void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
    }

    //返回键监听实现
    public interface RefreshListener {
        void onRefresh();
    }
    private void getBoxIdList(){
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_boxes")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        converter=new BoxListDataConverter();
                        mAdapter= BoxListAdapter.create(converter.setJsonData(response),R.layout.simple_single_item_list);
                        mBoxidSpinner.setAdapter(mAdapter);
                        initData();
                    }
                })
                .build()
                .get();

    }
    private void getMedicineList(String boxId){
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine_of_box")
                //.url("medicine_mine")
                .params("tel",tel)
                .params("boxId",boxId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        MedicineListDataConverter converter = new MedicineListDataConverter();
                        medicineNameList.addAll(converter.getMedicinesOfBox(response));
                        //Toast.makeText(getContext(),medicineNameList.toString(),Toast.LENGTH_LONG).show();
                    }
                })
                .build()
                .get();

    }
    Runnable updateThread=new Runnable() {
        @Override
        public void run() {
            RestClient.builder()
                    .clearParams()
                    .params("uuid",msgid)
                    //.url("http://192.168.1.3:8081/Web01_exec/getStatus")
                    .url(UploadConfig.API_HOST+"/api/getStatus")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            JSONObject object=JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            Log.d("statuscode", code+"");
                            if(code==1){
                                ToastUtil.showToast((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "药品数据已修改等待向硬件端同步");
                                myHandler.postDelayed(updateThread,1000);
                            }
                            if(code==2){
                                myHandler.removeCallbacks(updateThread);
                                ToastUtil.showToast((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "药品数据已成功修改");
                                LatteDelegate delegate= Latte.getConfiguration(ConfigKeys.MEDICINEMINEDELEGATE);
                                if(delegate instanceof MedicineMineDelegate)
                                {
                                    ((MedicineMineDelegate)delegate).onRefresh();
                                }else if(delegate instanceof MedicineMineDelegateForBox)
                                {
                                    ((MedicineMineDelegateForBox)delegate).onRefresh();
                                }
                                pop();


                            }
                            if(code==3||code==4){
                                myHandler.removeCallbacks(updateThread);
                                Toast.makeText((Context)Latte.getConfiguration(ConfigKeys.ACTIVITY), "药品添加失败，请重新编辑", Toast.LENGTH_LONG).show();
                                pop();
                            }
                        }
                    })
                    .build()
                    .get();

        }
    };

}
