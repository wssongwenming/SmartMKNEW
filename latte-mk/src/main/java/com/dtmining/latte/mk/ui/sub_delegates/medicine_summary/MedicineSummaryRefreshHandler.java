package com.dtmining.latte.mk.ui.sub_delegates.medicine_summary;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.dtmining.latte.app.Latte;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue.MedicineOverdueRefreshHandler;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

/**
 * author:songwenming
 * Date:2018/11/25
 * Description:
 */
public class MedicineSummaryRefreshHandler implements SwipeRefreshLayout.OnRefreshListener{
    private final SwipeRefreshLayout REFESH_LAYOUT;
    private final RecyclerView RECYCLERVIEW;
    private MultipleRecyclerAdapter mAdapter=null;
    private final DataConverter CONVERTER;
     public MedicineSummaryRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,RecyclerView RECYCLERVIEW, DataConverter CONVERTER) {
        this.RECYCLERVIEW = RECYCLERVIEW;
        this.CONVERTER = CONVERTER;
         this.REFESH_LAYOUT=swipeRefreshLayout;
         if(REFESH_LAYOUT!=null) {
             REFESH_LAYOUT.setOnRefreshListener(this);
         }

    }
    public static MedicineSummaryRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                                       DataConverter dataConverter){
        return new MedicineSummaryRefreshHandler(swipeRefreshLayout,recyclerView,dataConverter);
    }

    public void getMedicineSummary(String url,String tel,String begin,String end){
        //BEAN.setDelayed(1000);

        RestClient.builder()
                .url(url)
                .params("tel",tel)
                .params("begin_time",begin)
                .params("end_time",end)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        mAdapter= MultipleRecyclerAdapter.getMedicineSummary(CONVERTER.setJsonData(response),null);
                        RECYCLERVIEW.setAdapter(mAdapter);

                    }
                })
                .build()
                .get();
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
}
