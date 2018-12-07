package com.dtmining.latte.mk.main.aboutme.usermessage;

import android.support.v4.widget.SwipeRefreshLayout;

import com.dtmining.latte.app.Latte;

/**
 * author:songwenming
 * Date:2018/12/7
 * Description:
 */
public class MessageRefreshHandler implements SwipeRefreshLayout.OnRefreshListener{
    private final SwipeRefreshLayout REFESH_LAYOUT;

    public MessageRefreshHandler(SwipeRefreshLayout refesh_layout) {
        REFESH_LAYOUT = refesh_layout;
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
