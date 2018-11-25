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
public class MedicineSummaryRefreshHandler {
    private final RecyclerView RECYCLERVIEW;
    private MultipleRecyclerAdapter mAdapter=null;
    private final DataConverter CONVERTER;
     public MedicineSummaryRefreshHandler(SwipeRefreshLayout REFESH_LAYOUT,RecyclerView RECYCLERVIEW, DataConverter CONVERTER) {
        this.RECYCLERVIEW = RECYCLERVIEW;
        this.CONVERTER = CONVERTER;

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

}
