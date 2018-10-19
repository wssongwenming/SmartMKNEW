package com.dtmining.latte.ui.recycler;


import android.support.annotation.ColorInt;

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
}
