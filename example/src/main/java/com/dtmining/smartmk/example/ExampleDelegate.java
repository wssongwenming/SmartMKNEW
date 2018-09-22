package com.dtmining.smartmk.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dtmining.latte.delegates.LatteDelegate;

/**
 * author:songwenming
 * Date:2018/9/22
 * Description:
 */
public class ExampleDelegate extends LatteDelegate {
    @Override
    public Object setLayout() {
        return R.layout.delegate_example;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        //载入fragment后对每个控件做的操作
    }
}
