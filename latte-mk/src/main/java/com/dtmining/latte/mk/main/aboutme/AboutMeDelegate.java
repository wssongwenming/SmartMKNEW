package com.dtmining.latte.mk.main.aboutme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.mk.R;

/**
 * author:songwenming
 * Date:2018/10/10
 * Description:
 */
public class AboutMeDelegate extends BottomItemDelegate{
    @Override
    public Object setLayout() {
        return R.layout.delegate_about_me;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
