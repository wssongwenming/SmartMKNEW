package com.dtmining.latte.mk.ui.refresh;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.Set;


public class RefreshHandler1 implements  BaseQuickAdapter.RequestLoadMoreListener{
    private Set<SwipeListLayout> SETS =null;
    private final PagingBean BEAN;
    private final RecyclerView RECYCLERVIEW;

    private final DataConverter CONVERTER;
    private final LatteDelegate DELEGATE;
    public RefreshHandler1(SwipeRefreshLayout swipeRefreshLayout,
                           RecyclerView recyclerView,
                           DataConverter converter, PagingBean pagingBean, LatteDelegate delegate, Set<SwipeListLayout> sets){

        this.RECYCLERVIEW=recyclerView;
        this.CONVERTER=converter;
        this.BEAN=pagingBean;
        this.DELEGATE=delegate;
        this.SETS=sets;
          }

    public static RefreshHandler1 create(SwipeRefreshLayout swipeRefreshLayout,
                                         RecyclerView recyclerView,
                                         DataConverter converter, LatteDelegate delegate, Set<SwipeListLayout> sets){
        return new RefreshHandler1(swipeRefreshLayout,recyclerView,converter,new PagingBean(),delegate,sets);

    }

    public void firstPage_medicine_history(String url){
        //BEAN.setDelayed(1000);
        RestClient.builder()
                .url(url)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        //Toast.makeText(Latte.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                       // final JSONObject object=JSON.parseObject(response);
                        /*BEAN.setTotal(object.getInteger("total"))
                                .setPageSize(object.getInteger("page_size"));*/
                        //设置Adapter
                        MultipleRecyclerAdapter mAdapter=MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response),DELEGATE);
                        mAdapter.setOnLoadMoreListener(RefreshHandler1.this,RECYCLERVIEW);
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
                        //Toast.makeText(Latte.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        // final JSONObject object=JSON.parseObject(response);
                        /*BEAN.setTotal(object.getInteger("total"))
                                .setPageSize(object.getInteger("page_size"));*/
                        //设置Adapter
                        //mAdapter=MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response),DELEGATE);
                       // mAdapter.setOnLoadMoreListener(RefreshHandler.this,RECYCLERVIEW);
                      //  RECYCLERVIEW.setAdapter(mAdapter);
                        BEAN.addIndex();
                    }
                })
                .build()
                .get();

    }

 /*   public void firstPage_medicine_mine(String url){
        BEAN.setDelayed(1000);
        RestClient.builder()
                .url(url)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        //Toast.makeText(Latte.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        MedicineMineRecyclerAdapter mAdapter= MedicineMineRecyclerAdapter.create(CONVERTER.setJsonData(response),SETS);
                        mAdapter.setOnLoadMoreListener(RefreshHandler1.this,RECYCLERVIEW);
                        RECYCLERVIEW.setAdapter(mAdapter);
                        BEAN.addIndex();
                    }
                })
                .build()
                .get();

    }*/

    @Override
    public void onLoadMoreRequested() {

    }
}
