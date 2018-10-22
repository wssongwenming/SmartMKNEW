package com.dtmining.latte.mk.main.aboutme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.aboutme.list.ListAdapter;
import com.dtmining.latte.mk.main.aboutme.list.ListBean;
import com.dtmining.latte.mk.main.aboutme.list.ListItemType;
import com.dtmining.latte.mk.main.aboutme.order.OrderListDelegate;
import com.dtmining.latte.mk.main.aboutme.profile.UserProfileDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/10/10
 * Description:
 */
public class AboutMeDelegate extends BottomItemDelegate{
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
        getParentDelegate().start(new UserProfileDelegate());
    }
    private void startOrderListByType(){
        final OrderListDelegate delegate=new OrderListDelegate();
        delegate.setArguments(mArgs);
        getParentDelegate().start(delegate);
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
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
        ListBean medhistory=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(3)
                .setText("用药记录")
                .setImage(R.drawable.self_history)
                .build();
        ListBean message=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(4)
                .setText("用户消息")
                .setImage(R.drawable.self_message)
                .build();
        ListBean feedback=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(5)
                .setText("反馈")
                .setImage(R.drawable.self_reward)
                .build();
        ListBean cacheclean=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(6)
                .setText("清除缓存")
                .setImage(R.drawable.self_clean)
                .build();
        ListBean signout=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_WITH_IMAGE)
                .setId(7)
                .setText("退出")
                .setImage(R.drawable.self_exit)
                .build();
        final List<ListBean>data=new ArrayList<>();
        data.add(boxadd);
        data.add(minebox);
        data.add(medhistory);
        data.add(message);
        data.add(feedback);
        data.add(cacheclean);
        data.add(signout);
        //设置RecyclerView
        final LinearLayoutManager manager=new LinearLayoutManager(getContext());
        mRvSettings.setLayoutManager(manager);
        final ListAdapter adapter=new ListAdapter(data,this.getParentDelegate());
        mRvSettings.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs=new Bundle();
    }
}
