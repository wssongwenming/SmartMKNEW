package com.dtmining.latte.mk.ui.sub_delegates.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.adapter.CheckAdapter;
import com.dtmining.latte.mk.adapter.CheckBoxAdapter;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.model.MedicineState;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class CheckMedicinesDialog extends Dialog implements CheckAdapter.CheckItemListener{


    //适配器
    private CheckAdapter mCheckAdapter;
    //列表
    private RecyclerView check_rcy;
    //全选操作
    private CheckBox check_all_cb;
    //列表数据
    private List<MedicineState> medicines=new ArrayList<>();
    private List<MedicineState> checkedMedicine=new ArrayList<>();
    private List<Integer>checkedPosition=new ArrayList<>();
    private List<String>checkedUseCount=new ArrayList<>();
    private List<String>doseUnits=new ArrayList<>();



    private String boxId;
    private String tel;
    private Context context;
    private ClickListenerInterface mclickListenerInterface;
    private Button button=null;

    @Override
    public void itemChecked(List<Integer> seletor,List<MedicineState> seletorString) {
        //处理Item点击选中回调事件
        checkedPosition=seletor;

        checkedMedicine=seletorString;
        //Toast.makeText(getContext(), checkedPosition+"",Toast.LENGTH_LONG).show();
    }

    public interface ClickListenerInterface {
        public void doConfirm(List<MedicineState> input,List<String>useCount);
    }

    public CheckMedicinesDialog(Context context , ClickListenerInterface clickListenerInterface) {
        super(context, R.style.Theme_MYDialog);
        this.context = context;
        mclickListenerInterface=clickListenerInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_select_check_dialog);
        button= (Button) findViewById(R.id.btn_add_plan_by_time_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUseCount()){
                    getUseCount();
                    mclickListenerInterface.doConfirm(checkedMedicine,checkedUseCount);
                }
            }
        });
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId= LattePreference.getBoxId();
        if(userProfile==null){

        }else {
            tel=Long.toString(userProfile.getTel());

        }
        initDatas();
        //initViews();
    }

    private void initDatas(){
        RestClient.builder()
                .url(UploadConfig.API_HOST+"/api/get_medicine_of_box")
                .clearParams()
                .params("tel",tel)
                .params("boxId",LattePreference.getBoxId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if(response!=null) {
                            final JSONObject jsonObject = JSON.parseObject(response);
                            String tel = jsonObject.getString("tel");
                            final JSONArray dataArray = jsonObject.getJSONArray("detail");
                            final int size = dataArray.size();
                            for (int i = 0; i < size; i++) {
                                JSONObject data = (JSONObject) dataArray.get(i);
                                final String medicineId = data.getString("medicineId");
                                final int medicineCount = data.getInteger("medicineCount");
                                final int medicineType  =data.getIntValue("medicineType");
                                final String medicineName = data.getString("medicineName");
                                final String medicine_img_url = data.getString("medicine_img_url");
                                final String boxId = data.getString("boxId");
                                final int medicinePause = data.getInteger("medicinePause");
                                String doseUnit="";
                                switch (medicineType) {
                                    case 0:
                                        doseUnit = "片";
                                        break;
                                    case 1:
                                        doseUnit = "粒/颗";
                                        break;
                                    case 2:
                                        doseUnit = "瓶/支";
                                        break;
                                    case 3:
                                        doseUnit = "包";
                                        break;
                                    case 4:
                                        doseUnit = "克";
                                        break;
                                    case 5:
                                        doseUnit = "毫升";
                                        break;
                                    case 6:
                                        doseUnit = "其他";
                                        break;
                                }
                                MedicineState medicineState=new MedicineState();
                                medicineState.setMedicineId(medicineId);
                                medicineState.setBoxId(boxId);
                                medicineState.setMedicineName(medicineName);
                                medicineState.setDoseUnit(doseUnit);
                                medicines.add(medicineState);

                            }
                            initViews();
                        }
                    }
                })
                .build()
                .get();
    }
    private void initViews(){
        check_rcy = (RecyclerView) findViewById(R.id.add_plan_by_time_check_rcy);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        check_rcy.setLayoutManager(linearLayoutManager);

        mCheckAdapter = new CheckAdapter(getContext(), medicines, this);
        check_rcy.setAdapter(mCheckAdapter);

    }

    private void getUseCount(){
        for (int i = 0; i < checkedPosition.size(); i++) {
            int positions=checkedPosition.get(i);
            LinearLayoutCompat layout = (LinearLayoutCompat)  check_rcy.getChildAt(positions);
            AppCompatEditText et_content = (AppCompatEditText) layout.findViewById(R.id.et_add_plan_by_time_medicine_usecount);
            checkedUseCount.add(et_content.getText().toString());
        }
    }

    private boolean checkUseCount(){
        boolean isPass=true;
        for (int i = 0; i < checkedPosition.size(); i++) {
            int position=checkedPosition.get(i);
            LinearLayoutCompat layout = (LinearLayoutCompat)  check_rcy.getChildAt(position);
            AppCompatEditText et_content = (AppCompatEditText) layout.findViewById(R.id.et_add_plan_by_time_medicine_usecount);
            if(et_content.getText().toString().isEmpty())
            {
                et_content.setError("请输入用药量");
                isPass=false;
            }
            else
            {
                et_content.setError(null);
            }
        }
        return  isPass;
    }
}
