package com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue;

import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.refresh.PagingBean;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;
import com.dtmining.latte.util.storage.LattePreference;

/**
 * author:songwenming
 * Date:2018/11/19
 * Description:
 */
public class MedicineOverdueRefreshHandler implements SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener {
    private final SwipeRefreshLayout REFESH_LAYOUT;
    private final RecyclerView RECYCLERVIEW;
    private MultipleRecyclerAdapter mAdapter=null;
    private final DataConverter CONVERTER;
    private final LatteDelegate DELEGATE;
    public MedicineOverdueRefreshHandler(SwipeRefreshLayout REFESH_LAYOUT,RecyclerView RECYCLERVIEW, DataConverter CONVERTER,LatteDelegate delegate) {
        this.REFESH_LAYOUT = REFESH_LAYOUT;
        this.RECYCLERVIEW = RECYCLERVIEW;
        this.CONVERTER = CONVERTER;
        this.DELEGATE=delegate;
    }
    public static MedicineOverdueRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                                DataConverter dataConverter, LatteDelegate delegate){
        return new MedicineOverdueRefreshHandler(swipeRefreshLayout,recyclerView,dataConverter,delegate);
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
    public void getMedicineOverdue(String url,String tel,String boxId){
        //BEAN.setDelayed(1000);
        RestClient.builder()
                .url(url)
                .params("tel",tel)
                .params("boxId", LattePreference.getBoxId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("overdue", response);
                        mAdapter=MultipleRecyclerAdapter.getMedicineOverdue(CONVERTER.setJsonData(response),DELEGATE);
                        RECYCLERVIEW.setAdapter(mAdapter);

                    }
                })
                .build()
                .get();
    }
    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMoreRequested() {

    }

}
