package com.dtmining.latte.mk.ui.sub_delegates.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.ListView;
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
    private List<MedicineState> checkedList=new ArrayList<>();



    private String boxId;
    private String tel;
    private Context context;
    private ClickListenerInterface mclickListenerInterface;


    @Override
    public void itemChecked(MedicineState checkBean, boolean isChecked) {

    }

    public interface ClickListenerInterface {
        public void doConfirm(ArrayList<MedicineState> input);
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
                .url("medicine_mine")
                .params("tel",tel)
                .params("boxId",boxId)
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
                                final String medicineName = data.getString("medicineName");
                                final String medicine_img_url = data.getString("medicine_img_url");
                                final String boxId = data.getString("boxId");
                                final int medicinePause = data.getInteger("medicinePause");
                                MedicineState medicineState=new MedicineState();
                                medicineState.setMedicineId(medicineId);
                                medicineState.setBoxId(boxId);
                                medicineState.setMedicineName(medicineName);
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
/*    private void get_medicine(){
        RestClient.builder()
                .url("medicine_mine")
                .params("tel",tel)
                .params("boxId",boxId)
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
                                final String medicineName = data.getString("medicineName");
                                final String medicine_img_url = data.getString("medicine_img_url");
                                final String boxId = data.getString("boxId");
                                final int medicinePause = data.getInteger("medicinePause");
                                MedicineState medicineState=new MedicineState();
                                medicineState.setMedicineId(medicineId);
                                medicineState.setBoxId(boxId);
                                medicineState.setMedicineName(medicineName);
                                medicines.add(medicineState);

                            }

                        }
                    }
                })
                .build()
                .get();
    }*/

   /* public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.medicine_select_check_dialog, null);
        setContentView(view);

        //get_medicine();
        btnConfim= (Button) findViewById(R.id.btn_add_plan_by_time_confirm);
        checkBoxAdapter=new CheckBoxAdapter(context,medicines);
        drugs_lv= (ListView) findViewById(R.id.add_plan_by_time_drugs_lv);
        drugs_lv.setAdapter(checkBoxAdapter);
        drugs_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        btnConfim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }*/


}
