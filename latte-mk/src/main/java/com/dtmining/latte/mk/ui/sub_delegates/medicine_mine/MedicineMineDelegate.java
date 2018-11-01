package com.dtmining.latte.mk.ui.sub_delegates.medicine_mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;


import com.dtmining.latte.app.ConfigKeys;
import com.dtmining.latte.app.Latte;
import com.dtmining.latte.database.UserProfile;
import com.dtmining.latte.delegates.LatteDelegate;
import com.dtmining.latte.mk.R;
import com.dtmining.latte.mk.R2;
import com.dtmining.latte.mk.sign.SignInDelegate;
import com.dtmining.latte.mk.ui.recycler.DividerItemDecoration;
import com.dtmining.latte.mk.ui.recycler.MultipleItemEntity;
import com.dtmining.latte.mk.ui.recycler.MyDecoration;
import com.dtmining.latte.mk.ui.refresh.RefreshHandler;
import com.dtmining.latte.mk.ui.sub_delegates.SwipeListLayout;
import com.dtmining.latte.net.RestClient;
import com.dtmining.latte.net.callback.ISuccess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


/**
 * author:songwenming
 * Date:2018/10/19
 * Description:
 */
public class MedicineMineDelegate extends LatteDelegate {
    private String tel=null;
 /*   @BindView(R2.id.srl_medicine_mine)
    SwipeRefreshLayout mRefreshLayout=null;*/
    @BindView(R2.id.rv_medicine_mine)
    RecyclerView mRecylerView=null;
/*    @BindView(R2.id.ed_search_medicine)
    AppCompatEditText mEditText_Medicine_Name=null;
    @BindView(R2.id.btn_search_medicine)
    AppCompatButton mBtn_Search_Medicine=null;
    @BindView(R2.id.btn_scan_medicine)
    AppCompatButton mBtn_Scan_Medicine=null;
    @BindView(R2.id.tb_medicine_mine)*/
    Toolbar mToolbar=null;
    private RefreshHandler mRefreshHandler=null;
    private Set<SwipeListLayout> sets = new HashSet();
/*    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light

        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }*/

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        //initRefreshLayout();
        initRecyclerView();
        mRefreshHandler.firstPage_medicine_mine("medicine_mine");
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_medicine_mine;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRefreshHandler=RefreshHandler.create(null,mRecylerView,new MedincineMineDataConverter(),null,sets);
        UserProfile userProfile= (UserProfile) Latte.getConfigurations().get(ConfigKeys.LOCAL_USER);
        if(userProfile==null){
            startWithPop(new SignInDelegate());
        }else {
            tel=Long.toString(userProfile.getTel());
        }
/*        RestClient.builder()
                .url("medicine_mine")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final MedincineMineDataConverter converter=new MedincineMineDataConverter();
                        converter.setJsonData(response);
                        ArrayList<MultipleItemEntity> list=converter.convert();
                        list.get(0).getField("medicineName");
                        Toast.makeText(getContext(),list.get(0).getField(MedicineMineFields.MEDICINENAME).toString(),Toast.LENGTH_LONG).show();
                    }
                })
                .build()
                .get();*/

    }
    private void initRecyclerView(){
        final LinearLayoutManager manager=new LinearLayoutManager(getContext());
        mRecylerView.setLayoutManager(manager);
        mRecylerView.addItemDecoration(new MyDecoration(getContext(), DividerItemDecoration.VERTICAL));
/*        mRecylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    //当listview开始滑动时，若有item的状态为Open，则Close，然后移除
                    case SCROLL_STATE_IDLE:
                        if (sets.size() > 0) {
                            for (SwipeListLayout s : sets) {
                                s.setStatus(SwipeListLayout.Status.Close, true);
                                sets.remove(s);
                            }
                        }
                        break;

                }
            }
        });*/
    }

}