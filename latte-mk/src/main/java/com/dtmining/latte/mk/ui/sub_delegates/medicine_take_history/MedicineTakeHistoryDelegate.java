package com.dtmining.latte.mk.ui.sub_delegates.medicine_take_history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.layoutmanager.MyLayoutManager;
import com.dtmining.latte.mk.main.index.IndexDataConverter;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.recycler.MyDecoration;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.util.storage.LattePreference;

import butterknife.BindView;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineTakeHistoryDelegate extends LatteDelegate {
    private RefreshHandler mRefreshHandler=null;
    @BindView(R2.id.srl_medicine_history)
    SwipeRefreshLayout mSwipeRefreshLayout=null;
    @BindView(R2.id.rv_medicine_history)
    RecyclerView mRecyclerView=null;
    String boxId=null;
    String tel=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_take_history;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRefreshHandler=RefreshHandler.create(mSwipeRefreshLayout,mRecyclerView,
                null,new IndexDataConverter(),this,null);
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId= LattePreference.getBoxId();
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());

        }
    }
    private void initRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setProgressViewOffset(true,120,300);
    }
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new MyDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        initRefreshLayout();
        mRefreshHandler.get_medicine_history("index",tel,boxId);
    }
}
