package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.ui.sub_delegates.add_medicineBox.AddMedicineBoxByScanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.model.MedicineModel;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.ui.date.DateDialogUtil;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * author:songwenming
 * Date:2018/12/14
 * Description:
 */
public class MedicineMineEditDelegate extends LatteDelegate {
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
        interval=pos;
    }
    /*    //服药间隔
        @BindView(R2.id.edit_medicine_hand_add_day_interval)
        AppCompatEditText mInterval=null;*/
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
    String medicineImage="";
    //药箱ID
    @BindView(R2.id.spinner_medicine_hand_edit_boxid)
    AppCompatSpinner mBoxidSpinner=null;
    @BindView(R2.id.btn_medicine_hand_edit_submit)
    AppCompatButton mSubmit=null;
    String boxId=null;
    @OnItemSelected(R2.id.spinner_medicine_hand_edit_boxid)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        //Toast.makeText(this.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
        boxId=parent.getItemAtPosition(position).toString();

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



    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_hand_edit;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.d("count",medicineModel.getMedicineCount()+"");;
        mMedicinName.setText(medicineModel.getMedicineName());
        mMedicineCode.setText(medicineModel.getMedicineCode());
        mBtnValidityTimeSelection.setText(medicineModel.getMedicineValidity());
        mMedicineCount.setText(medicineModel.getMedicineCount()+"");
        mBtnStartRemindTimeSelection.setText(medicineModel.getStartRemind());
        mBtnEndRemindTimeSelection.setText(medicineModel.getEndRemind());
        mTimeSpanSpinner.setSelection(medicineModel.getDayInterval());
        mTimesOnDay.setText(medicineModel.getTimesOnDay()+"");
        mMedicineUseCount.setText(medicineModel.getMedicineUseCount()+"");
        Glide.with(getContext())
                .load(UploadConfig.UPLOAD_IMG+medicineModel.getMedicineImage())
                .into(mMedicineImage);
        setSpinnerItemSelectedByValue(mBoxidSpinner,medicineModel.getBoxId());


    }
    public  void setSpinnerItemSelectedByValue(Spinner spinner,String value){
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
//                spinner.setSelection(i,true);// 默认选中项
                spinner.setSelection(i);// 默认选中项

                break;
            }
        }
    }
}
