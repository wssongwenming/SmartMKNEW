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
    @OnClick(R2.id.tv_all_order)
    void onClickAllOrder(){
        mArgs.putString(ORDER_TYPE,"all");
        startOrderListByType();
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
       final ListBean address=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_NORMAL)
                .setId(1)
                .setText("收获地址")
                .build();
        ListBean system=new ListBean.Builder()
                .setItemType(ListItemType.ITEM_NORMAL)
                .setId(2)
                .setText("系统设置")
                .build();
        final List<ListBean>data=new ArrayList<>();
        data.add(address);
        data.add(system);
        //设置RecyclerView
        final LinearLayoutManager manager=new LinearLayoutManager(getContext());
        mRvSettings.setLayoutManager(manager);
        final ListAdapter adapter=new ListAdapter(data);
        mRvSettings.setAdapter(adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs=new Bundle();
    }
}
