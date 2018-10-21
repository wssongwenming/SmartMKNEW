package com.dtmining.latte.mk.main.aboutme.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;


/**
 * Created by 傅令杰
 */

public class NameDelegate extends LatteDelegate {
    @Override
    public Object setLayout() {
        return R.layout.delegate_name;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

    }
}
