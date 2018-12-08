package com.dtmining.latte.mk.main.aboutme.usermessage;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;

import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.refresh.PagingBean;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.Set;

/**
 * author:songwenming
 * Date:2018/12/7
 * Description:
 */
public class MessageRefreshHandler implements SwipeRefreshLayout.OnRefreshListener{
    private final SwipeRefreshLayout REFESH_LAYOUT;
    private  MultipleRecyclerAdapter mAdapter=null;
    private final DataConverter CONVERTER;
    private final LatteDelegate DELEGATE;
    private final RecyclerView RECYCLERVIEW;

    public MessageRefreshHandler(SwipeRefreshLayout refeshlayout,RecyclerView recyclerView,DataConverter dataConverter,LatteDelegate delegate) {
        this.RECYCLERVIEW=recyclerView;
        this.REFESH_LAYOUT = refeshlayout;
        this.CONVERTER=dataConverter;
        this.DELEGATE=delegate;
        if(REFESH_LAYOUT!=null) {
            REFESH_LAYOUT.setOnRefreshListener(this);
        }
    }
    public static MessageRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,DataConverter converter, LatteDelegate delegate){
        return new MessageRefreshHandler(swipeRefreshLayout,recyclerView,converter,delegate);
    }
    @Override
    public void onRefresh() {
        refresh();
    }
    private void refresh(){
        if(REFESH_LAYOUT!=null)
            REFESH_LAYOUT.setRefreshing(true);
        Latte.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //可以进行网络请求，REFESH_LAYOUT.setRefreshing(false);可以放入网络请求的success回调
                REFESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }
    public void getUserMessage(String tel){
                             //设置Adapter
                        mAdapter= MultipleRecyclerAdapter.create(CONVERTER.getEntities(),DELEGATE);
                        RECYCLERVIEW.setAdapter(mAdapter);
                    }

    }

