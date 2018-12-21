package com.dtmining.latte.mk.ui.sub_delegates.hand_add;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.dtmining.latte.mk.adapter.SpinnerAdapter;
import com.dtmining.latte.mk.main.aboutme.AboutMeDelegate;
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
import com.dtmining.latte.util.storage.LattePreference;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class HandAddDelegate extends LatteDelegate {
    private int interval;
    private int medicineType;
    private static final String MEDICINE_CODE = "MEDICINE_CODE";
    private static final String MEDICINE_NAME="MEDICINE_NAME";
    //药品名称
    @BindView(R2.id.edit_medicine_hand_add_medicine_name)
    AppCompatEditText mMedicinName=null;
    //药品条形码
    @BindView(R2.id.edit_medicine_hand_add_medicine_code)
    AppCompatEditText mMedicineCode=null;
    // 药品有效期
    @BindView(R2.id.btn_medicine_hand_add_please_select_medicine_validity_time)
    AppCompatButton mBtnValidityTimeSelection=null;
    //剂量单位
    @BindView(R2.id.sp_medicine_hand_add_dose_unit)
    Spinner mDoseUnitSpinner=null;
    @OnItemSelected(R2.id.sp_medicine_hand_add_dose_unit)
    public void onDoseUnitSelected(AdapterView<?> parent, View view,int pos, long id)
    {
        medicineType=pos-1;
    }
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
    @BindView(R2.id.sp_medicine_hand_add_day_interval)
    Spinner mTimeSpanSpinner=null;
    @OnItemSelected(R2.id.sp_medicine_hand_add_day_interval)
    public void onIntervalSelected(AdapterView<?> parent, View view,int pos, long id)
    {
        interval=pos;
    }
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
    private String medicineName=null;
    private BoxListDataConverter converter=null;
    private BoxListAdapter mAdapter=null;
    private String tel=null;
    @OnClick(R2.id.btn_medicine_hand_add_submit)
    void onClickSubmit(){
        if(checkForm()){
            MedicineModel medicineModel=new MedicineModel();
            MedicineAddModel medicineAddModel=new MedicineAddModel();
            medicineModel.setBoxId(boxId);
            medicineModel.setDayInterval(interval);
            medicineModel.setMedicineType(medicineType);
            medicineModel.setEndRemind(mBtnEndRemindTimeSelection.getText().toString());
            medicineModel.setMedicineCode(mMedicineCode.getText().toString());
            medicineModel.setMedicineCount(Integer.parseInt(mMedicineCount.getText().toString()));
            medicineModel.setMedicineImage(medicineImage);
            medicineModel.setMedicineName(mMedicinName.getText().toString());
            medicineModel.setMedicineUseCount(Integer.parseInt(mMedicineUseCount.getText().toString()));
            medicineModel.setMedicineValidity(mBtnValidityTimeSelection.getText().toString());
            medicineModel.setStartRemind(mBtnStartRemindTimeSelection.getText().toString());
            medicineModel.setTel(tel);
            medicineModel.setTimesOnDay(Integer.parseInt(mTimesOnDay.getText().toString()));
            medicineAddModel.setDetail(medicineModel);
            final String medicineAddJson = JSON.toJSON(medicineAddModel).toString();
            Log.d("drugadd", medicineAddJson);
            RestClient.builder()
                    .url(UploadConfig.API_HOST+"/api/Medicine_add")
                    .clearParams()
                    .raw(medicineAddJson)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            if(response!=null) {
                                JSONObject object=JSON.parseObject(response);
                                int code=object.getIntValue("code");
                                if(code==1)
                                {
                                    Toast.makeText(getContext(), "药品添加成功", Toast.LENGTH_LONG).show();
                                    mMedicinName.setText(null);
                                    mMedicineCode.setText(null);
                                    mDoseUnitSpinner.setSelection(0);
                                    mBtnValidityTimeSelection.setText("请选择有效期");
                                    mMedicineCount.setText(null);
                                    mBtnStartRemindTimeSelection.setText("请选择开始提醒时间");
                                    mBtnEndRemindTimeSelection.setText("请选择结束提醒时间");
                                    mMedicineUseCount.setText(null);
                                    mTimesOnDay.setText(null);
                                    Glide.with(getContext())
                                            .load(null)
                                            .into(mMedicineImage);


                                }
                            }
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
            medicineName=args.getString(MEDICINE_NAME);

        }
    }
    public static HandAddDelegate newInstance(String medicineCode,String medicineName){
        final Bundle args = new Bundle();
        args.putString(MEDICINE_CODE,medicineCode);
        //if(medicineName!=null){
            args.putString(MEDICINE_NAME,medicineName);
        //}
        final HandAddDelegate delegate = new HandAddDelegate();
        delegate.setArguments(args);
        return delegate;
    }
    private void initData() {
        if(medicineCode!=null) {
            mMedicineCode.setText(medicineCode);
            mMedicinName.setText(medicineName);

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
        //图片校验
        if(medicineImage.isEmpty()||medicineImage==null){
            mMedicineImage.setImageResource(R.drawable.warn);
            isPass=false;
        }else{
            //mMedicineImage.setError(null);
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
        ArrayAdapter timespanadap = new ArrayAdapter<String>(getContext(), R.layout.single_item_tv, new String[]{"请选择服药间隔","每天", "间隔1天","间隔2天","间隔3天","间隔4天","间隔5天","间隔6天","间隔7天"});
        mTimeSpanSpinner.setAdapter(timespanadap);

        SpinnerAdapter doseunitadap = new SpinnerAdapter<String>(getContext(), R.layout.single_item_tv, Arrays.asList(new String[]{"请选择剂量单位","片", "粒/颗", "瓶/支", "包", "克", "毫升", "其他"}));
        mDoseUnitSpinner.setAdapter(doseunitadap);

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
                    }
                })
                .build()
                .get();

    }


    public void compressImage2FileBySize(Bitmap bmp , File file,int kb)
    {
        //压缩尺寸倍数 值越大 ，图片的尺寸就越小
        int ratio = 1 ;
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() /ratio , bmp.getHeight() / ratio ,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result) ;
        Rect rect = new Rect(0,0,bmp.getWidth() / ratio ,bmp.getHeight() / ratio );
        canvas.drawBitmap(bmp,null,rect,null);
        int options = 100;
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        result.compress(Bitmap.CompressFormat.JPEG,100,bos);
        Log.d("is", "正在压缩: "+bos.toByteArray().length/1024);
        while (bos.toByteArray().length / 1024 > kb) {
            Log.d("is", "正在压缩11: "+bos.toByteArray().length/1024);
            bos.reset();//重置baos即清空baos
            if (options <= 10) {
                options -= 3;//每次压缩3%
            } else {
                options -= 10;//每次压缩10%
            }
            if (options <= 0) break;
            result.compress(Bitmap.CompressFormat.JPEG, options, bos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bos.toByteArray());
            Log.d("aa", "compressImage2FileBySize: ");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compressImage2FileBySize(Bitmap bmp , File file)
    {
        //压缩尺寸倍数 值越大 ，图片的尺寸就越小
        int ratio = 4 ;
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() /ratio , bmp.getHeight() / ratio ,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result) ;
        Rect rect = new Rect(0,0,bmp.getWidth() / ratio ,bmp.getHeight() / ratio );
        canvas.drawBitmap(bmp,null,rect,null);

        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        result.compress(Bitmap.CompressFormat.JPEG,100,bos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bos.toByteArray());

            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
