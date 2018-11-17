package com.dtmining.latte.mk.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * author:songwenming
 * Date:2018/11/17
 * Description:
 */
public class MyLayoutManager extends LinearLayoutManager {

    public MyLayoutManager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int itemCount = state.getItemCount() ;
        if(itemCount == 0) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return ;
        }
        View view = recycler.getViewForPosition(0);
        if(view != null){
            measureChild(view, widthSpec, heightSpec);
            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
            int measuredHeight = view.getMeasuredHeight();
            setMeasuredDimension(measuredWidth, measuredHeight);
        }
    }
}