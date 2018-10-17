package com.dtmining.latte.mk.main.index;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.detail.GoodsDetailDelegate;

public class IndexItemClickListener extends SimpleClickListener {
    private final LatteDelegate DELEGATE;

    private IndexItemClickListener(LatteDelegate delegate) {
        this.DELEGATE = delegate;
    }
    public static SimpleClickListener create(LatteDelegate delegate)
    {
        return new IndexItemClickListener(delegate);
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        final GoodsDetailDelegate delegate= GoodsDetailDelegate.create();
        DELEGATE.start(delegate);

    }

    @Override
    public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

    }

    @Override
    public void onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

    }
}
