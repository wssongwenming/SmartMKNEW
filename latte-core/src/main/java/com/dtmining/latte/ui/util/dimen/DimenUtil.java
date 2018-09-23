package com.dtmining.latte.ui.util.dimen;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.dtmining.latte.app.Latte;

/**
 * author:songwenming
 * Date:2018/9/23
 * Description:
 */
public class DimenUtil {
    public static int getScreenWidth(){
        final Resources resources= Latte.getApplication().getResources();
        final DisplayMetrics dm=resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(){
        final Resources resources=Latte.getApplication().getResources();
        final DisplayMetrics dm=resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
