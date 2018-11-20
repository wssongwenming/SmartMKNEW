package com.dtmining.latte.mk.ui.sub_delegates.medicine_overdue;

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
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.util.storage.LattePreference;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/11/19
 * Description:
 */
public class MedicineOverdueDelegate extends LatteDelegate {
    String boxId=null;
    String tel=null;
    private MedicineOverdueRefreshHandler mRefreshHandler=null;
    @BindView(R2.id.srl_medicine_over_due)
    SwipeRefreshLayout mRefreshLayout=null;
    @BindView(R2.id.rv_index_medicine_over_due)
    RecyclerView mRecyclerView=null;
    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_over_due;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        boxId= LattePreference.getBoxId();
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());

        }
        mRefreshHandler= MedicineOverdueRefreshHandler.create(mRefreshLayout,mRecyclerView,new MedicineOverdueDataConverter(),this.getParentDelegate());
    }
    private void initRecyclerView() {
        final LinearLayoutManager linearLayoutManager_history=new MyLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }
    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_red_light,
                android.R.color.holo_red_light
        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRecyclerView();
        initRefreshLayout();
        mRefreshHandler.getMedicineOverdue("medicine_overdue",tel,boxId);
    }
}
