package com.dtmining.latte.ui.refresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;

import com.dtmining.latte.app.Latte;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.ui.recycler.DataConverter;
import com.dtmining.latte.ui.recycler.MultipleRecyclerAdapter;


public class RefreshHandler implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener{
    private final SwipeRefreshLayout REFESH_LAYOUT;
    private final PagingBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private MultipleRecyclerAdapter mAdapter=null;
    private final DataConverter CONVERTER;

    public RefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                          RecyclerView recyclerView,
                          DataConverter converter,PagingBean pagingBean){
        this.REFESH_LAYOUT=swipeRefreshLayout;
        this.RECYCLERVIEW=recyclerView;
        this.CONVERTER=converter;
        this.BEAN=pagingBean;
        REFESH_LAYOUT.setOnRefreshListener(this);
    }

    public static RefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                        RecyclerView recyclerView,
                                        DataConverter converter){
        return new RefreshHandler(swipeRefreshLayout,recyclerView,converter,new PagingBean());

    }

    private void refresh(){
        REFESH_LAYOUT.setRefreshing(true);
        Latte.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //在这里进行网络,把FESH_LAYOUT.setRefreshing(false)放入网络请求的success回调中
                REFESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }
    public void firstPage(String url){
//        BEAN.setDelayed(1000);
        RestClient.builder()
                .url(url)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(Latte.getApplicationContext(),response, Toast.LENGTH_LONG).show();
/*                        final JSONObject object=JSON.parseObject(response);
                        BEAN.setTotal(object.getInteger("total"))
                                .setPageSize(object.getInteger("page_size"));
                        //设置Adapter
                        mAdapter=MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response));
                        mAdapter.setOnLoadMoreListener(RefreshHandler.this,RECYCLERVIEW);
                        RECYCLERVIEW.setAdapter(mAdapter);
                        BEAN.addIndex();*/
                    }
                })
                .build()
                .get();

    }
    @Override
    public void onRefresh() {
        refresh();

    }

    @Override
    public void onLoadMoreRequested() {

    }
}
