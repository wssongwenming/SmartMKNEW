package com.dtmining.latte.mk.main.aboutme.usermessage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.index.IndexDataConverter;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
/**
 * author:songwenming
 * Date:2018/12/7
 * Description:
 */
public class UserMessageDelegate  extends LatteDelegate  {
    String tel=null;
    String boxId=null;
    DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    MultipleRecyclerAdapter useMessageRecyclerViewAdapter;
    ArrayList<MultipleItemEntity> useMessageList=new ArrayList<>();

    MultipleRecyclerAdapter overDueMessageRecyclerViewAdapter;
    ArrayList<MultipleItemEntity> overDueList=new ArrayList<>();

    MultipleRecyclerAdapter supplyMessageRecyclerViewAdapter;
    ArrayList<MultipleItemEntity> supplyMessageList=new ArrayList<>();

    @BindView(R2.id.rv_use_message)
    RecyclerView mRecyclerViewUseMessage=null;

    @BindView(R2.id.rv_supply_message)
    RecyclerView mRecyclerViewSupplyMessage=null;

    @BindView(R2.id.rv_overdue_message)
    RecyclerView mRecyclerViewOverDueMessage=null;

    @BindView(R2.id.srl_user_message)
    SwipeRefreshLayout mRefreshLayout=null;
    private MessageRefreshHandler mRefreshHandler=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_user_message;
    }
    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_red_light
        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(mRefreshLayout!=null) {
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }
            });
        }
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
            boxId= LattePreference.getBoxId();
            if(boxId.equalsIgnoreCase("未设置boxId"))
            {
                Toast.makeText(getContext(),"请添加药箱，并绑定当前药箱",Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        initRefreshLayout();
        get_medicine_use_message(tel);
        get_medicine_supply_message(tel);
        get_medicine_overdue_message(tel);
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(getContext());
        mRecyclerViewUseMessage.setLayoutManager(linearLayoutManager);
        mRecyclerViewSupplyMessage.setLayoutManager(linearLayoutManager1);
        mRecyclerViewOverDueMessage.setLayoutManager(linearLayoutManager2);


    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        useMessageRecyclerViewAdapter= MultipleRecyclerAdapter.create(useMessageList,this.getParentDelegate());
        mRecyclerViewUseMessage.setAdapter(useMessageRecyclerViewAdapter);

        overDueMessageRecyclerViewAdapter=MultipleRecyclerAdapter.create(overDueList,this.getParentDelegate());
        mRecyclerViewOverDueMessage.setAdapter(overDueMessageRecyclerViewAdapter);

        supplyMessageRecyclerViewAdapter=MultipleRecyclerAdapter.create(supplyMessageList,this.getParentDelegate());
        mRecyclerViewSupplyMessage.setAdapter(supplyMessageRecyclerViewAdapter);
    }
    private void get_medicine_use_message(final String tel) {
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine_use_message")
                //.url(UploadConfig.API_HOST+"/api/get_medicine_use_message")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if(response!=null){
                            JSONObject object=JSON.parseObject(response);
                            int code= object.getIntValue("code");
                            if(code==1){
                                StringBuilder plansbuilder=new StringBuilder();
                                JSONObject detail= object.getJSONObject("detail");
                                JSONArray  planlist=detail.getJSONArray("planlist");
                                plansbuilder.append("请于");
                                int size=planlist.size();
                                for (int i = 0; i <size ; i++) {
                                    JSONObject planlist_i= (JSONObject) planlist.get(i);
                                    String useTime=planlist_i.getString("time");
                                    plansbuilder.append(useTime+"服用");
                                    JSONArray plans=planlist_i.getJSONArray("plans");
                                    int size1=plans.size();
                                    for (int j = 0; j <size1 ; j++){
                                        JSONObject plan=(JSONObject) plans.get(j);
                                        String boxId=plan.getString("boxId");
                                        String medicineName=plan.getString("medicineName");
                                        int medicineUseCount=plan.getIntValue("medicineUseCount");
                                        int medicineType=plan.getIntValue("medicineType");
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
                                        plansbuilder.append("药盒"+boxId+"内:"+medicineName+medicineUseCount+doseUnit);
                                    }
                                }
                                final MultipleItemEntity entity=MultipleItemEntity.builder()
                                        .setField(MultipleFields.ITEM_TYPE, ItemType.MEDICINE_USE_MESSAGE)
                                        .setField(MultipleFields.MEDICINE_USE_MESSAGE_TITLE,"用药提醒")
                                        .setField(MultipleFields.MEDICINE_USE_MESSAGE_TIME,format1.format(new Date()))
                                        .setField(MultipleFields.MEDICINE_USE_MESSAGE_CONTENT,plansbuilder.toString())
                                        .build();
                                useMessageList.add(entity);
                                useMessageRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .build()
                .get();
    }

    private void get_medicine_supply_message(final String tel){
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine_supply_message")
                //.url(UploadConfig.API_HOST+"/api/get_medicine_supply_message")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if(response!=null){
                            JSONObject object=JSON.parseObject(response);
                            int code= object.getIntValue("code");
                            if(code==1){
                                StringBuilder overduebuilder=new StringBuilder();
                                JSONArray detail= object.getJSONArray("detail");
                                int size=detail.size();
                                for (int i = 0; i <size ; i++) {
                                    JSONObject object1= (JSONObject) detail.get(i);
                                    String boxId= (String) object1.getString("boxId");
                                    String medicineName=object1.getString("medicineName");
                                    int medicineCount=object1.getIntValue("medicineCount");
                                    overduebuilder.append("药箱"+boxId+":"+medicineName+"剩余量为"+medicineCount);
                                }
                                overduebuilder.append("请注意补充");
                                final MultipleItemEntity entity=MultipleItemEntity.builder()
                                        .setField(MultipleFields.ITEM_TYPE, ItemType.MEDICINE_SUPPLY_MESSAGE)
                                        .setField(MultipleFields.MEDICINE_SUPPLY_MESSAGE_TITLE,"药品补充")
                                        .setField(MultipleFields.MEDICINE_SUPPLY_MESSAGE_TIME,format1.format(new Date()))
                                        .setField(MultipleFields.MEDICINE_SUPPLY_MESSAGE_CONTENT,overduebuilder.toString())
                                        .build();
                                supplyMessageList.add(entity);
                                supplyMessageRecyclerViewAdapter.notifyDataSetChanged();

                            }
                        }

                    }
                })
                .build()
                .get();
    }
    private void get_medicine_overdue_message(String tel){
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_medicine_over_due_message")
                //.url(UploadConfig.API_HOST+"/api/get_medicine_over_due_message")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if(response!=null){
                            JSONObject object=JSON.parseObject(response);
                            int code= object.getIntValue("code");
                            if(code==1){
                                StringBuilder overduebuilder=new StringBuilder();
                                overduebuilder.append("如下药品已经或即将过期");
                                JSONArray detail= object.getJSONArray("detail");
                                int size=detail.size();
                                for (int i = 0; i <size ; i++) {
                                    JSONObject object1= (JSONObject) detail.get(i);
                                    String boxId= (String) object1.getString("boxId");
                                    String medicineName=object1.getString("medicineName");
                                    overduebuilder.append("药箱"+boxId+":");
                                    overduebuilder.append(medicineName);
                                }
                                final MultipleItemEntity entity=MultipleItemEntity.builder()
                                        .setField(MultipleFields.ITEM_TYPE, ItemType.MEDICINE_OVERDUE_MESSAGE)
                                        .setField(MultipleFields.MEDICINE_OVERDUE_MESSAGE_TITLE,"药品过期")
                                        .setField(MultipleFields.MEDICINE_OVERDUE_MESSAGE_TIME,format1.format(new Date()))
                                        .setField(MultipleFields.MEDICINE_OVERDUE_MESSAGE_CONTENT,overduebuilder.toString())
                                        .build();
                                overDueList.add(entity);
                                overDueMessageRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .build()
                .get();

    }
    private void refresh(){
        if(mRefreshLayout!=null)
            mRefreshLayout.setRefreshing(true);
        Latte.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                useMessageList.clear();
                supplyMessageList.clear();
                overDueList.clear();
                get_medicine_use_message(tel);
                get_medicine_supply_message(tel);
                get_medicine_overdue_message(tel);
                //可以进行网络请求，REFESH_LAYOUT.setRefreshing(false);可以放入网络请求的success回调
                mRefreshLayout.setRefreshing(false);
            }
        },2000);
    }
}
