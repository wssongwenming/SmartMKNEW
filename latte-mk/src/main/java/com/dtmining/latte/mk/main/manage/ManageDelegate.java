package com.dtmining.latte.mk.main.manage;

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
public class ManageDelegate extends BottomItemDelegate {
    @Override
    public Object setLayout() {
        return R.layout.delegate_manage;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
