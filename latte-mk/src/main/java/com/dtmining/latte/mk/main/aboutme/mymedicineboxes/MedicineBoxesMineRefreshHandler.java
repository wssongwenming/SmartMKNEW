package com.dtmining.latte.mk.main.aboutme.mymedicineboxes;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
 * Date:2018/11/4
 * Description:
 */
public class MedicineBoxesMineRefreshHandler implements SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {
    private final SwipeRefreshLayout REFESH_LAYOUT;
    private Set<SwipeListLayout> SETS =null;
    private final PagingBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private final DataConverter CONVERTER;
    private final LatteDelegate DELEGATE;

    public MedicineBoxesMineRefreshHandler(SwipeRefreshLayout refesh_layout, PagingBean bean, RecyclerView recyclerview, DataConverter converter, LatteDelegate delegate) {
        REFESH_LAYOUT = refesh_layout;
        BEAN = bean;
        RECYCLERVIEW = recyclerview;
        CONVERTER = converter;
        DELEGATE = delegate;
        if(REFESH_LAYOUT!=null) {
            REFESH_LAYOUT.setOnRefreshListener(this);
        }
    }
    public static MedicineBoxesMineRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                        RecyclerView recyclerView,
                                        DataConverter converter, LatteDelegate delegate){
        return new MedicineBoxesMineRefreshHandler(swipeRefreshLayout,new PagingBean(),recyclerView,converter,delegate);

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

    public void firstPage_medicine_boxes(String url,String tel){
        //BEAN.setDelayed(1000);

        RestClient.builder()
                .url(url)
                .clearParams()
                .params("tel",tel)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        final JSONObject object= JSON.parseObject(response);
                        MedicineBoxesMineAdapter mAdapter=MedicineBoxesMineAdapter.create(CONVERTER.setJsonData(response),DELEGATE);
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
