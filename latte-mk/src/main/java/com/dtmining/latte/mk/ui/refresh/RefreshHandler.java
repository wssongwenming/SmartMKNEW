package com.dtmining.latte.mk.ui.refresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;


public class RefreshHandler implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener{
    private final SwipeRefreshLayout REFESH_LAYOUT;
    private final PagingBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private MultipleRecyclerAdapter mAdapter=null;
    private final DataConverter CONVERTER;
    private final LatteDelegate DELEGATE;
    private RefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                           RecyclerView recyclerView,
                           DataConverter converter,PagingBean pagingBean,LatteDelegate delegate){
        this.REFESH_LAYOUT=swipeRefreshLayout;
        this.RECYCLERVIEW=recyclerView;
        this.CONVERTER=converter;
        this.BEAN=pagingBean;
        this.DELEGATE=delegate;
       REFESH_LAYOUT.setOnRefreshListener(this);
    }

    public static RefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                        RecyclerView recyclerView,
                                        DataConverter converter,LatteDelegate delegate){
        return new RefreshHandler(swipeRefreshLayout,recyclerView,converter,new PagingBean(),delegate);

    }

    private void refresh(){
        REFESH_LAYOUT.setRefreshing(true);
        Latte.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //可以进行网络请求，REFESH_LAYOUT.setRefreshing(false);可以放入网络请求的success回调
                REFESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }
    public void firstPage_medicine_history(String url){
        //BEAN.setDelayed(1000);
        RestClient.builder()
                .url(url)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(Latte.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                       // final JSONObject object=JSON.parseObject(response);
                        /*BEAN.setTotal(object.getInteger("total"))
                                .setPageSize(object.getInteger("page_size"));*/
                        //设置Adapter
                        mAdapter=MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response),DELEGATE);
                        mAdapter.setOnLoadMoreListener(RefreshHandler.this,RECYCLERVIEW);
                        RECYCLERVIEW.setAdapter(mAdapter);
                        BEAN.addIndex();
                    }
                })
                .build()
                .get();

    }
    public void firstPage_medicine_plan(String url){
        //BEAN.setDelayed(1000);
        RestClient.builder()
                .url(url)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(Latte.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        // final JSONObject object=JSON.parseObject(response);
                        /*BEAN.setTotal(object.getInteger("total"))
                                .setPageSize(object.getInteger("page_size"));*/
                        //设置Adapter
                        mAdapter=MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response),DELEGATE);
                        mAdapter.setOnLoadMoreListener(RefreshHandler.this,RECYCLERVIEW);
                        RECYCLERVIEW.setAdapter(mAdapter);
                        BEAN.addIndex();
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
