package com.dtmining.latte.mk.main.aboutme.boxdelete;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.dtmining.latte.app.Latte;
import com.dtmining.latte.mk.ui.recycler.DataConverter;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MultipleRecyclerAdapter;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_summary.MedicineSummaryRefreshHandler;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/12/13
 * Description:
 */
public class BoxDeleteRefreshHandler implements SwipeRefreshLayout.OnRefreshListener{

    private final SwipeRefreshLayout REFESH_LAYOUT;
    private final RecyclerView RECYCLERVIEW;
    private MultipleRecyclerAdapter mAdapter=null;
    private final BoxListDataConverter CONVERTER;
    public BoxDeleteRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,RecyclerView RECYCLERVIEW, BoxListDataConverter CONVERTER) {
        this.RECYCLERVIEW = RECYCLERVIEW;
        this.CONVERTER = CONVERTER;
        this.REFESH_LAYOUT=swipeRefreshLayout;
        if(REFESH_LAYOUT!=null) {
            REFESH_LAYOUT.setOnRefreshListener(this);
        }

    }
    public static BoxDeleteRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                                 BoxListDataConverter dataConverter){
        return new BoxDeleteRefreshHandler(swipeRefreshLayout,recyclerView,dataConverter);
    }

    public void getBoxList(String url,String tel){
        //BEAN.setDelayed(1000);

        RestClient.builder()
                .url(url)
                .params("tel",tel)
                 .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ArrayList<MultipleItemEntity> boxList=new ArrayList<>();

                        boxList.addAll(CONVERTER.convertResponse(response));
                        Log.d("boxlist", boxList.toString());
                        mAdapter= MultipleRecyclerAdapter.create(boxList,null);
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
