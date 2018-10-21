package com.dtmining.latte.mk.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class GoodsDetailDelegate extends LatteDelegate {
    public static GoodsDetailDelegate create(){
        return new GoodsDetailDelegate();
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_goods_detail;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }


}
