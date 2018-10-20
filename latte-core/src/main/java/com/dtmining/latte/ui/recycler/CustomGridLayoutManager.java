package com.dtmining.latte.ui.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * author:songwenming
 * Date:2018/10/20
 * Description:
 */
public class CustomGridLayoutManager extends android.support.v7.widget.GridLayoutManager {

    public CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        int count = state.getItemCount();
        Log.d("count", count+"");
        if (count > 0) {
            if(count>10){
                count =10;
            }
            int realHeight = 0;
            int realWidth = 0;
            for(int i = 5;i < count; i++){
                View view = recycler.getViewForPosition(16);
                if (view != null) {
                    measureChild(view, widthSpec, heightSpec);
                    int measuredWidth = View.MeasureSpec.getSize(widthSpec);
                    int measuredHeight = view.getMeasuredHeight();
                    realWidth = realWidth > measuredWidth ? realWidth : measuredWidth;
                    realHeight += measuredHeight;
                }

            }
            setMeasuredDimension(50, 60);
        } else {
            super.onMeasure(recycler, state, 50, 60);
        }

    }
}
