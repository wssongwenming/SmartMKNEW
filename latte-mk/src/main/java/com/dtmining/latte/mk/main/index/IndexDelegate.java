package com.dtmining.latte.mk.main.index;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.bottom.BottomItemDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.adapter.MyAdapter;
import com.dtmining.latte.mk.layoutmanager.MyLayoutManager;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.tools.Icon;
import com.dtmining.latte.mk.ui.recycler.CustomGridLayoutManager;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.views.MyGridView;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/10/8
 * Description:
 */
public class IndexDelegate extends BottomItemDelegate {

    @BindView(R2.id.grid_photo_index)
    MyGridView buttonGrid=null;
    @BindView(R2.id.rv_index_history)
    RecyclerView mRecyclerViewHistory=null;
    @BindView(R2.id.elv_index_plan)
    ExpandableListView mExpandableListView=null;
    @BindView(R2.id.srl_index)
    SwipeRefreshLayout mRefreshLayout=null;
    String tel=null;
    private LinkedList<Icon> mData;
    private MyAdapter<Icon> mAdapter;
    private RefreshHandler mRefreshHandler=null;
    private Set<SwipeListLayout> sets = new HashSet();
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
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());

        }
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

        mRefreshHandler=RefreshHandler.create(mRefreshLayout,mRecyclerViewHistory,
                mExpandableListView,new IndexDataConverter(),this.getParentDelegate(),null);
        //final MkBottomDelegate mkBottomDelegate=getParentDelegate();
        //单击跳转，显示每个项目的详情
        //mRecyclerView.addOnItemTouchListener(IndexItemClickListener.create(mkBottomDelegate));
    }
    private void initGridView(){
        mData = new LinkedList<>();
        mData.add(new Icon(R.mipmap.icon_medicine_scan_add, "扫码添加"));

        mData.add(new Icon(R.mipmap.icon_medicine_hand_add, "手动添加"));
        mData.add(new Icon(R.mipmap.icon_mdicine_mine, "我的药品"));
        mData.add(new Icon(R.mipmap.icon_medicine_take_plan, "用药计划"));
        mData.add(new Icon(R.mipmap.icon_medicine_take_plan, "用药记录"));
        mAdapter = new MyAdapter<Icon>(mData, R.layout.item_grid_icon) {
            @Override
            public void bindView(ViewHolder holder, Icon icon) {
                holder.setImageResource(R.id.img_icon, icon.getiId());
                holder.setText(R.id.txt_icon,icon.getiName());
            }
        };
        buttonGrid.setAdapter(mAdapter);
    }
    private void initRecyclerView() {

        final GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),3);
        final LinearLayoutManager linearLayoutManager_history=new MyLayoutManager(getContext());
        mRecyclerViewHistory.setLayoutManager(linearLayoutManager_history);

        //单击跳转，显示每个项目的详情
        //mRecylerView.addOnItemTouchListener(IndexItemClickListener.create(ecBottomDelegate));
    }


    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(),mExpandableListView.toString(),Toast.LENGTH_LONG).show();
        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        initGridView();
        initRefreshLayout();

        mRefreshHandler.firstPage_medicine_history("index",tel);
        mRefreshHandler.get_medicine_plan("medicine_plan",tel);


    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_index;
    }


}
