package com.dtmining.latte.mk.main.aboutme;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.list.ListAdapter;
import com.dtmining.latte.mk.main.aboutme.list.ListBean;
import com.dtmining.latte.mk.main.aboutme.list.ListItemType;
import com.dtmining.latte.mk.main.aboutme.order.OrderListDelegate;
import com.dtmining.latte.mk.main.aboutme.profile.UploadConfig;
import com.dtmining.latte.mk.main.aboutme.profile.UserProfileClickListener;
import com.dtmining.latte.mk.main.aboutme.profile.UserProfileDelegate;
import com.dtmining.latte.mk.sign.ISignListener;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * author:songwenming
 * Date:2018/10/10
 * Description:
 */
public class AboutMeDelegate extends BottomItemDelegate implements  UserProfileClickListener.RefreshListener{
    private String tel;
    @BindView(R2.id.tv_about_me_username)
    AppCompatTextView mUserName=null;
    @BindView(R2.id.img_user_avatar)
    CircleImageView mUserImgView=null;
    @BindView(R2.id.rv_personal_setting)
    RecyclerView mRvSettings=null;
    public static final String ORDER_TYPE="ORDER_TYPE";
    private Bundle mArgs=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_personal;
    }
    @OnClick(R2.id.img_user_avatar)
    void onClickAvatar(){
        UserProfileDelegate userProfileDelegate=new UserProfileDelegate();
        getParentDelegate().start(userProfileDelegate);
        Latte.getConfigurator().withDelegate(AboutMeDelegate.this);
    }

    private ISignListener mISignListener=null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISignListener)
        {
            mISignListener=(ISignListener) activity;
        }
    }
    private static final RequestOptions REQUEST_OPTIONS=
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
        ListBean boxadd=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(1)
                .setText("添加药箱")
                .setImage(R.drawable.self_add)
                .build();
        ListBean minebox=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(2)
                .setText("我的药箱")
                .setImage(R.drawable.self_mine)
                .build();
        ListBean currentbox=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(3)
                .setText("绑定当前药箱")
                .setImage(R.drawable.current_box)
                .build();
        ListBean deletebox=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(4)
                .setText("删除已有药箱")
                .setImage(R.drawable.current_box)
                .build();
        ListBean medhistory=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(5)
                .setText("用药记录")
                .setImage(R.drawable.self_history)
                .build();
        ListBean message=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(6)
                .setText("用户消息")
                .setImage(R.drawable.self_message)
                .build();
        ListBean feedback=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(7)
                .setText("反馈")
                .setImage(R.drawable.self_reward)
                .build();
        ListBean cacheclean=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(8)
                .setText("清除缓存")
                .setImage(R.drawable.self_clean)
                .build();
        ListBean signout=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(9)
                .setText("退出")
                .setImage(R.drawable.self_exit)
                .build();
        final List<ListBean>data=new ArrayList<>();
        data.add(boxadd);
        data.add(minebox);
        data.add(currentbox);
        //data.add(deletebox);
        data.add(medhistory);
        data.add(message);
        data.add(feedback);
        data.add(cacheclean);
        data.add(signout);

        //设置RecyclerView
        final LinearLayoutManager manager=new LinearLayoutManager(getContext());
        mRvSettings.setLayoutManager(manager);
        final ListAdapter adapter=new ListAdapter(data,this.getParentDelegate(),mISignListener,tel);
        mRvSettings.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs=new Bundle();
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
                               JSONObject detail=object.getJSONObject("detail");
                               String userName=detail.getString("username");
                               if(userName!=null) {
                                   mUserName.setText(userName);
                               }else
                               {
                                   mUserName.setText("用户："+tel);

                               }
                               String userImgUrl= UploadConfig.UPLOAD_IMG+detail.getString("userImage");
                               if(!userImgUrl.contains("default.png")) {
                                   Log.d("img", userImgUrl);
                                   Glide.with(getContext())
                                           .load(userImgUrl)
                                           .apply(REQUEST_OPTIONS)
                                           .into(mUserImgView);
                               }

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
