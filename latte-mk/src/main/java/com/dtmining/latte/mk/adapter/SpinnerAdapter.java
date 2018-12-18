package com.dtmining.latte.mk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * author:songwenming
 * Date:2018/12/18
 * Description:
 */
public class SpinnerAdapter<T> extends ArrayAdapter {
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        //返回数据的统计数量，大于0项则减去1项，从而不显示最后一项
        int i = super.getCount();
        return i > 0 ? i - 1 : i;


    }
}
