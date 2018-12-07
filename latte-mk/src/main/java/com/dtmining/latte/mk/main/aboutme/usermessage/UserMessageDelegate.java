package com.dtmining.latte.mk.main.aboutme.usermessage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.ItemType;
import com.dtmining.latte.mk.ui.recycler.MultipleFields;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/12/7
 * Description:
 */
public class UserMessageDelegate extends LatteDelegate {
    String tel=null;
    String boxId=null;
    MultipleRecyclerAdapter userMessageRecyclerViewAdapter;
    ArrayList<MultipleItemEntity> userMessageList=new ArrayList<>();
    @BindView(R2.id.rv_user_message)
    RecyclerView mRecyclerViewMessage=null;
    @BindView(R2.id.srl_user_message)
    SwipeRefreshLayout mRefreshLayout=null;
    private RefreshHandler mRefreshHandler=null;
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
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerViewMessage.setLayoutManager(linearLayoutManager);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userMessageRecyclerViewAdapter= MultipleRecyclerAdapter.create(userMessageList,this.getParentDelegate());
        mRecyclerViewMessage.setAdapter(userMessageRecyclerViewAdapter);
    }
    private void convert_response_to_message(String jsonString){
        if(jsonString!=null) {
            String medicineId = null;
            String medicineName = null;
            String medicineUseTime = null;
            String tel = null;
            String boxId = null;
            String id = null;
            if (jsonString != null) {
                JSONObject jsonobject = JSON.parseObject(jsonString);
                JSONObject jsonobject1 = jsonobject.getJSONObject("detail");
                JSONArray jsonarray = jsonobject1.getJSONArray("histories");
                int size = jsonarray.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonobject2 = jsonarray.getJSONObject(i);
                    boxId = jsonobject2.getString("boxId");
                    medicineName = jsonobject2.getString("medicineNames");
                    medicineUseTime = jsonobject2.getString("medicineUseTime");
                    tel = jsonobject2.getString("tel");
                    id = jsonobject2.getString("id");

                    final MultipleItemEntity entity = MultipleItemEntity.builder()
                            .setField(MultipleFields.ITEM_TYPE, ItemType.TEXT_TEXT)
                            .setField(MultipleFields.SPAN_SIZE, 3)
                            .setField(MultipleFields.MEDICINE_NAME, medicineName)
                            .setField(MultipleFields.MEDICINEUSERTIME, medicineUseTime)
                            .setField(MultipleFields.BOXID, boxId)
                            .setField(MultipleFields.TEL, tel)
                            .setField(MultipleFields.ID, id)
                            .build();
                    userMessageList.add(entity);
                }
            }
        }
    }
}
