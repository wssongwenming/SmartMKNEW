package com.dtmining.latte.mk.main.aboutme.mymedicineboxes;

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
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.recycler.MyDecoration;

import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.medicine_mine.MedincineMineDataConverter;

import butterknife.BindView;

/**
 * author:songwenming
 * Date:2018/11/4
 * Description:
 */
public class MedicineBoxesMineDelegate extends LatteDelegate {
    @BindView(R2.id.rv_delegate_medicine_boxes_mine)
    RecyclerView mRecyclerView=null;
    @BindView(R2.id.srl_delegate_medicine_boxes_mine)
    SwipeRefreshLayout mRefreshLayout=null;
    String tel=null;
    private MedicineBoxesMineRefreshHandler mRefreshHandler=null;
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
    public Object setLayout() {
        return R.layout.delegate_medicine_boxes_mine;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRefreshHandler= MedicineBoxesMineRefreshHandler.create(mRefreshLayout,mRecyclerView,new MedicineBoxesMineDataConverter(),null);
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
    }

    private void initRecyclerView(){
        final LinearLayoutManager manager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new MyDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRefreshLayout();
        initRecyclerView();
        mRefreshHandler.firstPage_medicine_boxes("medicine_boxes");
    }
}
