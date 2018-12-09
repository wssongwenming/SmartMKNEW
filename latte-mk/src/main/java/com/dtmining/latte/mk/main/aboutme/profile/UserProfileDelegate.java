package com.dtmining.latte.mk.main.aboutme.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.list.ListAdapter;
import com.dtmining.latte.mk.main.aboutme.list.ListBean;
import com.dtmining.latte.mk.main.aboutme.list.ListItemType;
import com.dtmining.latte.mk.main.aboutme.settings.NameDelegate;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history.MedicineReactionDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class UserProfileDelegate  extends LatteDelegate implements NameDelegate.RefreshListener  {
    String tel=null;
    final List<ListBean> data = new ArrayList<>();
    UserInfo userInfo=new UserInfo();
     ListAdapter adapter;
    @BindView(R2.id.rv_user_profile)
    RecyclerView recyclerView=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_user_profile;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
        final ListBean image = new ListBean.Builder()
                .setItemType(ListItemType.ITEM_AVATAR)
                .setId(1)
                .setImageUrl("http://i9.qhimg.com/t017d891ca365ef60b5.jpg")
                .build();

        final ListBean name = new ListBean.Builder()
                .setItemType(ListItemType.ITEM_NORMAL)
                .setId(2)
                .setText("姓名")
                .setDelegate(new NameDelegate())
                .setValue("未设置姓名")
                .build();

        final ListBean gender = new ListBean.Builder()
                .setItemType(ListItemType.ITEM_NORMAL)
                .setId(3)
                .setText("性别")
                .setValue("未设置性别")
                .build();

        final ListBean birth = new ListBean.Builder()
                .setItemType(ListItemType.ITEM_NORMAL)
                .setId(4)
                .setText("生日")
                .setValue("未设置生日")
                .build();
        final ListBean role=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_NORMAL)
                .setId(5)
                .setText("用户角色")
                .setValue("未设置角色")
                .build();
        final ListBean TEL=new ListBean.Builder()
                .setItemType(ListItemType.TEL)
                .setValue(tel)
                .setId(6)
                .build();
        data.add(image);
        data.add(name);
        data.add(gender);
        data.add(birth);
        data.add(role);

        userInfo.setBirthday("未设置生日");
        userInfo.setGender("未设置性别");
        userInfo.setRole("未设置角色");
        userInfo.setTel(tel);
        userInfo.setUser_image("http://i9.qhimg.com/t017d891ca365ef60b5.jpg");
        userInfo.setUsername("未设置姓名");
        //设置RecyclerView
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new ListAdapter(data,this.getParentDelegate(),null,null);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new UserProfileClickListener(this,userInfo));
    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getUserInfo();
    }
    private void getUserInfo()
    {
        RestClient.builder()
                .clearParams()
                .url(UploadConfig.API_HOST+"/api/get_userinfo")
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if(response!=null){
                            JSONObject object= JSON.parseObject(response);
                            int code=object.getIntValue("code");
                            if(code==1){
                                Log.d("res1", object.toJSONString());
                                JSONObject detail=object.getJSONObject("detail");
                                String birthday=detail.getString("birthday");
                                String role=detail.getString("role");
                                String userImageUrl=detail.getString("userImage");
                                String gender=detail.getString("gender");
                                String tel=detail.getString("tel");
                                String userName=detail.getString("username");
                                if(userName!=null) {
                                    ((ListBean)data.get(1)).setValue(userName);
                                    userInfo.setUsername(userName);
                                }
                                if(gender!=null){
                                    ((ListBean)data.get(2)).setValue(gender);
                                    userInfo.setGender(gender);
                                }
                                if(birthday!=null)
                                {
                                    ((ListBean)data.get(3)).setValue(birthday);
                                    userInfo.setBirthday(birthday);
                                }
                                if(role!=null)
                                {
                                    ((ListBean)data.get(4)).setValue(role);
                                    userInfo.setRole(role);
                                }

                                if(!userImageUrl.contains("/images/default.png"))
                                {
                                    ((ListBean)data.get(0)).setImageUrl(UploadConfig.UPLOAD_IMG+userImageUrl);
                                    userInfo.setUser_image(userImageUrl);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .build()
                .get();
    }

    @Override
    public void onRefresh() {
        getUserInfo();
    }
}