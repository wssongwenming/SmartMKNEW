package com.dtmining.latte.ui.recycler;


import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.choices.divider.DividerItemDecoration;
/**
 * author:songwenming
 * Date:2018/9/21
 * Description:
 */
public class BaseDecoration extends DividerItemDecoration {
    private BaseDecoration(@ColorInt int color, int size){
        setDividerLookup(new DividerLookupImpl(color,size));
    }
    public static BaseDecoration create(@ColorInt int color, int size){
        return new BaseDecoration(color,size);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //super.getItemOffsets(outRect, view, parent, state);
        int childAdapterPosition = parent.getChildAdapterPosition(view);

        int lastCount = parent.getAdapter().getItemCount() - 1;

        if (childAdapterPosition == 0) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (childAdapterPosition == 11) {
            outRect.set(0, 0, 0, 1);
            return;
        }

        if (childAdapterPosition == lastCount) {
            outRect.set(0, 0, 0, 1);
            return;
        }

    }
}
