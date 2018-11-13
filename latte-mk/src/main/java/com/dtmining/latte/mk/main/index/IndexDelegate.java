package com.dtmining.latte.mk.main.index;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;

import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.CustomGridLayoutManager;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;

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
 /*   @BindView(R2.id.tb_index)
    Toolbar mToolbar=null;
    @BindView(R2.id.icon_index_scan)
    IconTextView mIconScan=null;
    @BindView(R2.id.et_search_view)
    AppCompatEditText mSearchView=null;*/
    private RefreshHandler mRefreshHandler=null;
    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_red_light
        );
        //
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_SCAN, new IGlobalCallback() {
                    @Override
                    public void executeCallback(@Nullable Object args){
                        Toast.makeText(getContext(),"扫描到的二维码"+args,Toast.LENGTH_LONG).show();

                    }
                })
        .addCallback(CallbackType.ON_BIND_BOXID, new IGlobalCallback() {
            @Override
            public void executeCallback(@Nullable Object args) {
                Toast.makeText(getContext(),"boxId="+ LattePreference.getBoxId("boxId"),Toast.LENGTH_LONG).show();
            }
        });

        mRefreshHandler=RefreshHandler.create(mRefreshLayout,mRecyclerView,new IndexDataConverter(),this.getParentDelegate(),null);
        //final MkBottomDelegate mkBottomDelegate=getParentDelegate();
        //单击跳转，显示每个项目的详情
        //mRecyclerView.addOnItemTouchListener(IndexItemClickListener.create(mkBottomDelegate));
    }

    private void initRecyclerView() {
        //final GridLayoutManager manager = new CustomGridLayoutManager(getContext(), 3);
        final GridLayoutManager manager=new GridLayoutManager(getContext(),3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        //(BaseDecoration.create(ContextCompat.getColor(getContext(),R.color.main_orange_color),0));
        //final EcBottomDelegate ecBottomDelegate=getParentDelegate();
        //单击跳转，显示每个项目的详情
        //mRecylerView.addOnItemTouchListener(IndexItemClickListener.create(ecBottomDelegate));
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //设置recyclerView高度
                ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
                if (Build.VERSION.SDK_INT >= 16) {
                    mRecyclerView.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    mRecyclerView.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }

                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                int height = wm.getDefaultDisplay().getWidth() / 2;
                if (mRecyclerView.getHeight() < height && mRecyclerView.getHeight() > wm.getDefaultDisplay().getWidth() / 3) {
                    layoutParams.height = 480;
                } else {
                    layoutParams.height = 300;
                }
                mRecyclerView.setLayoutParams(layoutParams);

            }
        });
    }


    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {

        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        initRefreshLayout();
        mRefreshHandler.firstPage_medicine_history("index");
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_index;
    }


}
