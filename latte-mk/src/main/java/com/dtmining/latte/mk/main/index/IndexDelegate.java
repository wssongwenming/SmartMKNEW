package com.dtmining.latte.mk.main.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.main.MkBottomDelegate;
import com.dtmining.latte.ui.recycler.BaseDecoration;
import com.dtmining.latte.ui.refresh.RefreshHandler;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/10/8
 * Description:
 */
public class IndexDelegate extends BottomItemDelegate {
    @BindView(R2.id.rv_index)
    RecyclerView mRecyclerView=null;
    @BindView(R2.id.srl_index)
    SwipeRefreshLayout mRefreshLayout=null;
    @BindView(R2.id.tb_index)
    Toolbar mToolbar=null;
    @BindView(R2.id.icon_index_scan)
    IconTextView mIconScan=null;
    @BindView(R2.id.et_search_view)
    AppCompatEditText mSearchView=null;
    private RefreshHandler mRefreshHandler=null;

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        Log.d("aa", "onBind: ");
        mRefreshHandler=RefreshHandler.create(mRefreshLayout,mRecyclerView,new IndexDataConverter());
    }
    private void initRefreshLayout(){
        final GridLayoutManager manager=new GridLayoutManager(getContext(),4);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(),R.color.app_background),5));
        final MkBottomDelegate mkBottomDelegate=getParentDelegate();
        //单击跳转，显示每个项目的详情
        mRecyclerView.addOnItemTouchListener(IndexItemClickListener.create(mkBottomDelegate));
    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        Log.d("aa", "onLazyInitView: ");
        super.onLazyInitView(savedInstanceState);
        initRefreshLayout();
        mRefreshHandler.firstPage("index");
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_index;
    }


}
