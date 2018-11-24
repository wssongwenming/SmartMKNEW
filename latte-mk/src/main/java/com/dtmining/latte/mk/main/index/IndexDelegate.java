package com.dtmining.latte.mk.main.index;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
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
import com.dtmining.latte.mk.ui.sub_delegates.hand_add.HandAddDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedicineMineDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history.MedicineTakeHistoryDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicinePlan;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_take_plan.MedicineTakePlanDelegate;
import com.dtmining.latte.mk.ui.sub_delegates.views.MyGridView;
import com.dtmining.latte.mk.ui.sub_delegates.views.SwipeListLayout;
import com.dtmining.latte.ui.launcher.LauncherHolderCreator;
import com.dtmining.latte.util.callback.CallbackManager;
import com.dtmining.latte.util.callback.CallbackType;
import com.dtmining.latte.util.callback.IGlobalCallback;
import com.dtmining.latte.util.storage.LattePreference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author:songwenming
 * Date:2018/10/8
 * Description:
 */
public class IndexDelegate extends BottomItemDelegate {


    @BindView(R2.id.banner_index)
    ConvenientBanner mConvenientBanner=null;
    @BindView(R2.id.grid_photo_index)
    MyGridView buttonGrid=null;
    @BindView(R2.id.rv_index_history)
    RecyclerView mRecyclerViewHistory=null;
    @BindView(R2.id.elv_index_plan)
    ExpandableListView mExpandableListView=null;
    @BindView(R2.id.srl_index)
    SwipeRefreshLayout mRefreshLayout=null;
    @OnClick(R2.id.tv_more_medicine_history_index)
    void moreHistory(){
        getParentDelegate().start(new MedicineTakeHistoryDelegate());
    }
    @OnClick(R2.id.tv_more_medicine_plan_index)
    void morePlan(){
        getParentDelegate().start(new MedicineTakePlanDelegate());
    }
    String tel=null;
    String boxId=null;
    private LinkedList<Icon> mData;
    private MyAdapter<Icon> mAdapter;
    private RefreshHandler mRefreshHandler=null;
    private Set<SwipeListLayout> sets = new HashSet();
    //private ConvenientBanner<Integer> mConvenientBanner=null;
    private static final ArrayList<Integer> INTEGERS=new ArrayList<>();

    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_red_light
        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId=LattePreference.getBoxId();
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
                        HandAddDelegate delegate=HandAddDelegate.newInstance(args.toString());
                        start(delegate);

                    }
                })
        .addCallback(CallbackType.ON_BIND_BOXID, new IGlobalCallback() {
            @Override
            public void executeCallback(@Nullable Object args) {
                Toast.makeText(getContext(),"boxId="+ LattePreference.getBoxId(),Toast.LENGTH_LONG).show();
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
        mData.add(new Icon(R.mipmap.main_health, "身体状况"));
        mAdapter = new MyAdapter<Icon>(mData, R.layout.item_grid_icon) {
            @Override
            public void bindView(ViewHolder holder, Icon icon) {
                holder.setImageResource(R.id.img_icon, icon.getiId());
                holder.setText(R.id.txt_icon,icon.getiName());
            }
        };
        buttonGrid.setAdapter(mAdapter);
        buttonGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://点击了“扫码添加”
                        getParentDelegate().startScanWithCheck(getParentDelegate());
                        break;
                    case 1://点击了“手动添加”
                        getParentDelegate().start(new HandAddDelegate());
                        break;
                    case 2://点击了“我的药品”
                        getParentDelegate().start(new MedicineMineDelegate());
                        break;
                    case 3://点击了“用药计划”
                        getParentDelegate().start(new MedicineTakePlanDelegate());
                        break;
                    case 4://点击了“用药记录”
                        getParentDelegate().start(new MedicineTakeHistoryDelegate());
                        break;
                }
            }
        });
    }
    private void initBanner(){
        INTEGERS.add(R.mipmap.banner_01);
        INTEGERS.add(R.mipmap.banner_02);
        INTEGERS.add(R.mipmap.banner_03);
        mConvenientBanner
                .setPages(new LauncherHolderCreator(),INTEGERS)
                .setPageIndicator(new int[]{R.drawable.dot_normal,R.drawable.dot_focus})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setCanLoop(false);
    }
    private void initRecyclerView(){
        final LinearLayoutManager linearLayoutManager_history=new MyLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerViewHistory.setLayoutManager(linearLayoutManager);
    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(),mExpandableListView.toString(),Toast.LENGTH_LONG).show();
        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        initGridView();
        initRefreshLayout();
        initBanner();
        mRefreshHandler.firstPage_medicine_history("index",tel);
        mRefreshHandler.get_medicine_plan("medicine_plan",tel);
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_index;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
